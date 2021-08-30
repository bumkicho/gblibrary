package com.bkc.gblibrary;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class InitialSetupTest {
	
	@Autowired
	FileDownLoadTest fileDownLoadTest;
	
	@Autowired
	FileCatalogTest fileCatalogTest;
	
	@Autowired
	BatchProcessorTest batchProcessorTest;
	
	@Test
	public void initSetup() {
		String catalogName = "rdf-files.tar.bz2";
		String downloadFolder = "D:\\\\Temp";
		String downloadUrl = "https://gutenberg.org/cache/epub/feeds";
		
		//clears download folder, download latest catalog file, and extract them
//		fileDownLoadTest.testFileUtilities(downloadUrl, catalogName, downloadFolder);
		
		try {
			// go through rdf and populate book_info
			fileCatalogTest.processCatalog(catalogName, downloadFolder);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
		// receive catalog, find all bookinfo by catalog
		// go through bookinfo and populate bookinfodetail
//		batchProcessorTest.bookInfoDetailProcessorTest(catalogName);
	}

}
