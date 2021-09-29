package com.bkc.gblibrary.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bkc.gblibrary.model.BookInfo;
import com.bkc.gblibrary.repository.BookInfoRepository;

/**
 * 
 * @author bumki
 *
 */

@Service
public class BookInfoService implements Serializable {	

	private static final long serialVersionUID = 1L;
	
	@Autowired
	private BookInfoRepository bookInfoRepository;
	
	public void toggleBookInfoNew(BookInfo bookInfo, String yn) {
		bookInfo.setIsNew(yn);
		bookInfoRepository.save(bookInfo);
	}

}
