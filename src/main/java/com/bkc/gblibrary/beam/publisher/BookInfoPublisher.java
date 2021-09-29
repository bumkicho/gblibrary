package com.bkc.gblibrary.beam.publisher;

import java.util.Comparator;
import java.util.List;

import org.apache.beam.runners.dataflow.DataflowRunner;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;

import com.bkc.gblibrary.model.BookInfo;

public class BookInfoPublisher {

	public static void publishNewBook(List<BookInfo> newBooks) {
		
		BookInfo book = newBooks.stream().max(Comparator.comparingLong(BookInfo::getId)).get();
		
		DataflowPipelineOptions pipelineOptions = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
		pipelineOptions.setJobName("publishbook"+book.getGbId());
		pipelineOptions.setProject("gblibrary");
		pipelineOptions.setRegion("us-central1");
		pipelineOptions.setRunner(DataflowRunner.class);
		pipelineOptions.setGcpTempLocation("gs://gblibrary_bucket//temp");
		
		Pipeline dataFlowPipeline = Pipeline.create(pipelineOptions);
		
		//List<String> bookInfoList = Arrays.asList(book.getGbId().toString(), book.getTitle(), book.getAuthor().toString(), book.getBookURL().toString(), book.getCatalog().toString());

		dataFlowPipeline.apply(Create.of(newBooks))
				.apply(ParDo.of(new BookInfoMessage()))
				.apply(PubsubIO.writeStrings().to("projects/gblibrary/topics/newbook"));

		dataFlowPipeline.run().waitUntilFinish();
		
	}
	
	public static class BookInfoMessage extends DoFn<BookInfo, String> {
		
		@ProcessElement
		public void process(@Element BookInfo bookInfo, OutputReceiver<String> out) {
			out.output(bookInfo.getGbId()+";"+bookInfo.getTitle()+";"+bookInfo.getAuthor()+";"+bookInfo.getCatalog().getName());
		}
		
	}
}
