package com.bkc.gblibrary.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

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
@Table(name = "book_info_detail")
public class BookInfoDetail implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;	
	
	// books that are used for search
	@ManyToOne
	@JoinColumn(name = "book_id")
	BookInfo bookInfo;
	
	@Column(name = "word")
	String word;
	
	@Column(name = "word_count")
	Long wordCount;
}
