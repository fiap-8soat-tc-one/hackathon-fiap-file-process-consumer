#!/bin/bash

set -e

awslocal s3 mb s3://bucket-fiap-hackaton-t32-files

awslocal sqs create-queue --queue-name upload-event-queue
awslocal sqs create-queue --queue-name notification-event-queue

awslocal dynamodb create-table \
    --table-name fiap-hackaton-uploads \
    --attribute-definitions \
        AttributeName=id,AttributeType=S \
        AttributeName=email,AttributeType=S \
        AttributeName=status_upload,AttributeType=S \
        AttributeName=url_download,AttributeType=S \
        AttributeName=data_criacao,AttributeType=S \
    --key-schema AttributeName=id,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST \
    --global-secondary-indexes "[{\"IndexName\": \"email-index\", \"KeySchema\": [{\"AttributeName\": \"email\", \"KeyType\": \"HASH\"}], \"Projection\": {\"ProjectionType\": \"ALL\"}}, {\"IndexName\": \"url_download-index\", \"KeySchema\": [{\"AttributeName\": \"url_download\", \"KeyType\": \"HASH\"}], \"Projection\": {\"ProjectionType\": \"ALL\"}}, {\"IndexName\": \"status-index\", \"KeySchema\": [{\"AttributeName\": \"status_upload\", \"KeyType\": \"HASH\"}], \"Projection\": {\"ProjectionType\": \"ALL\"}}, {\"IndexName\": \"data-criacao-index\", \"KeySchema\": [{\"AttributeName\": \"data_criacao\", \"KeyType\": \"HASH\"}], \"Projection\": {\"ProjectionType\": \"ALL\"}}]" \
    --endpoint-url=http://localhost:4566
