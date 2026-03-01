package ru.emobile.mytinyurl.service;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.emobile.mytinyurl.dto.TinyUrlRequest;
import ru.emobile.mytinyurl.dto.TinyUrlResponse;
import ru.emobile.mytinyurl.entity.ExpireUrl;
import ru.emobile.mytinyurl.entity.TinyUrl;
import ru.emobile.mytinyurl.exception.ServiceException;
import ru.emobile.mytinyurl.mapper.TinyUrlMapper;
import ru.emobile.mytinyurl.repository.TinyUrlRepository;
import ru.emobile.mytinyurl.service.impl.TinyUrlServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ru.emobile.mytinyurl.exception.ExceptionMessages.ALIAS_EXIST;

@ExtendWith(MockitoExtension.class)
class TinyUrlServiceTest {

    @Mock
    private TinyUrlRepository tinyUrlRepository;

    @Mock
    private TinyUrlMapper tinyUrlMapper;

    @Mock
    private AliasProducer aliasProducer;

    @InjectMocks
    private TinyUrlServiceImpl tinyUrlService;

    @Test
    void shouldReturnSuccessResultWithTtlAndAlias() {
        TinyUrlRequest request = new TinyUrlRequest(
                "https://news.mail.ru/incident/69990471/?frommail=1&md=1",
                300L,
                "test"
        );
        var now = LocalDateTime.now();
        TinyUrl tinyUrl = TinyUrl.builder()
                .id(1L)
                .url(request.url())
                .tiny(request.alias())
                .expire(ExpireUrl.builder().id(1L).expiredAt(now).build())
                .build();

        TinyUrlResponse expected = new TinyUrlResponse(request.alias(), now);

        when(tinyUrlRepository.findByAlias(request.alias())).thenReturn(Optional.empty());
        when(tinyUrlMapper.toTinyUrl(request, request.alias())).thenReturn(tinyUrl);
        when(tinyUrlRepository.save(tinyUrl)).thenReturn(tinyUrl);
        when(tinyUrlMapper.toTinyUrlResponse(eq(request.alias()), any(ExpireUrl.class)))
                .thenReturn(expected);

        var actual = tinyUrlService.createTinyUrl(request);

        Assertions.assertEquals(expected, actual);

        verify(tinyUrlRepository).findByAlias(request.alias());
        verify(tinyUrlMapper).toTinyUrl(request,request.alias());
        verify(tinyUrlRepository).save(tinyUrl);
        verify(tinyUrlMapper).toTinyUrlResponse(eq(request.alias()), any(ExpireUrl.class));

    }

    @Test
    void shouldReturnResultWithoutAlias(){
        TinyUrlRequest request = new TinyUrlRequest(
                "https://news.mail.ru/incident/69990471/?frommail=1&md=1",
                300L,
                null
        );
        String alias="test";
        var now = LocalDateTime.now();
        TinyUrl tinyUrl = TinyUrl.builder()
                .id(1L)
                .url(request.url())
                .tiny(alias)
                .expire(ExpireUrl.builder().id(1L).expiredAt(now).build())
                .build();

        TinyUrlResponse expected = new TinyUrlResponse(alias, now);

        when(aliasProducer.getAlias(request.url())).thenReturn(alias);
        when(tinyUrlMapper.toTinyUrl(request, alias)).thenReturn(tinyUrl);
        when(tinyUrlRepository.save(tinyUrl)).thenReturn(tinyUrl);
        when(tinyUrlMapper.toTinyUrlResponse(eq(alias), any(ExpireUrl.class)))
                .thenReturn(expected);

        var actual = tinyUrlService.createTinyUrl(request);

        Assertions.assertEquals(expected, actual);

        verify(aliasProducer).getAlias(request.url());
        verify(tinyUrlMapper).toTinyUrl(request,alias);
        verify(tinyUrlRepository).save(tinyUrl);
        verify(tinyUrlMapper).toTinyUrlResponse(eq(alias), any(ExpireUrl.class));

    }

    @Test
    void shouldReturnResultWithoutTtl(){
        TinyUrlRequest request = new TinyUrlRequest(
                "https://news.mail.ru/incident/69990471/?frommail=1&md=1",
                null,
                null
        );
        String alias="test";
        var now = LocalDateTime.now();
        TinyUrl tinyUrl = TinyUrl.builder()
                .id(1L)
                .url(request.url())
                .tiny(alias)
                .expire(null)
                .build();

        TinyUrlResponse expected = new TinyUrlResponse(alias, now);

        when(aliasProducer.getAlias(request.url())).thenReturn(alias);
        when(tinyUrlMapper.toTinyUrl(request, alias)).thenReturn(tinyUrl);
        when(tinyUrlRepository.save(tinyUrl)).thenReturn(tinyUrl);
        when(tinyUrlMapper.toTinyUrlResponse(eq(alias), isNull()))
                .thenReturn(expected);

        var actual = tinyUrlService.createTinyUrl(request);

        Assertions.assertEquals(expected, actual);

        verify(aliasProducer).getAlias(request.url());
        verify(tinyUrlMapper).toTinyUrl(request,alias);
        verify(tinyUrlRepository).save(tinyUrl);
        verify(tinyUrlMapper).toTinyUrlResponse(eq(alias),  isNull());

    }

    @Test
    void shouldThrowExceptionWhenAliasExist(){
        var alias = "test";
        TinyUrlRequest request = new TinyUrlRequest(
                "https://news.mail.ru/incident/69990471/?frommail=1&md=1",
                null,
                alias

        );
        TinyUrl tinyUrl = TinyUrl.builder()
                .id(1L)
                .url(request.url())
                .tiny(alias)
                .expire(null)
                .build();

        when(tinyUrlRepository.findByAlias(request.alias())).thenReturn(Optional.of(tinyUrl));

        ServiceException serviceException = Assertions.assertThrows(
                ServiceException.class,
                () -> tinyUrlService.createTinyUrl(request)
        );
        var actualMessage = serviceException.getMessage();
        Assertions.assertEquals(String.format(ALIAS_EXIST, alias),actualMessage);

        verify(tinyUrlRepository).findByAlias(request.alias());
        verifyNoMoreInteractions(tinyUrlMapper);
        verifyNoMoreInteractions(tinyUrlRepository);
        verifyNoMoreInteractions(tinyUrlMapper);

    }



}
