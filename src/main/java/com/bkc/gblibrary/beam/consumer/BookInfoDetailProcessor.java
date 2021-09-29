package com.bkc.gblibrary.beam.consumer;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.apache.beam.runners.dataflow.DataflowRunner;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.metrics.Counter;
import org.apache.beam.sdk.metrics.Metrics;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.KV;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.apache.beam.sdk.io.jdbc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import com.bkc.gblibrary.model.BookInfo;
import com.bkc.gblibrary.model.Catalog;
import com.bkc.gblibrary.repository.BookInfoRepository;
import com.bkc.gblibrary.repository.CatalogRepository;
import com.bkc.gblibrary.service.WordService;
import com.bkc.gblibrary.utility.FileUtilities;

/**
 * 
 * @author bumki
 *
 */

@Component
public class BookInfoDetailProcessor {
	
	private static final Logger log = LogManager.getLogger(BookInfoDetailProcessor.class);
	
	@Value("${spring.datasource.driver-class-name}")
	private String db_driver;
	
	@Value("${spring.datasource.url}")
	private String db_url;
	
	@Value("${spring.datasource.username}")
	private String db_user;
	
	@Value("${spring.datasource.password}")
	private String db_pwd;
	
	@Autowired
	private CatalogRepository catalogRepository;
	
	@Autowired
	private BookInfoRepository bookInfoRepository;
	
	private @Autowired AutowireCapableBeanFactory beanFactory;
	
	@Autowired
	private WordService wordService;
	
	private List<String> ignoredWords;

	public void processThroughBookInfoByCatalog(String catalogName, String range, String downloadFolder) {
		Optional<Catalog> catalog = catalogRepository.findByName(catalogName);
		if(!catalog.isPresent()) return;
		
		int minId;
		int maxId;

		if(range!=null && !range.isEmpty()) {
			minId = Integer.parseInt(range.substring(0, range.indexOf("-")));
			maxId = Integer.parseInt(range.substring(range.indexOf("-")+1));
		} else {
			minId = 0;
			maxId = 0;
		}
		
		ignoredWords = wordService.getAllStopWords();

		List<BookInfo> bookInfoList = bookInfoRepository.findByCatalog(catalog.get())
				.stream().filter(bookInfo -> (Integer.parseInt(bookInfo.getGbId())>=minId && Integer.parseInt(bookInfo.getGbId())<maxId))
				.collect(Collectors.toList());
		
		PipelineOptions pipelineOptions = PipelineOptionsFactory.create();
		Pipeline pipeline = Pipeline.create(pipelineOptions);
		
		pipeline
		.apply(Create.of(bookInfoList))
		.apply(ParDo.of(
				new DownloadBookForDetail(beanFactory, downloadFolder)));
		
		pipeline.run().waitUntilFinish();

		pipeline
		.apply(Create.of(bookInfoList))
		.apply(ParDo.of(
			new ProcessBookForDetail(beanFactory, ignoredWords, downloadFolder)));
		
		pipeline.run().waitUntilFinish();

	}

	public static class DownloadBookForDetail extends DoFn<BookInfo, BookInfo> {
		private static final long serialVersionUID = 1L;

		private AutowireCapableBeanFactory beanFactory;
		
		private FileUtilities fileUtil;
		
		private String downloadFolder;

		public DownloadBookForDetail(AutowireCapableBeanFactory beanFactory, String downloadFolder) {
			this.beanFactory = beanFactory;
			this.downloadFolder = downloadFolder;
		}
		
		@ProcessElement
		public void process(@Element BookInfo bookInfo) {
			String fileURL = bookInfo.getBookURL();
			String fileName = fileURL.substring(fileURL.lastIndexOf("/")+1, fileURL.length());
			String fileLink = fileURL.substring(0, fileURL.lastIndexOf("/"));
			String dest = downloadFolder;
			
			this.fileUtil = getFileUtilities();
			
			try {
				fileUtil.downloadFile(fileLink, fileName, dest, false, false);
			} catch (Exception e) {
				log.error(e.getMessage());
			}		
		}
		
		@Autowired
		private FileUtilities getFileUtilities() {
			return this.beanFactory.createBean(FileUtilities.class);
		}
	}

	public static class ProcessBookForDetail extends DoFn<BookInfo, String> {
		private static final long serialVersionUID = 1L;

		private AutowireCapableBeanFactory beanFactory;
		
		private List<String> ignoredWords;

		private BookInfoDetailProcessor bookInfoDetailProcessor;
		
		private String downloadFolder;

		public ProcessBookForDetail(AutowireCapableBeanFactory beanFactory, List<String> ignoredWords, String downloadFolder) {
			this.beanFactory = beanFactory;
			this.ignoredWords = ignoredWords;
			this.downloadFolder = downloadFolder;
		}
		
		@ProcessElement
		public void process(@Element BookInfo bookInfo) {
			String fileURL = bookInfo.getBookURL();
			String fileName = fileURL.substring(fileURL.lastIndexOf("/")+1, fileURL.length());
			String fileLink = fileURL.substring(0, fileURL.lastIndexOf("/"));
			String dest = downloadFolder;
			
			bookInfoDetailProcessor = getBookInfoDetailProcessor();
			bookInfoDetailProcessor.genearteWordCount(dest, fileName, bookInfo.getId(), ignoredWords);			
		}
		
		@Autowired
		private BookInfoDetailProcessor getBookInfoDetailProcessor() {
			return beanFactory.createBean(BookInfoDetailProcessor.class);
		}

	}

	public void genearteWordCount(String dest, String fileName, Long bookId, List<String> ignoredWords) {
		
		if(!(new File(dest+File.separatorChar+fileName)).exists()) {
			return;
		}
		
		deleteFromTable(bookId);
		
		PipelineOptions pipelineOptions = PipelineOptionsFactory.create();
		Pipeline pipeline = Pipeline.create(pipelineOptions);

		pipeline.apply(TextIO.read().from(dest + File.separatorChar + fileName))
				.apply(ParDo.of(new ExtractWordsFn(beanFactory, ignoredWords)))
				.apply(Count.perElement())
				.apply(JdbcIO.<KV<String, Long>>write()
					      .withDataSourceConfiguration(JdbcIO.DataSourceConfiguration.create(
					    		  db_driver, db_url)
					          .withUsername(db_user)
					          .withPassword(db_pwd))
					      .withStatement("insert into gblibrary.book_info_detail (book_id, word, word_count) values(?, ?, ?)")
					      .withPreparedStatementSetter(
					              (element, statement) -> {
					            	  statement.setLong(1, bookId);
					            	  statement.setString(2, element.getKey());
					            	  statement.setLong(3, element.getValue());
					              }));

		pipeline.run().waitUntilFinish();
		
		writeToDataStorage(dest, fileName, ignoredWords);

	}
	
	private void writeToDataStorage(String dest, String fileName, List<String> ignoredWords) {
		
		/*
		 * Write ingoredWords to google data storage
		 * DataflowPipeline appears to be super slow
		 * Even just writing out ignored words takes awhile.
		 */
		DataflowPipelineOptions dataFlowPipelineOptions = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
		
		//dataFlowPipelineOptions.setJobName("optional and needs to be unique");
		dataFlowPipelineOptions.setProject("gblibrary");
		dataFlowPipelineOptions.setRegion("us-central1");
		dataFlowPipelineOptions.setRunner(DataflowRunner.class);
		dataFlowPipelineOptions.setGcpTempLocation("gs://gblibrary_bucket//temp");
		
		Pipeline dataFlowPipeline = Pipeline.create(dataFlowPipelineOptions);

		dataFlowPipeline.apply(Create.of(ignoredWords))
				.apply(TextIO.write().to("gs://gblibrary_bucket//ignoredWords").withSuffix(".txt"));

		dataFlowPipeline.run().waitUntilFinish();
		
		/*
		 * Write word counts to google data storage via direct runner
		 */
		PipelineOptions dirPipelineOptions = PipelineOptionsFactory.create();
		Pipeline dirPipeline = Pipeline.create(dirPipelineOptions);
		
		dirPipeline.apply(TextIO.read().from(dest + File.separatorChar + fileName))
			.apply(ParDo.of(new ExtractWordsFn(beanFactory, ignoredWords)))
			.apply(Count.perElement())
			.apply(MapElements.via(new FormatAsText()))
			.apply(TextIO.write().to("gs://gblibrary_bucket//"+fileName).withSuffix(".txt"));
		
		dirPipeline.run().waitUntilFinish();
		
	}
	
	public static class FormatAsText extends SimpleFunction<KV<String, Long>, String> {
		@Override
		public String apply(KV<String, Long> input) {
			return input.getKey()+": "+input.getValue();
		}
	}

	private void deleteFromTable(Long bookId) {
		Connection connection = null;
        Statement stmt = null;
        try
        {
            Class.forName(db_driver);
            connection = DriverManager.getConnection(db_url, db_user, db_pwd);
             
            stmt = connection.createStatement();
            stmt.execute("delete from gblibrary.book_info_detail where book_id = " + bookId);
        } 
        catch (Exception e) {
            log.error(e.getMessage());
        }finally {
            try {   
                stmt.close();
                connection.close();
            } catch (Exception e) {
            	log.error(e.getMessage());
            }
        }
	}

	public static class ExtractWordsFn extends DoFn<String, String> {

		private static final long serialVersionUID = 1L;
		
		private AutowireCapableBeanFactory beanFactory;			

		private final Counter emptyLines = Metrics.counter(ExtractWordsFn.class, "emptyLines");
		private List<String> WORDSTOSKIP;

		public ExtractWordsFn(AutowireCapableBeanFactory beanFactory, List<String> ignoredWords) {
			this.beanFactory = beanFactory;
			this.WORDSTOSKIP = ignoredWords;
		}

		@ProcessElement
		public void processElement(ProcessContext c) {
			if(WORDSTOSKIP==null || WORDSTOSKIP.isEmpty()) {
				WORDSTOSKIP = new ArrayList<String>();
			}
			
			if (c.element().trim().isEmpty()) {
				emptyLines.inc();
			}

			// Split the line into words.
			String[] words = c.element().split("[^\\p{L}]+");
			for (String word : words) {
				if (!word.isEmpty() && word.length()>=3 && !WORDSTOSKIP.contains(word.toUpperCase())) {
					c.output(word);
				}
			}
		}

	}

	public static class ProcessBookInfo extends SimpleFunction<KV<String, Long>, String> {

		private static final long serialVersionUID = 1L;

		@Override
		public String apply(KV<String, Long> input) {
			return input.getKey() + ": " + input.getValue();
		}
	}

}
