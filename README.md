# Processador de Arquivos - FIAP Hackathon
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=fiap-8soat-tc-one_hackathon-fiap-file-process-consumer&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=fiap-8soat-tc-one_hackathon-fiap-file-process-consumer)
[![Build and Publish image to ECR](https://github.com/fiap-8soat-tc-one/hackathon-fiap-file-process-consumer/actions/workflows/build.yml/badge.svg)](https://github.com/fiap-8soat-tc-one/hackathon-fiap-file-process-consumer/actions/workflows/build.yml)

Este é um serviço desenvolvido em Spring Boot que processa arquivos de vídeo de forma assíncrona, gerando snapshots e notificando o resultado do processamento.

## 🚀 Funcionalidades

- Consumo de mensagens SQS para processamento de arquivos
- Download de arquivos do Amazon S3
- Geração de snapshots de vídeos
- Compactação dos snapshots em arquivo ZIP
- Upload do arquivo ZIP para o S3
- Atualização de status no DynamoDB
- Notificação do processamento via SQS

## 🛠️ Tecnologias Utilizadas

- Java 21
- Spring Boot 3.2.0
- Spring Cloud AWS
- JavaCV (para processamento de vídeo)
- LocalStack (para desenvolvimento local)
- Docker e Docker Compose
- Maven

## 📋 Pré-requisitos

- Java 21
- Docker e Docker Compose
- Maven

## 🔧 Configuração do Ambiente Local

1. Clone o repositório:
```bash
git clone <url-do-repositorio>
```

2. Configure as variáveis de ambiente (já definidas no arquivo .env):
```properties
AWS_DEFAULT_REGION=us-east-1
AWS_ACCESS_KEY_ID=fakeAccessKeyId
AWS_SECRET_ACCESS_KEY=fakeSecretAccessKey
AWS_REGION=us-east-1
DYNAMO_DB_ENDPOINT=https://localhost.localstack.cloud:4566
SQS_ENDPOINT=https://localhost.localstack.cloud:4566
S3_ENDPOINT=https://localhost.localstack.cloud:4566
```

3. Inicie o ambiente local com Docker Compose:
```bash
docker-compose up -d
```

## 🏃‍♂️ Fluxo da Aplicação

1. **Recebimento da Mensagem**
   - A aplicação recebe uma mensagem do SQS contendo informações sobre um arquivo carregado no S3

2. **Download e Processamento**
   - Faz o download do arquivo do S3
   - Processa o vídeo gerando 5 snapshots em intervalos regulares
   - Cria um arquivo ZIP contendo os snapshots

3. **Atualização e Notificação**
   - Faz upload do ZIP para o S3
   - Atualiza o status no DynamoDB
   - Envia mensagem de notificação para uma fila SQS

## 🗂️ Estrutura do Projeto

O projeto segue uma arquitetura limpa com as seguintes camadas:

- **Application**: Casos de uso e interfaces
- **Domain**: Entidades e regras de negócio
- **Infrastructure**: Implementações concretas
  - Persistência (DynamoDB)
  - Serviços (Processamento de arquivo, Storage)
  - Workers (Consumidores e Publishers SQS)

## ⚙️ Recursos AWS Utilizados

- **S3**: Armazenamento dos arquivos originais e processados
- **SQS**: Filas para eventos de upload e notificações
- **DynamoDB**: Armazenamento dos metadados de upload

## 🐳 LocalStack

O projeto utiliza LocalStack para simular os serviços AWS localmente. Os recursos necessários são criados automaticamente através do script `init-resources.sh`:

- Bucket S3: `bucket-fiap-hackaton-t32-files`
- Filas SQS: 
  - `upload-event-queue`
  - `notification-event-queue`

## 📝 Status de Upload

O sistema utiliza os seguintes status para controle do processamento:

- `PENDING`: Arquivo recebido, aguardando processamento
- `PROCESSED`: Arquivo processado com sucesso
- `NOTIFIED`: Notificação enviada

## 🛠️ Compilação e Execução

Para executar localmente com Maven:
```bash
./mvnw spring-boot:run
```

Para construir a imagem Docker:
```bash
docker build -t file-process-consumer .
```
