package com.bkc.gblibrary.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

/**
 * 
 * @author bumki
 *
 */

@Data
@Entity
@Table(name = "book_info",
	indexes = {@Index(name = "idx_gbId",  columnList="gb_book_id", unique = true),
	        @Index(name = "idx_title", columnList="gb_book_title", unique = false),
	        @Index(name = "idx_author", columnList="gb_book_author", unique = false),
	        @Index(name = "idx_catalog", columnList="catalog_id", unique = false)})
public class BookInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
	
	@Column(name = "gb_book_id", length=20)
	String gbId;
	
	@Column(name = "gb_book_title", length=500)
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
