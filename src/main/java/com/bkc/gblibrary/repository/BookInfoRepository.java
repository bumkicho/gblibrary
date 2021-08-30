package com.bkc.gblibrary.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bkc.gblibrary.model.BookInfo;

@Repository
public interface BookInfoRepository extends JpaRepository<BookInfo, Long>{

	Optional<BookInfo> findByBookURL(String fileURL);

}
