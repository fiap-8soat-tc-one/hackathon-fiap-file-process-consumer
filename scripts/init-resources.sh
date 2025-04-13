#!/bin/bash
awslocal s3 mb s3://bucket-fiap-hackaton-t32-files
awslocal sqs create-queue --queue-name upload-event-queue
awslocal sqs create-queue --queue-name notification-event-queue
