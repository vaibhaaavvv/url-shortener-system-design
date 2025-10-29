package com.urlshortner.readservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.urlshortner.readservice.dto.response.UrlListItem;
import com.urlshortner.readservice.dto.response.UrlListResponse;
import com.urlshortner.readservice.model.ShortURL;
import com.urlshortner.readservice.repository.ShortURLRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UrlListService {

    @Autowired
    private ShortURLRepository shortURLRepository;

    public UrlListResponse getUserUrls(String userId, int pageNumber, int count) {
        int offset = pageNumber * count;
        // fetching extra to check for hasMore
        List<ShortURL> urls = shortURLRepository.findByUserIdWithOffsetAndLimit(userId, offset, count + 1);
        
        boolean hasMore = urls.size() > count;
        
        List<ShortURL> resultUrls = hasMore ? urls.subList(0, count) : urls;
        
        List<UrlListItem> urlItems = resultUrls.stream()
            .map(url -> new UrlListItem(
                url.getShortUrl(),
                url.getLongUrl(),
                url.getCreatedAt()
            ))
            .collect(Collectors.toList());
        
        return new UrlListResponse(urlItems, hasMore);
    }
}