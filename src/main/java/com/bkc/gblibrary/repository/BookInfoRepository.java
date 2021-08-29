package com.bkc.gblibrary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bkc.gblibrary.model.BookInfo;

@Repository
public interface BookInfoRepository extends JpaRepository<BookInfo, Long>{

}
