package com.fiap.hackaton.infrastructure.services;

import com.fiap.hackaton.domain.enums.UploadStatus;
import com.fiap.hackaton.domain.exceptions.NotFoundException;
import com.fiap.hackaton.infrastructure.persistence.entities.Uploads;
import com.fiap.hackaton.infrastructure.persistence.repositories.UploadsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadService {
    private final UploadsRepository uploadsRepository;

    public Uploads findById(String id) {
        return uploadsRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(format("Upload record not found for id: %s", id)));
    }

    public void updateUploadStatus(Uploads upload, String urlDownload, UploadStatus status) {
        upload.setStatus(status.name());

        if(!urlDownload.isEmpty())
            upload.setUrlDownload(urlDownload);

        uploadsRepository.save(upload);
    }
}