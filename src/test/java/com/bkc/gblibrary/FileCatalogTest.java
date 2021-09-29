package com.bkc.gblibrary;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bkc.gblibrary.beam.publisher.BookInfoProcessor;

@Component
public class FileCatalogTest {
	
	@Autowired
	BookInfoProcessor bookInfoProcessor;

	public void processCatalog(String catalogName, String downloadFolder) throws IOException {
		bookInfoProcessor.processCatalog(catalogName, downloadFolder);
	}

}
