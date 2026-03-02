package ru.emobile.mytinyurl.dto;

import org.hibernate.validator.constraints.URL;

public record TinyUrlRequest(

        @URL(message = "The url field must contain a valid URL")
        String url,

        Long ttl,

        String alias
) {
}
