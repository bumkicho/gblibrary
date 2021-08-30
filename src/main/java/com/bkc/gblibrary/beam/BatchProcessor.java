package com.bkc.gblibrary.beam;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.metrics.Counter;
import org.apache.beam.sdk.metrics.Metrics;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.io.jdbc.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class BatchProcessor {
	
	@Value("${spring.datasource.driver-class-name}")
	private String db_driver;
	
	@Value("${spring.datasource.url}")
	private String db_url;
	
	@Value("${spring.datasource.username}")
	private String db_user;
	
	@Value("${spring.datasource.password}")
	private String db_pwd;

	public void genearteWordCount(String dest, String fileName, Long bookId) {
		PipelineOptions pipelineOptions = PipelineOptionsFactory.create();
		Pipeline pipeline = Pipeline.create(pipelineOptions);

		pipeline.apply(TextIO.read().from(dest + File.separatorChar + fileName))
				.apply(ParDo.of(new ExtractWordsFn()))
				.apply(Count.perElement())
				.apply(JdbcIO.<KV<String, Long>>write()
					      .withDataSourceConfiguration(JdbcIO.DataSourceConfiguration.create(
					    		  db_driver, db_url)
					          .withUsername(db_user)
					          .withPassword(db_pwd))
					      .withStatement("insert into gblibrary.book_info_detail (gb_book_id, word, word_count) values(?, ?, ?)")
					      .withPreparedStatementSetter(
					              (element, statement) -> {
					            	  statement.setLong(1, bookId);
					            	  statement.setString(2, element.getKey());
					            	  statement.setLong(3, element.getValue());
					              }));

		pipeline.run().waitUntilFinish();
	}

	public static class ExtractWordsFn extends DoFn<String, String> {

		private static final long serialVersionUID = 1L;

		private final Counter emptyLines = Metrics.counter(ExtractWordsFn.class, "emptyLines");
		private final ArrayList<String> WORDSTOSKIP = new ArrayList<String>(Arrays.asList("the","The"));

		@ProcessElement
		public void processElement(ProcessContext c) {
			if (c.element().trim().isEmpty()) {
				emptyLines.inc();
			}

			// Split the line into words.
			String[] words = c.element().split("[^\\p{L}]+");
			for (String word : words) {
				if (!word.isEmpty() && word.length()>=3 && !WORDSTOSKIP.contains(word)) {
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
