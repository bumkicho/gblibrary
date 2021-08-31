package com.bkc.gblibrary.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bkc.gblibrary.repository.StopWordRepository;

/**
 * 
 * @author bumki
 *
 */

@Service
public class WordService {

	@Autowired
	private StopWordRepository stopWordRepository;
	
	public List<String> getAllStopWords() {
		return stopWordRepository.findAllStopWord();
	}
	
}
