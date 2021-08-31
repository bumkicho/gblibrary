package com.bkc.gblibrary.beam;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.metrics.Counter;
import org.apache.beam.sdk.metrics.Metrics;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.io.jdbc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import com.bkc.gblibrary.model.BookInfo;
import com.bkc.gblibrary.model.Catalog;
import com.bkc.gblibrary.repository.BookInfoRepository;
import com.bkc.gblibrary.repository.CatalogRepository;
import com.bkc.gblibrary.repository.StopWordRepository;
import com.bkc.gblibrary.service.WordService;
import com.bkc.gblibrary.utility.FileUtilities;

/**
 * 
 * @author bumki
 *
 */

@Component
public class BookInfoDetailProcessor {
	
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

	public void processThroughBookInfoByCatalog(String catalogName, String range) {
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
				new DownloadBookForDetail(beanFactory)));
		
		pipeline.run().waitUntilFinish();

		pipeline
		.apply(Create.of(bookInfoList))
		.apply(ParDo.of(
			new ProcessBookForDetail(beanFactory, ignoredWords)));
		
		pipeline.run().waitUntilFinish();
		
		
	}
	
	public static class DownloadBookForDetail extends DoFn<BookInfo, BookInfo> {
		private static final long serialVersionUID = 1L;

		private AutowireCapableBeanFactory beanFactory;
		
		private FileUtilities fileUtil;

		public DownloadBookForDetail(AutowireCapableBeanFactory beanFactory) {
			this.beanFactory = beanFactory;
		}
		
		@ProcessElement
		public void process(@Element BookInfo bookInfo) {
			String fileURL = bookInfo.getBookURL();
			String fileName = fileURL.substring(fileURL.lastIndexOf("/")+1, fileURL.length());
			String fileLink = fileURL.substring(0, fileURL.lastIndexOf("/"));
			String dest = "D:\\Temp";
			
			this.fileUtil = getFileUtilities();
			
			try {
				fileUtil.downloadFile(fileLink, fileName, dest, false, false);
			} catch (Exception e) {
				//ignore for now
				//e.printStackTrace();
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

		public ProcessBookForDetail(AutowireCapableBeanFactory beanFactory, List<String> ignoredWords) {
			this.beanFactory = beanFactory;
			this.ignoredWords = ignoredWords;
		}
		
		@ProcessElement
		public void process(@Element BookInfo bookInfo) {
			String fileURL = bookInfo.getBookURL();
			String fileName = fileURL.substring(fileURL.lastIndexOf("/")+1, fileURL.length());
			String fileLink = fileURL.substring(0, fileURL.lastIndexOf("/"));
			String dest = "D:\\Temp";
			
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
            e.printStackTrace();
        }finally {
            try {   
                stmt.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
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
