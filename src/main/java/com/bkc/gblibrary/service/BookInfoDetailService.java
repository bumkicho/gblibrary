package com.bkc.gblibrary.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bkc.gblibrary.model.BookInfoDetail;
import com.bkc.gblibrary.repository.BookInfoDetailRepository;
import com.bkc.gblibrary.repository.BookInfoRepository;

@Service
public class BookInfoDetailService implements Serializable {	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private BookInfoDetailRepository bookInfoDetailRepository;

	@Autowired
	private BookInfoRepository bookInfoRepository;
	
	public BookInfoDetail saveBookInfoDetail(BookInfoDetail bookInfoDetail) {
		return bookInfoDetailRepository.save(bookInfoDetail);
	}

	public void process(Long bookId, String key, Long value) {
		bookInfoRepository.findById(bookId);
	}

}
