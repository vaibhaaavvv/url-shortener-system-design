package com.urlshortner.readservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.urlshortner.readservice.model.ShortURL;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface ShortURLRepository extends JpaRepository<ShortURL, String> {
    
    @Query(value = "SELECT * FROM short_urls WHERE user_id = :userId OR user_id = 'system-generated' ORDER BY created_at DESC LIMIT :limit OFFSET :offset", 
           nativeQuery = true)
    List<ShortURL> findByUserIdWithOffsetAndLimit(@Param("userId") String userId, 
                                                  @Param("offset") int offset, 
                                                  @Param("limit") int limit);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM ShortURL s WHERE s.expirationDate IS NOT NULL AND s.expirationDate < :currentTime")
    int deleteByExpirationDateBefore(@Param("currentTime") LocalDateTime currentTime);
}