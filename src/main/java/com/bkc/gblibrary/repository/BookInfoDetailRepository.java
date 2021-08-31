package com.bkc.gblibrary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bkc.gblibrary.model.BookInfo;
import com.bkc.gblibrary.model.BookInfoDetail;
import com.bkc.gblibrary.model.Catalog;
import com.bkc.gblibrary.projection.SearchResultProjection;

@Repository
public interface BookInfoDetailRepository extends JpaRepository<BookInfoDetail, Long> {
	
	@Query("select " +
            "b.title as title, a.word as word, a.wordCount as wordCount " +
            "from BookInfoDetail a inner join BookInfo b on a.bookInfo=b.id " +
            "where b.catalog=:catalog and b.gbId=:bookId order by a.wordCount desc ")
	List<SearchResultProjection> findWordsInBookTopMost(@Param("catalog") Catalog catalog, @Param("bookId") String bookId);

	@Query("select " +
            "b.title as title, a.word as word, a.wordCount as wordCount " +
            "from BookInfoDetail a inner join BookInfo b on a.bookInfo=b.id " +
            "where b.catalog=:catalog and b.gbId IN (:bookIDs) order by a.wordCount desc ")
	List<SearchResultProjection> findWordsFromBooksTopMost(@Param("catalog") Catalog catalog, @Param("bookIDs") List<String> bookIDs);

	@Query("select " +
            "a.word as word, b.title as title, a.wordCount as wordCount " +
            "from BookInfoDetail a inner join BookInfo b on a.bookInfo=b.id " +
            "where b.catalog=:catalog and a.word=:word order by a.wordCount desc ")
	List<SearchResultProjection> findBooksByWordTopMost(@Param("catalog") Catalog catalog, @Param("word") String word);

}
