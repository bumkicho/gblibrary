package com.bkc.gblibrary;

import java.io.File;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bkc.gblibrary.model.Catalog;
import com.bkc.gblibrary.repository.CatalogRepository;
import com.bkc.gblibrary.utility.CatalogFile;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@SpringBootTest
public class FileCatalogTest {

	@Autowired
	private CatalogFile catalogFile;
	
	@Autowired
	private CatalogRepository catalogRepository;
	
	@Test
	public void testThisFunction() {
		getCatalogFile("D:\\\\Temp\\cache\\epub\\1", "rdf-files.tar.bz2");
	}
	
	public void getCatalogFile(String filePath, String fileName) {
		File file = new File(filePath);
		
		Optional<Catalog> catalog = catalogRepository.findByName(fileName);
		if(catalog==null) return;
		
		catalogFile.saveBookInfo(file, catalog.get());
	}

}
