package com.fiap.hackaton.application.usecases;

import com.fiap.hackaton.application.gateways.ProcessFileUploadSpec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProcessFileUploadUseCaseTest {
    @Mock
    private ProcessFileUploadSpec processFileUploadSpec;
    @InjectMocks
    private ProcessFileUploadUseCase processFileUploadUseCase;

    @Test
    void processFileTest() {
        processFileUploadUseCase.execute("test-key");
        verify(processFileUploadSpec, times(1)).execute(anyString());
    }

}