package com.bkc.gblibrary.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bkc.gblibrary.model.BookInfo;
import com.bkc.gblibrary.model.Catalog;

@Repository
public interface BookInfoRepository extends JpaRepository<BookInfo, Long>{

	Optional<BookInfo> findByBookURL(String fileURL);
	
	List<BookInfo> findByCatalog(Catalog catalog);

	Optional<BookInfo> findByGbId(String bookId);

	@Query("select a " +
            "from BookInfo a " +
            "where a.title like %:title%")
	List<BookInfo> findAllByTitleContains(@Param("title") String title);

}
