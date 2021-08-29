package com.bkc.gblibrary.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bkc.gblibrary.model.Catalog;

@Repository
public interface CatalogRepository extends JpaRepository<Catalog, Long> {

	Optional<Catalog> findByName(String name);

}
