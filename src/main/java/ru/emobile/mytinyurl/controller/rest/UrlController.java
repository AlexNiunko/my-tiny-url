package ru.emobile.mytinyurl.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.emobile.mytinyurl.dto.TinyUrlRequest;
import ru.emobile.mytinyurl.dto.TinyUrlResponse;
import ru.emobile.mytinyurl.service.TinyUrlService;

@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
public class UrlController {

    private final TinyUrlService tinyUrlService;

    @PostMapping("/create")
    public TinyUrlResponse createTinyUrl(@RequestBody @Valid TinyUrlRequest tinyUrlRequest){
        return tinyUrlService.createTinyUrl(tinyUrlRequest);
    }

}
