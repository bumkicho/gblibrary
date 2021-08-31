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
	
	public List<SearchResultProjection> searchWord(String catalogName, String bookId, int limit, String searchOption) {
		Optional<Catalog> catalog = catalogRepository.findByName(catalogName);
		if(!catalog.isPresent()) {
			return null;
		}
		
		List<SearchResultProjection> result;
		List<BookInfo> books = new ArrayList<BookInfo>();
		List<String> bookIDs = new ArrayList<String>();
		Optional<BookInfo> bookInfo = bookInfoRepository.findByGbId(bookId);
		
		if(!bookInfo.isPresent() && searchOption!=null && !searchOption.isEmpty()) {
			
			if(searchOption.equalsIgnoreCase("b") && !bookInfo.isPresent()) {
				return null;
			} else if(searchOption.equalsIgnoreCase("t")) {
				books = bookInfoRepository.findAllByTitleContains(bookId);
				if(books.size()==0) {
					return null;
				} else {
					books.stream().forEach(b -> bookIDs.add(b.getGbId()));
				}
			} else if (searchOption.equalsIgnoreCase("a")) {
				books = bookInfoRepository.findAllByAuthorContains(bookId);
				if(books.size()==0) {
					return null;
				} else {
					books.stream().forEach(b -> bookIDs.add(b.getGbId()));
				}
			} else { // no search option was given. we'll have to do waterfall approach.
				books = bookInfoRepository.findAllByTitleContains(bookId);
				if(books.size() > 0) {
					books.stream().forEach(b -> bookIDs.add(b.getGbId()));
				}
				
				if(books.size()==0) {
					books = bookInfoRepository.findAllByAuthorContains(bookId);
					if(books.size() > 0) {
						books.stream().forEach(b -> bookIDs.add(b.getGbId()));
					} else {
						return null;
					}
				}
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
