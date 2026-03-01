package ru.emobile.mytinyurl.dto;

public record TinyUrlRequest(
        String url,
        Long ttl,
        String alias
) {
}
