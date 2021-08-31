package com.bkc.gblibrary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bkc.gblibrary.model.StopWord;

/**
 * 
 * @author bumki
 *
 */

@Repository
public interface StopWordRepository extends JpaRepository<StopWord, Long> {
	
	@Query("select upper(a.word) " +
            "from StopWord a ")
	List<String> findAllStopWord();

}
