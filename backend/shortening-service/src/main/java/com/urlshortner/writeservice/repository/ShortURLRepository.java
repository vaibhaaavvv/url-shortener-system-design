package com.urlshortner.writeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.urlshortner.writeservice.model.ShortURL;
import java.util.Optional;

@Repository
public interface ShortURLRepository extends JpaRepository<ShortURL, String> {
    Optional<ShortURL> findByLongUrl(String longUrl);
}