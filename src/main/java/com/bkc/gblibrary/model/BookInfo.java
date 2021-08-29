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
@Table(name = "book_info")
public class BookInfo {

	@Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
	
	@Column(name = "gb_book_id", length=20)
	String gbId;
	
	@Column(name = "gb_book_title", length=200)
	String title;
	
	@Column(name = "gb_book_url", length=200)
	String bookURL;
	
	// appears there is no author id and there can be multiple authors
	@Column(name = "gb_book_author", length=500)
	String author;
	
	@Column(name = "gb_book_language", length=10)
	String language;
	
	@Column(name = "gb_book_issued")
	LocalDateTime issuedDt;
	
	@Column(name = "gb_book_modified")
	LocalDateTime modifiedDt;
	
	@ManyToOne
	@JoinColumn(name="catalog_id")
	Catalog catalog;

}
