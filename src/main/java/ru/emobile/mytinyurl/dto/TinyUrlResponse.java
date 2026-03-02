package ru.emobile.mytinyurl.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record TinyUrlResponse(
        String tinyUrl,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime expiredAt

) {
}
