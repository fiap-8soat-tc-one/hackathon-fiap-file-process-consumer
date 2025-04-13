package com.fiap.hackaton.service.async;

import com.fiap.hackaton.broker.dto.FileProcessedEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
@Slf4j
public class FileEventProcessedListener {
    @SneakyThrows
    @Async
    @EventListener
    public void handle(FileProcessedEvent fileProcessedEvent) {
        log.info("FileEventProcessedListener handle {}", fileProcessedEvent.getFiles().stream().map(Path::getFileName).toList());
        for (Path file : fileProcessedEvent.getFiles()) {
            Files.deleteIfExists(file);
        }
    }
}
