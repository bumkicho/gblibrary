package com.bkc.gblibrary.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "catalog")
public class Catalog implements Serializable {
	
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
