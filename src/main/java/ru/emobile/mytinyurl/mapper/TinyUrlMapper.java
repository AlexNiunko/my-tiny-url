package ru.emobile.mytinyurl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.emobile.mytinyurl.dto.TinyUrlRequest;
import ru.emobile.mytinyurl.dto.TinyUrlResponse;
import ru.emobile.mytinyurl.entity.ExpireUrl;
import ru.emobile.mytinyurl.entity.TinyUrl;

@Mapper(componentModel = "spring")
public interface TinyUrlMapper {

    @Mapping(target = "tinyUrl", source = "alias")
    @Mapping(target = "expiredAt ", source = "expireUrl.expiredAt")
    TinyUrlResponse toTinyUrlResponse(String alias, ExpireUrl expireUrl);

    @Mapping(target = "tiny", source = "tiny")
    @Mapping(target = "url", source = "request.url")
    TinyUrl toTinyUrl(TinyUrlRequest request, String tiny);
}
