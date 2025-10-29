package com.urlshortner.readservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.urlshortner.readservice.model.ClickEvent;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {
    
    @Query("SELECT c.shortUrl, COUNT(c) FROM ClickEvent c WHERE c.shortUrl IN :shortUrls GROUP BY c.shortUrl")
    List<Object[]> countClicksByShortUrls(@Param("shortUrls") List<String> shortUrls);
    
    @Query("SELECT DATE(c.timestamp), COUNT(c) FROM ClickEvent c WHERE c.shortUrl = :shortUrl AND c.timestamp >= :startDate AND c.timestamp < :endDate GROUP BY DATE(c.timestamp) ORDER BY DATE(c.timestamp)")
    List<Object[]> countClicksByShortUrlAndDateRange(@Param("shortUrl") String shortUrl, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}