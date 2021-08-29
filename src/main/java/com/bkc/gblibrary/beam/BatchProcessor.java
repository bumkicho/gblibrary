package com.bkc.gblibrary.beam;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;

public class BatchProcessor {
	
	public void batchProcessCatalog() {
		PipelineOptions pipelineOptions = PipelineOptionsFactory.create();
		Pipeline pipeline=Pipeline.create(pipelineOptions);
		pipeline.run().waitUntilFinish();
	}

}
