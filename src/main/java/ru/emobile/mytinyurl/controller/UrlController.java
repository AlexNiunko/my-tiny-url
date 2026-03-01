package ru.emobile.mytinyurl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.emobile.mytinyurl.dto.TinyUrlRequest;
import ru.emobile.mytinyurl.dto.TinyUrlResponse;
import ru.emobile.mytinyurl.service.TinyUrlService;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final TinyUrlService tinyUrlService;

    @PostMapping("/create")
    public TinyUrlResponse createTinyUrl(TinyUrlRequest tinyUrlRequest){
        return tinyUrlService.createTinyUrl(tinyUrlRequest);
    }

}
