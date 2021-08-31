package com.bkc.gblibrary.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bkc.gblibrary.model.BookInfo;
import com.bkc.gblibrary.model.Catalog;
import com.bkc.gblibrary.projection.SearchResultProjection;
import com.bkc.gblibrary.repository.BookInfoDetailRepository;
import com.bkc.gblibrary.repository.BookInfoRepository;
import com.bkc.gblibrary.repository.CatalogRepository;

@Service
public class SearchAPIService {
	
	@Autowired
	BookInfoDetailRepository bookInfoDetailRepository;
	
	@Autowired
	BookInfoRepository bookInfoRepository;
	
	@Autowired
	CatalogRepository catalogRepository;
	
	public List<SearchResultProjection> searchWord(String catalogName, String bookId, int limit) {
		Optional<Catalog> catalog = catalogRepository.findByName(catalogName);
		if(!catalog.isPresent()) {
			return null;
		}
		
		List<SearchResultProjection> result;
		List<BookInfo> books;
		List<String> bookIDs = new ArrayList<String>();
		
		Optional<BookInfo> bookInfo = bookInfoRepository.findByGbId(bookId);
		if(!bookInfo.isPresent()) {
			books = bookInfoRepository.findAllByTitleContains(bookId);
			if(books.size()==0) {
				return null;
			} else {
				books.stream().forEach(b -> bookIDs.add(b.getGbId()));
			}
		}
		
		if(bookIDs.size()>0) {
			result = bookInfoDetailRepository.findWordsFromBooksTopMost(catalog.get(), bookIDs);
		} else {
			result = bookInfoDetailRepository.findWordsInBookTopMost(catalog.get(), bookInfo.get().getGbId());
		}
		
		if(result.size()>limit) {
			return result.subList(0, limit);
		}
		
		return result;
	}
	
	public List<SearchResultProjection> searchBook(String catalogName, String word, int limit) {
		Optional<Catalog> catalog = catalogRepository.findByName(catalogName);
		if(!catalog.isPresent()) {
			return null;
		}
		List<SearchResultProjection> result = bookInfoDetailRepository.findBooksByWordTopMost(catalog.get(), word);
		if(result.size()>limit) {
			return result.subList(0, limit);
		}
		
		return result;
	}
	

}
