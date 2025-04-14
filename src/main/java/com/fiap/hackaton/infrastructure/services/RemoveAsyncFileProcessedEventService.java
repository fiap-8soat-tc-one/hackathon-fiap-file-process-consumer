package com.fiap.hackaton.infrastructure.services;

import com.fiap.hackaton.infrastructure.presentation.workers.dto.FileProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RemoveAsyncFileProcessedEventService {
    private final ApplicationEventPublisher publisher;

    public void emitEvent(List<Path> filesTemp) {
        log.info("FileProcessedEventService emitEvent {}",
                filesTemp.stream().map(Path::getFileName).toList());
        publisher.publishEvent(FileProcessedEvent.builder().files(filesTemp).build());
    }
}
