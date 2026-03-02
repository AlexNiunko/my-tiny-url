package ru.emobile.mytinyurl.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class TaskSchedulingConfig {

    public static final String TASK_SCHEDULER = "task-scheduler-";
    @Value("${scheduler.pool-size}")
    private final Integer poolSize;

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(poolSize);
        scheduler.setThreadNamePrefix(TASK_SCHEDULER);
        scheduler.initialize();
        return scheduler;
    }
}
