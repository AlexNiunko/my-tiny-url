package ru.emobile.mytinyurl.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.emobile.mytinyurl.service.TinyUrlService;

@Controller
@RequestMapping("/tiny")
@RequiredArgsConstructor
public class TinyController {

    private final TinyUrlService tinyUrlService;

    @GetMapping("/{page}")
    public String getPage(@PathVariable String page){
        return tinyUrlService.findUrl(page);
    }

}
