package ru.emobile.mytinyurl.service;

import ru.emobile.mytinyurl.dto.TinyUrlRequest;
import ru.emobile.mytinyurl.dto.TinyUrlResponse;

public interface TinyUrlService {

    TinyUrlResponse createTinyUrl(TinyUrlRequest request);

    String findUrl(String tinyUrl);
}
