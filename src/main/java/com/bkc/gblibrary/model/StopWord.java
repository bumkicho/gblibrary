package com.bkc.gblibrary.model;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * 
 * @author bumki
 *
 */

@Data
@Entity
@Table(name = "ignored_word")
public class StopWord {

	@Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
	
	@Column(name = "word", length=50)
	String word;

}
