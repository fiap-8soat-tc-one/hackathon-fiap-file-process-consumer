package com.fiap.hackaton.fixture.templates;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import com.fiap.hackaton.domain.enums.UploadStatus;
import com.fiap.hackaton.infrastructure.persistence.entities.Uploads;
import com.fiap.hackaton.infrastructure.presentation.workers.dto.*;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

public class UploadFileProcessTemplates implements TemplateLoader {

    @Override
    public void load() {

        Fixture.of(UploadEventMessage.class).addTemplate("valid", new Rule() {
            {
                add("records", has(1).of(S3Record.class, "valid"));
            }
        });

        Fixture.of(UploadEventMessage.class).addTemplate("invalid", new Rule() {
            {
                add("records", Collections.emptyList());
            }
        });

        Fixture.of(S3Record.class).addTemplate("valid", new Rule() {
            {
                add("s3", one(S3Detail.class, "valid"));
            }
        });

        Fixture.of(S3Detail.class).addTemplate("valid", new Rule() {
            {
                add("bucket", one(S3Bucket.class, "valid"));
                add("object", one(S3Object.class, "valid"));

            }
        });

        Fixture.of(S3Bucket.class).addTemplate("valid", new Rule() {
            {
                add("name", "bucket-fiap-hackaton-t32-files");
            }
        });

        Fixture.of(S3Object.class).addTemplate("valid", new Rule() {
            {
                add("key", "e-mail/test%40test.com/390f8e17-c586-4335-8054-196429bc8923.mp4");
            }
        });
        Fixture.of(S3Object.class).addTemplate("valid-simple", new Rule() {
            {
                add("key", "390f8e17-c586-4335-8054-196429bc8923.mp4");
            }
        });

        Fixture.of(S3Object.class).addTemplate("invalid", new Rule() {
            {
                add("key", "e-mail/test%40test.com/390f8e17-c586-4335-8054-196429bc8923.zip");
            }
        });

        Fixture.of(Uploads.class).addTemplate("valid", new Rule() {
            {
                add("id", UUID.fromString("390f8e17-c586-4335-8054-196429bc8926"));
                add("email", "teste@teste.com");
                add("status", UploadStatus.PENDING.name());
                add("urlDownload", "http://test-bucket/390f8e17-c586-4335-8054-196429bc8926.mp4");
                add("createdAt", Instant.now());
            }
        });


    }
}
