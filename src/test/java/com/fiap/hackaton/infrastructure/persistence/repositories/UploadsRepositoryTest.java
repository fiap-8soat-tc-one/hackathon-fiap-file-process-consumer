package com.fiap.hackaton.infrastructure.persistence.repositories;

import br.com.six2six.fixturefactory.Fixture;
import com.fiap.hackaton.fixture.FixtureTest;
import com.fiap.hackaton.infrastructure.persistence.entities.Uploads;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UploadsRepositoryTest extends FixtureTest {
    @Mock
    private DynamoDbTemplate dynamoDbTemplateMock;
    @InjectMocks
    private UploadsRepository uploadsRepository;

    private Uploads uploadsMock;

    @BeforeEach
    void setUp() {
        uploadsMock = Fixture.from(Uploads.class).gimme("valid");
    }

    @Test
    void findById() {
        var id = uploadsMock.getId().toString();
        Mockito.when(dynamoDbTemplateMock.load(Mockito.any(), Mockito.any())).thenReturn(uploadsMock);
        var uploads = uploadsRepository.findById(id);

        verify(dynamoDbTemplateMock, Mockito.times(1)).load(Mockito.any(), Mockito.any());
        assertNotNull(uploads);

    }

    @Test
    void notFoundId() {
        var id = uploadsMock.getId().toString();
        Mockito.when(dynamoDbTemplateMock.load(Mockito.any(), Mockito.any())).thenReturn(null);
        var uploads = uploadsRepository.findById(id);

        verify(dynamoDbTemplateMock, Mockito.times(1)).load(Mockito.any(), Mockito.any());
        assertTrue(uploads.isEmpty());

    }

    @Test
    void save() {
        uploadsRepository.save(uploadsMock);
        verify(dynamoDbTemplateMock, Mockito.times(1)).save(Mockito.any());
    }


}