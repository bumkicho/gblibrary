package com.bkc.gblibrary;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bkc.gblibrary.utility.FileUtilities;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@SpringBootTest
public class FileDownLoadTest {
	
	@Autowired
	private FileUtilities fileUtilities;
	
	@Test
	public void testThisFunction() {
		//testFileUtilities();
		testExtractFiles();
	}

	public void testFileUtilities() {
		try {
			fileUtilities.downloadFile("https://gutenberg.org/cache/epub/feeds", "rdf-files.tar.bz2", "D:\\Temp");
		} catch (IOException e) {
			
			System.out.println(e.getStackTrace());

		}
	}

	public void testExtractFiles() {
		try {
			fileUtilities.extractFile("D:\\Temp","rdf-files.tar.bz2", "D:\\Temp");
		} catch (IOException e) {
			
			System.out.println(e.getStackTrace());

		}
	}

}
