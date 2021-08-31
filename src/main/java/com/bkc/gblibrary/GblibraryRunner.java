package com.bkc.gblibrary;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.internal.build.AllowSysOut;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import com.bkc.gblibrary.beam.BookInfoDetailProcessor;
import com.bkc.gblibrary.beam.BookInfoProcessor;
import com.bkc.gblibrary.projection.SearchResultProjection;
import com.bkc.gblibrary.service.SearchAPIService;
import com.bkc.gblibrary.utility.CatalogFile;
import com.bkc.gblibrary.utility.FileUtilities;

/**
 * 
 * @author bumki
 *
 */

public class GblibraryRunner {
	private static final Logger log = LogManager.getLogger(GblibraryRunner.class);

	private String catalogUrl;
	private String catalogName;
	private String downloadFolder;
	private String command;
	private String word;
	private String searchOption;
	private int limit;
	private String bookId;
	private String bookRange;
	private Boolean cleanYN;
	private Boolean catalogYN;

	@Autowired
	private FileUtilities fileUtilities;

	@Autowired
	private CatalogFile catalogFile;

	@Autowired
	BookInfoProcessor bookInfoProcessor;

	@Autowired
	private BookInfoDetailProcessor bookInfoDetailProcessor;

	@Autowired
	private SearchAPIService searchAPIService;

	private @Autowired AutowireCapableBeanFactory beanFactory;

	@Autowired
	private BookInfoDetailProcessor getBookInfoDetailProcessor() {
		return beanFactory.createBean(BookInfoDetailProcessor.class);
	}

	public void set(String[] args) {
		if (args.length == 0) {
			return;
		}

		this.command = args[0];

		if (this.command.equalsIgnoreCase("init")) {

			this.catalogUrl = args[1]; // https://gutenberg.org/cache/epub/feeds
			this.catalogName = args[2]; // rdf-files.tar.bz2
			this.downloadFolder = args[3]; // "D:\\\\Temp"
			this.bookRange = args[4];
			this.cleanYN = true;
			this.catalogYN = true;

		} else if (this.command.equalsIgnoreCase("count")) {

			this.catalogName = args[1]; // rdf-files.tar.bz2
			this.bookRange = args[2];

		} else if (this.command.equalsIgnoreCase("search_word")) {

			this.catalogName = args[1];
			this.bookId = args[2];
			this.limit = Integer.parseInt(args[3]);
			this.searchOption = args[4];

		} else if (this.command.equalsIgnoreCase("search_book")) {

			this.catalogName = args[1];
			this.word = args[2];
			this.limit = Integer.parseInt(args[3]);

		} else if (this.command.equalsIgnoreCase("refresh_word_count")) {

			this.catalogName = args[1];
		}

	}

	public void run() {
		if (this.command.equalsIgnoreCase("init")) {
			try {
				fileUtilities.downloadFile(catalogUrl, catalogName, downloadFolder, cleanYN, catalogYN);
			} catch (IOException e) {
				log.error("fileUtilites.downloadFile: " + e.getMessage());
			}

			try {
				bookInfoProcessor.processCatalog(catalogName, downloadFolder);
			} catch (IOException e) {
				log.error("bookInfoProcessor.processCatalog: " + e.getMessage());
			}

			bookInfoDetailProcessor = getBookInfoDetailProcessor();
			bookInfoDetailProcessor.processThroughBookInfoByCatalog(catalogName, this.bookRange);

		}  else if (this.command.equalsIgnoreCase("count")) {
			
			bookInfoDetailProcessor = getBookInfoDetailProcessor();
			bookInfoDetailProcessor.processThroughBookInfoByCatalog(catalogName, this.bookRange);
			
		} else if (this.command.equalsIgnoreCase("search_word")) {

			List<SearchResultProjection> result = searchAPIService.searchWord(catalogName, bookId, limit, searchOption);
			result.forEach(r -> {
				displayResultItem(r);
			});

		} else if (this.command.equalsIgnoreCase("search_book")) {

			List<SearchResultProjection> result = searchAPIService.searchBook(catalogName, word, limit);
			result.forEach(r -> {
				displayResultItem(r);
			});

		} else if (this.command.equalsIgnoreCase("refresh_word_count")) {
			System.out.println("NOT_IMPLEMENTED");
		} else {
			System.out.println("NOT_IMPLEMENTED");
		}

	}

	private void displayResultItem(SearchResultProjection r) {
		System.out.println("Title: " + r.getTitle() + " Word:" + r.getWord() + " Count:" + r.getWordCount());
	}

}
