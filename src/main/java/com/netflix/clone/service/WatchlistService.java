package com.netflix.clone.service;

import com.netflix.clone.dto.response.MessageResponse;

public interface WatchlistService {

    MessageResponse addToWatchlist(String email, Long videoId);

}
