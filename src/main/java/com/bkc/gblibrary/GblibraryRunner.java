package com.bkc.gblibrary;

import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;

import com.bkc.gblibrary.utility.CatalogFile;
import com.bkc.gblibrary.utility.FileUtilities;

public class GblibraryRunner {
	private static final Logger log = LogManager.getLogger(GblibraryRunner.class);
	
	private String catalogUrl;
	private String fileName;
	private String downloadFolder;
	private String catalogFolder;
	private String commands;
	private String word;
	private String limit;
	private String book;
	
	public GblibraryRunner(String[] args) {
		System.out.println(args);
		
		if(args.length==1) {
			if(args[0] == "refresh catalog") {
				try {
					fileUtilities.downloadFile("https://gutenberg.org/cache/epub/feeds", "rdf-files.tar.bz2", "D:\\Temp", true, true);
				} catch (IOException e) {
					log.error("fileUtilites.downloadFile: " + e.getMessage());
				}
			}
		}
	}

	@Autowired
	private FileUtilities fileUtilities;
	
	@Autowired
	private CatalogFile catalogFile;
	
	

}
