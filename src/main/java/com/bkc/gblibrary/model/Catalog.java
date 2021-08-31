package com.bkc.gblibrary.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Data;

/**
 * 
 * @author bumki
 *
 */

@Data
@Entity
@Table(name = "catalog",
	indexes = {@Index(name = "idx_name",  columnList="name", unique = true)})
public class Catalog implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
	
	@Column(name = "name", length=50)
	String name;
	
	@Column(name = "url", length=250)
	String url;
	
	@Column(name = "last_refresh_dt")
	Instant lastRefreshDt;

}
