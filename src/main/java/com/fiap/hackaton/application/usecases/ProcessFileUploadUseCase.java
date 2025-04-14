package com.fiap.hackaton.application.usecases;

import com.fiap.hackaton.application.gateways.ProcessFileUploadSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessFileUploadUseCase {
    private final ProcessFileUploadSpec processFileUploadSpec;

    public void execute(String key) {
        processFileUploadSpec.execute(key);
    }
}
