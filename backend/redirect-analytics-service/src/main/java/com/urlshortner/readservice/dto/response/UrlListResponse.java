package com.urlshortner.readservice.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class UrlListResponse {
    private List<UrlListItem> urls;
    private boolean hasMore;
    
    public UrlListResponse(List<UrlListItem> urls, boolean hasMore) {
        this.urls = urls;
        this.hasMore = hasMore;
    }
}