package com.bkc.gblibrary;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;

import com.bkc.gblibrary.beam.BatchProcessor;
import com.bkc.gblibrary.model.BookInfo;
import com.bkc.gblibrary.repository.BookInfoRepository;
import com.bkc.gblibrary.utility.FileUtilities;

@SpringBootTest
public class BatchProcessorTest {

	@Autowired
	private BatchProcessor batchP;
	
	@Autowired
	private FileUtilities fileUtil;
	
	@Autowired
	private BookInfoRepository bookInfoRepository;
	
	private @Autowired AutowireCapableBeanFactory beanFactory;
	
	
	
	@Test
	public void testBatch() throws IOException {
		String fileURL = "https://www.gutenberg.org/files/1/1-0.txt";
		
		Optional<BookInfo> bookInfo = bookInfoRepository.findByBookURL(fileURL);
		if(bookInfo==null) {
			return;
		}		
		
		testBatchProcessor(bookInfo.get().getId(), bookInfo.get().getBookURL());
		
	}
	
	public void testBatchProcessor(Long bookId, String fileURL) throws IOException {
		
		//String fileURL = "https://www.gutenberg.org/files/1/1-0.txt";
		String fileName = fileURL.substring(fileURL.lastIndexOf("/")+1, fileURL.length());
		String fileLink = fileURL.substring(0, fileURL.lastIndexOf("/"));
		String dest = "D:\\Temp";
		
		fileUtil.downloadFile(fileLink, fileName, dest, false, false);
		
		batchP = getBatchProcessor();
		batchP.genearteWordCount(dest, fileName, bookId);
	}

	@Autowired
	private BatchProcessor getBatchProcessor() {
		return beanFactory.createBean(BatchProcessor.class);
	}

}
