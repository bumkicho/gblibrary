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
@Table(name = "book_info_detail")
public class BookInfoDetail {
	
	@Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;	
	
	@Column(name = "node_value")
	Long nodeValue;
	
	// books that are used for search
	@ManyToOne
	@JoinColumn(name = "gb_book_id")
	BookInfo bookInfo;
	
	// words that are used for search
	@ManyToOne
	@JoinColumn(name = "word_id")
	Word word;

}
