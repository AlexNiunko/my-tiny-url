package ru.emobile.mytinyurl.integration;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.emobile.mytinyurl.entity.TinyUrl;
import ru.emobile.mytinyurl.repository.TinyUrlRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@ActiveProfiles("test")
@DataJpaTest
@RequiredArgsConstructor
@AutoConfigureTestDatabase(replace = NONE)
@Sql(scripts = {"/sql/init.sql", "/sql/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class TinyUrlRepositoryTest {

    private final TinyUrlRepository tinyUrlRepository;

    @Test
    void shouldFindTinyUrlByAlias() {
        String alias = "grodnoNews";
        String expected = "https://news.google.com/topics/CAAqIggKIhxDQkFTRHdvSkwyMHZNREUxT1dabUVnSnlkU2dBUAE?hl=ru&gl=RU&ceid=RU:ru";
        var byAlias = tinyUrlRepository.findByAlias(alias).map(TinyUrl::getUrl).orElse("default");
        assertEquals(expected, byAlias);
    }

    @Test
    void shouldGetUrlWithNullExpire() {
        String url = "mvd";
        String expected="https://mvd.gov.by/ru/private/home/service/17";
        tinyUrlRepository.getUrl(url).ifPresentOrElse(
                value -> {
                    Assertions.assertEquals(expected, value.getUrl());
                    Assertions.assertNull(value.getExpire());
                }, () -> {throw new RuntimeException("Empty result");}

        );
    }

    @Test
    void shouldDeleteExpiredTinyUrl(){
        tinyUrlRepository.delete(LocalDateTime.now());
        var byId = tinyUrlRepository.findById(1L);
        Assertions.assertFalse(byId.isPresent());
    }

}
