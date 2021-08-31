package com.bkc.gblibrary;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import com.bkc.gblibrary.beam.BookInfoDetailProcessor;
import com.bkc.gblibrary.model.BookInfo;
import com.bkc.gblibrary.repository.BookInfoRepository;
import com.bkc.gblibrary.utility.FileUtilities;

@Component
public class BatchProcessorTest {

	@Autowired
	private BookInfoDetailProcessor bookInfoDetailProcessor;
	
	@Autowired
	private FileUtilities fileUtil;
	
	@Autowired
	private BookInfoRepository bookInfoRepository;
	
	private @Autowired AutowireCapableBeanFactory beanFactory;
	
	public void bookInfoDetailProcessorTest(String catalogName) {
		bookInfoDetailProcessor = getBookInfoDetailProcessor();
		bookInfoDetailProcessor.processThroughBookInfoByCatalog(catalogName, "0-100", "D:\\");

		
	}
	
	public void testBatch() throws IOException {
		
		String fileURL = "https://www.gutenberg.org/files/1/1-0.txt";
		
		Optional<BookInfo> bookInfo = bookInfoRepository.findByBookURL(fileURL);
		if(bookInfo==null) {
			return;
		}		
		
		testBatchProcessor(bookInfo.get().getId(), bookInfo.get().getBookURL());
		
	}
	
	public void testBatch(BookInfo bookInfo) throws IOException {
		testBatchProcessor(bookInfo.getId(), bookInfo.getBookURL());
	}
	
	public void testBatchProcessor(Long bookId, String fileURL) throws IOException {
		
		//String fileURL = "https://www.gutenberg.org/files/1/1-0.txt";
		String fileName = fileURL.substring(fileURL.lastIndexOf("/")+1, fileURL.length());
		String fileLink = fileURL.substring(0, fileURL.lastIndexOf("/"));
		String dest = "D:\\Temp";
		
		fileUtil.downloadFile(fileLink, fileName, dest, false, false);
		
		bookInfoDetailProcessor = getBookInfoDetailProcessor();
		bookInfoDetailProcessor.genearteWordCount(dest, fileName, bookId, null);
	}

	@Autowired
	private BookInfoDetailProcessor getBookInfoDetailProcessor() {
		return beanFactory.createBean(BookInfoDetailProcessor.class);
	}

	

}
