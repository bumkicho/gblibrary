package com.bkc.gblibrary.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "word")
public class Word {

	@Id
	@Column(name = "word", length=50)
	String word;
	
	@Column(name = "search_count")
	int searchCount;
	
	@Column(name = "last_search_dt")
	LocalDateTime lastSearchDt;

}
