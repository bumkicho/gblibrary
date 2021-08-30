package com.bkc.gblibrary.beam;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.KV;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bkc.gblibrary.model.Catalog;
import com.bkc.gblibrary.repository.CatalogRepository;
import com.bkc.gblibrary.utility.CatalogFile;

@Component
public class BookInfoProcessor {	
	
	@Autowired
	private BookInfoDetailProcessor bookDetailProcessor;
	
	@Autowired
	private CatalogFile catalogFile;
	
	@Autowired
	private CatalogRepository catalogRepository;
	
	public void processCatalog(String cataloFileName, String folderName) throws IOException {
		Optional<Catalog> catalog = catalogRepository.findByName(cataloFileName);
		if(catalog==null) return;
		
//		processThroughCatalog(catalog.get(), folderName);
		generateBookInfoFromFiles(catalog.get(), folderName);
	}
	
	// process rdf files one by one
	public void processThroughCatalog(Catalog catalog, String folderName) throws IOException {
		
		File file = new File(folderName);
		String[] extensions = new String[] {"rdf"};
		String id;
		
		List<File> files = (List<File>) FileUtils.listFiles(file, extensions, true);		
		
		for (File f : files) {
			id = f.getParent().substring(f.getParent().lastIndexOf(File.separatorChar)+1);
			catalogFile.saveBookInfo(f, id, catalog);
		}
	}
	
	public void generateBookInfoFromFiles(Catalog catalog, String folderName) {
		PipelineOptions pipelineOptions = PipelineOptionsFactory.create();
		Pipeline pipeline = Pipeline.create(pipelineOptions);
		
		File file = new File(folderName);
		String[] extensions = new String[] {"rdf"};		
		List<File> files = (List<File>) FileUtils.listFiles(file, extensions, true);
		
		pipeline
		.apply(Create.of(files))
		.apply(ParDo.of(
	            new DoFn<File, String>() {
	                @ProcessElement
	                public void process(@Element File file) {
	                	String id = file.getParent().substring(file.getParent().lastIndexOf(File.separatorChar)+1);
	                	try {
							catalogFile.saveBookInfo(file, id, catalog);
						} catch (Exception e) {
							e.printStackTrace();
						}
	                  String result = "Processed file is {} " + file.getName();
	                }
	              }));
		
		pipeline.run().waitUntilFinish();
	}

}
