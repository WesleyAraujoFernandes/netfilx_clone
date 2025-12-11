package com.netflix.clone.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.netflix.clone.entity.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {

    @Query("SELECT v FROM Video v WHERE "
            + "LOWER(v.title) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(v.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Video> searchVideos(@Param("search") String search, Pageable pageable);

}
