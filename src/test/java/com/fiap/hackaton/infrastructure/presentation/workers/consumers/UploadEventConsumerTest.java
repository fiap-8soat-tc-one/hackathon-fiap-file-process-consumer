package com.fiap.hackaton.infrastructure.presentation.workers.consumers;

import br.com.six2six.fixturefactory.Fixture;
import com.fiap.hackaton.application.usecases.ProcessFileUploadUseCase;
import com.fiap.hackaton.fixture.FixtureTest;
import com.fiap.hackaton.infrastructure.presentation.workers.dto.UploadEventMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UploadEventConsumerTest extends FixtureTest {
    @Mock
    private ProcessFileUploadUseCase processFileUploadUseCaseMock;
    @InjectMocks
    private UploadEventConsumer uploadEventConsumer;
    private UploadEventMessage uploadEventMessageMock;
    private UploadEventMessage uploadEventMessageInvalidMock;

    @BeforeEach
    void setUp() {
        uploadEventMessageMock = Fixture.from(UploadEventMessage.class).gimme("valid");
        uploadEventMessageInvalidMock = Fixture.from(UploadEventMessage.class).gimme("invalid");
    }

    @Test
    void dequeueValidMessage() {
        uploadEventConsumer.listen(uploadEventMessageMock);
        Mockito.verify(processFileUploadUseCaseMock, Mockito.times(1)).execute(Mockito.anyString());
    }

    @Test
    void dequeueInvalidMessage() {
        var assertThrows = assertThrows(NoSuchElementException.class,
                () -> {
                    uploadEventConsumer.listen(uploadEventMessageInvalidMock);
                });
        assertInstanceOf(NoSuchElementException.class, assertThrows);

    }
}
