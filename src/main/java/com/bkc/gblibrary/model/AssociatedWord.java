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

/**
 * 
 * @author bumki
 *
 */

@Data
@Entity
@Table(name = "associated_word")
public class AssociatedWord {
	
	@Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
	
	@ManyToOne
	@JoinColumn(name = "word")
	Word word;
	
	@ManyToOne
	@JoinColumn(name = "associated_word")
	Word associatedWord;

}
