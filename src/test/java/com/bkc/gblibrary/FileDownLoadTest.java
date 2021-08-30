package com.bkc.gblibrary;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bkc.gblibrary.utility.FileUtilities;

@Component
public class FileDownLoadTest {
	
	@Autowired
	private FileUtilities fileUtilities;

	public void testFileUtilities(String downloadUrl, String catalogName, String downloadFolder) {
		try {
			fileUtilities.downloadFile(downloadUrl, catalogName, downloadFolder, true, true);
		} catch (IOException e) {
			
			System.out.println(e.getStackTrace());

		}
	}

}
