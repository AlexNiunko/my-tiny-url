package ru.emobile.mytinyurl.dto;

import java.time.LocalDateTime;

public record TinyUrlResponse(
        String tinyUrl,
        LocalDateTime expiredAt

) {
}
