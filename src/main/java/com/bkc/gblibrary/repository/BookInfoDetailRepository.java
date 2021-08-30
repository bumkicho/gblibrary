package com.bkc.gblibrary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bkc.gblibrary.model.BookInfoDetail;

@Repository
public interface BookInfoDetailRepository extends JpaRepository<BookInfoDetail, Long> {

}
