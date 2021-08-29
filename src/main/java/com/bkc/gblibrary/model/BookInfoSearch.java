package com.bkc.gblibrary.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "book_info_search")
public class BookInfoSearch {

	@Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;	
	
	// search_words || search_books
	@Column(name = "search_type", length=20)
	String searchType;
	
	// most frequent || least frequent 
	@Column(name = "search_option", length=20)
	String searchOption;
	
	// search associated words true || false 
	@Column(name = "book_option")
	Boolean bookOption;

	@Column(name = "search_limit")
	int limit;
	
	@Column(name = "search_dt")
	LocalDateTime searchDt;
	
	// books that are used for search
	@ManyToOne
	@JoinColumn(name = "gb_book_id")
	BookInfo bookInfo;
	
	// words that are used for search
	@ManyToOne
	@JoinColumn(name = "searched_word")
	Word word;
}