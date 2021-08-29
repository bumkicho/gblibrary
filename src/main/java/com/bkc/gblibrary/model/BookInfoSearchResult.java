package com.bkc.gblibrary.model;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "book_info_search_result")
public class BookInfoSearchResult {
	
	@Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
	
	@OneToOne
	@JoinColumn(name = "gb_book_search_id")
	BookInfoSearch bookInfoSearch;
	
	@Column(name = "search_status", length=20)
	String searchStatus;
	
	@ManyToOne
	@JoinColumn(name = "searched_word_id")
	Word word;

	@ManyToOne
	@JoinColumn(name = "searched_book_id")
	BookInfo bookInfo;

}
