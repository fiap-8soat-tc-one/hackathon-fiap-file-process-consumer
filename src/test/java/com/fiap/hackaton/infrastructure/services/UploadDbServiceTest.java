package com.fiap.hackaton.infrastructure.services;

import br.com.six2six.fixturefactory.Fixture;
import com.fiap.hackaton.domain.enums.UploadStatus;
import com.fiap.hackaton.domain.exceptions.NotFoundException;
import com.fiap.hackaton.fixture.FixtureTest;
import com.fiap.hackaton.infrastructure.persistence.entities.Uploads;
import com.fiap.hackaton.infrastructure.persistence.repositories.UploadsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UploadDbServiceTest extends FixtureTest {
    @Mock
    private UploadsRepository uploadsRepositoryMock;
    @InjectMocks
    private UploadDbService uploadDbService;

    private Uploads uploadsMock;

    @BeforeEach
    void setUp() {
        uploadsMock = Fixture.from(Uploads.class).gimme("valid");
    }

    @Test
    void findById() {
        var id = uploadsMock.getId().toString();
        Mockito.when(uploadsRepositoryMock.findById(id)).thenReturn(Optional.of(uploadsMock));

        var result = uploadDbService.findById(id);

        assertNotNull(result);
        assertEquals(uploadsMock, result);
        Mockito.verify(uploadsRepositoryMock, Mockito.times(1)).findById(id);

    }

    @Test
    void findByIdNotFound() {
        var id = uploadsMock.getId().toString();
        Mockito.when(uploadsRepositoryMock.findById(id)).thenReturn(Optional.empty());

        var assertThrows = assertThrows(NotFoundException.class, () -> {
            uploadDbService.findById(id);
        });
        Assertions.assertInstanceOf(NotFoundException.class, assertThrows);
        Mockito.verify(uploadsRepositoryMock, Mockito.times(1)).findById(id);

    }

    @Test
    void updateUploadStatus() {
        uploadDbService.updateUploadStatus(uploadsMock, "http://localhost/1234.mp4", UploadStatus.PROCESSED);
        Mockito.verify(uploadsRepositoryMock, Mockito.times(1)).save(uploadsMock);
    }

    @Test
    void updateUploadStatusNullEmptyLink() {
        uploadDbService.updateUploadStatus(uploadsMock, null, UploadStatus.PROCESSED);

        uploadDbService.updateUploadStatus(uploadsMock, "", UploadStatus.PROCESSED);

        Mockito.verify(uploadsRepositoryMock, Mockito.times(2)).save(uploadsMock);
    }


}