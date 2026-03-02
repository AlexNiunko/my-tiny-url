package ru.emobile.mytinyurl.scheduller;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.emobile.mytinyurl.repository.TinyUrlRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class TinyUrlProcessScheduler {

    private final TinyUrlRepository tinyUrlRepository;

    @Scheduled(fixedDelayString = "${scheduler.interval-ms}")
    public void runTinyUrlProcessor() {
        log.info("Deleting expired tiny URLs.");
        tinyUrlRepository.delete(LocalDateTime.now());
    }

}
