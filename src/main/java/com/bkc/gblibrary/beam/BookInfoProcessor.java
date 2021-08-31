package com.bkc.gblibrary.beam;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import com.bkc.gblibrary.model.Catalog;
import com.bkc.gblibrary.repository.CatalogRepository;
import com.bkc.gblibrary.utility.CatalogFile;

/**
 * 
 * @author bumki
 *
 */

@Component
public class BookInfoProcessor {	
	
//	@Autowired
//	private BookInfoDetailProcessor bookDetailProcessor;
//	
//	@Autowired
//	private CatalogFile catalogFile;
	
	@Autowired
	private CatalogRepository catalogRepository;
	
	private @Autowired AutowireCapableBeanFactory beanFactory;
	
	public void processCatalog(String cataloFileName, String folderName) throws IOException {
		Optional<Catalog> catalog = catalogRepository.findByName(cataloFileName);
		if(!catalog.isPresent()) return;
		
//		processThroughCatalog(catalog.get(), folderName);
		generateBookInfoFromFiles(catalog.get(), folderName);
	}
	
	// process rdf files via Apache Beam pipeline	
	public void generateBookInfoFromFiles(Catalog catalog, String folderName) {
		PipelineOptions pipelineOptions = PipelineOptionsFactory.create();
		Pipeline pipeline = Pipeline.create(pipelineOptions);
		
		File file = new File(folderName);
		String[] extensions = new String[] {"rdf"};		
		List<File> files = (List<File>) FileUtils.listFiles(file, extensions, true);
		
		pipeline
		.apply(Create.of(files))
		.apply(ParDo.of(
	            new ExtractBookInfo(beanFactory, catalog)));
		
		pipeline.run().waitUntilFinish();
	}
	
	public static class ExtractBookInfo extends DoFn<File, String> {
		
		private static final long serialVersionUID = 1L;
		
		private AutowireCapableBeanFactory beanFactory;
		
		private CatalogFile catalogFile;
		
		private Catalog catalog;
		
		public ExtractBookInfo(AutowireCapableBeanFactory beanFactory, Catalog catalog) {
			this.beanFactory = beanFactory;
			this.catalog = catalog;
		}

		@ProcessElement
        public void process(@Element File file) {
        	String id = file.getParent().substring(file.getParent().lastIndexOf(File.separatorChar)+1);
        	try {
        		catalogFile = getCatalogFile();
				catalogFile.saveBookInfo(file, id, catalog);
			} catch (Exception e) {
				e.printStackTrace();
			}
          String result = "Processed file is {} " + file.getName();
        }
		
		@Autowired
		private CatalogFile getCatalogFile() {
			return beanFactory.createBean(CatalogFile.class);
		}
		
	}

}
