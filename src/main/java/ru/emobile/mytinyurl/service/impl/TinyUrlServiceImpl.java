package ru.emobile.mytinyurl.service.impl;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.emobile.mytinyurl.dto.TinyUrlRequest;
import ru.emobile.mytinyurl.dto.TinyUrlResponse;
import ru.emobile.mytinyurl.entity.ExpireUrl;
import ru.emobile.mytinyurl.entity.TinyUrl;
import ru.emobile.mytinyurl.exception.ServiceException;
import ru.emobile.mytinyurl.mapper.TinyUrlMapper;
import ru.emobile.mytinyurl.repository.TinyUrlRepository;
import ru.emobile.mytinyurl.service.AliasProducer;
import ru.emobile.mytinyurl.service.TinyUrlService;

import static ru.emobile.mytinyurl.exception.ExceptionMessages.ALIAS_EXIST;

@Slf4j
@Component
@RequiredArgsConstructor
public class TinyUrlServiceImpl implements TinyUrlService {

    private final TinyUrlRepository tinyUrlRepository;
    private final TinyUrlMapper tinyUrlMapper;
    private final AliasProducer aliasProducer;

    @Override
    public String findUrl(String tinyUrl) {



        return "";
    }

    @Override
    @Transactional
    public TinyUrlResponse createTinyUrl(TinyUrlRequest request) {
        String alias = request.alias();
        Long ttl = request.ttl();
        String tiny = Strings.isNotBlank(alias) ? getTiny(alias) : aliasProducer.getAlias(request.url());

        TinyUrl tinyUrl = tinyUrlMapper.toTinyUrl(request,tiny);
        ExpireUrl expired = getExpired(ttl);
        tinyUrl.setExpire(expired);

        TinyUrl saved = tinyUrlRepository.save(tinyUrl);

        return tinyUrlMapper.toTinyUrlResponse(saved.getTiny(), expired);
    }

    private ExpireUrl getExpired(Long ttl) {
        if (ttl != null) {
            return ExpireUrl.builder()
                    .expiredAt(LocalDateTime.now().plusSeconds(ttl))
                    .build();
        }
        return null;
    }

    private String getTiny(String alias) {
        return (String) tinyUrlRepository.findByAlias(alias)
                .map(line -> {
                    log.info("Alias: {} is exist", line);
                    throw new ServiceException(String.format(ALIAS_EXIST, alias));
                }).orElse(alias);
    }
}
