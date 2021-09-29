package com.bkc.gblibrary.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bkc.gblibrary.model.BookInfo;
import com.bkc.gblibrary.model.Catalog;

/**
 * 
 * @author bumki
 *
 */

@Repository
public interface CatalogRepository extends JpaRepository<Catalog, Long> {

	Optional<Catalog> findByName(String name);

	@Query("select a " +
            "from BookInfo a " +
            "where a.catalog=:catalog")
	List<BookInfo> findAllBooksByCatalog(@Param("catalog") Catalog catalog);

}
