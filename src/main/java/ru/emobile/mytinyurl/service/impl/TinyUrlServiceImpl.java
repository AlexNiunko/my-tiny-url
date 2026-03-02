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

import static ru.emobile.mytinyurl.util.ExceptionMessages.ALIAS_EXIST;
import static ru.emobile.mytinyurl.util.Pages.EXPIRED;
import static ru.emobile.mytinyurl.util.Pages.NOT_FOUND;

@Slf4j
@Component
@RequiredArgsConstructor
public class TinyUrlServiceImpl implements TinyUrlService {

    public static final String REDIRECT = "redirect:";
    private final TinyUrlRepository tinyUrlRepository;
    private final TinyUrlMapper tinyUrlMapper;
    private final AliasProducer aliasProducer;

    @Override
    @Transactional
    public String findUrl(String tinyUrl) {
        return tinyUrlRepository.getUrl(tinyUrl).map(
                tiny -> {
                    log.info("TinyUrl: {} has founded",tinyUrl);
                    var expire = tiny.getExpire();
                    if (expire!=null && expire.getExpiredAt().isBefore(LocalDateTime.now())) {
                        log.info("TinyUrl: {} is expired",tinyUrl);
                        tinyUrlRepository.delete(tiny);
                        log.info("TinyUrl: {} has removed from DB as expired",tinyUrl);
                        return EXPIRED;
                    } else {
                        log.info("TinyUrl: {} has founded",tinyUrl);
                        return REDIRECT + tiny.getUrl();
                    }
                }
        ).orElse(NOT_FOUND);
    }

    @Override
    @Transactional
    public TinyUrlResponse createTinyUrl(TinyUrlRequest request) {
        String alias = request.alias();
        Long ttl = request.ttl();
        String tiny = Strings.isNotBlank(alias) ? getTiny(alias) : aliasProducer.getAlias(request.url());

        TinyUrl tinyUrl = tinyUrlMapper.toTinyUrl(request, tiny);
        ExpireUrl expired = getExpired(ttl);
        tinyUrl.setExpire(expired);

        TinyUrl saved = tinyUrlRepository.save(tinyUrl);
        log.info("TinyUrl is saved ib DB");

        return tinyUrlMapper.toTinyUrlResponse(saved.getTiny(), expired);
    }

    private ExpireUrl getExpired(Long ttl) {
        if (ttl != null) {
            log.info("Building expireUrl with ttl - {} seconds",ttl);
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
