package com.bkc.gblibrary.beam.consumer;

import org.apache.beam.runners.dataflow.DataflowRunner;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;

import com.google.api.services.bigquery.model.TableRow;

public class BookInfoConsumer {

	public static void processBookInfoMessage() {
		
		DataflowPipelineOptions pipelineOptions = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
		pipelineOptions.setJobName("injestBookInfoMessage");
		pipelineOptions.setProject("gblibrary");
		pipelineOptions.setRegion("us-central1");
		pipelineOptions.setRunner(DataflowRunner.class);
		pipelineOptions.setGcpTempLocation("gs://gblibrary_bucket//temp");
		
		Pipeline dfPipeline = Pipeline.create(pipelineOptions);
		
		PCollection<String> pubsubBookInfoMessage = dfPipeline.apply(PubsubIO.readStrings().fromTopic("projects/gblibrary/topics/newbook"));
		PCollection<TableRow> bqRow = pubsubBookInfoMessage.apply(ParDo.of(new BookInfoDetailProcess()));
		
		bqRow.apply(BigQueryIO.writeTableRows().to("gblibrary:gbdataset.newbook_message")
				.withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_NEVER)
				.withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND));
		
		dfPipeline.run().waitUntilFinish();
	}
	
	public static class BookInfoDetailProcess extends DoFn<String, TableRow> {
		
		@ProcessElement
		public void processMessage(ProcessContext processContext) {
			String message = processContext.element().toString();
			String timeStr = processContext.timestamp().toString();
			
			TableRow tableRow = new TableRow().set("message", message)
					.set("messagedt", timeStr);
			
			processContext.output(tableRow);
		}
	}
}
