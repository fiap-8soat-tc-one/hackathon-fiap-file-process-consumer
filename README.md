# Processador de Arquivos - FIAP Hackathon

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=fiap-8soat-tc-one_hackathon-fiap-file-process-consumer&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=fiap-8soat-tc-one_hackathon-fiap-file-process-consumer)
[![Build and Publish image to ECR](https://github.com/fiap-8soat-tc-one/hackathon-fiap-file-process-consumer/actions/workflows/build.yml/badge.svg)](https://github.com/fiap-8soat-tc-one/hackathon-fiap-file-process-consumer/actions/workflows/build.yml)

Este é um serviço desenvolvido em Spring Boot que integra um sistema maior de processamento de arquivos de vídeo. Sua principal responsabilidade é processar vídeos de forma assíncrona, gerando snapshots (capturas de tela) em momentos específicos do vídeo e disponibilizando essas imagens em um arquivo compactado.

## 📋 Índice

1. [Visão Geral](#visão-geral)
2. [Arquitetura](#arquitetura)
3. [Tecnologias](#tecnologias)
4. [Estrutura do Projeto](#estrutura-do-projeto)
5. [Configuração do Ambiente](#configuração-do-ambiente)
6. [Fluxo de Processamento](#fluxo-de-processamento)
7. [Integração com AWS](#integração-com-aws)
8. [Deployment](#deployment)

## 🎯 Visão Geral

O serviço é parte de uma arquitetura orientada a eventos que processa arquivos de vídeo de forma assíncrona. Principais funcionalidades:

- Consumo de eventos de upload de arquivos via Amazon SQS
- Download automático de vídeos do Amazon S3
- Geração de snapshots de vídeos usando JavaCV
- Compactação de snapshots em arquivo ZIP
- Upload do resultado para Amazon S3
- Gerenciamento de estado no Amazon DynamoDB
- Notificação de resultados via Amazon SQS

## 🏗 Arquitetura

O projeto segue os princípios da Arquitetura Limpa (Clean Architecture):

### Camadas

1. **Domain Layer** (`domain/`)
   - Entidades core do negócio
   - Enums e exceções
   - Regras de negócio independentes

2. **Application Layer** (`application/`)
   - Casos de uso
   - Interfaces (gateways/ports)
   - Regras de aplicação

3. **Infrastructure Layer** (`infrastructure/`)
   - Implementações concretas
   - Adaptadores para serviços externos
   - Configurações

### Diagrama de Classes

```mermaid
classDiagram
    class ProcessFileUploadUseCase {
        +execute(String key)
    }
    
    class ProcessFileUploadSpec {
        <<interface>>
        +execute(String key)
    }
    
    class ProcessFileUploadGateway {
        +execute(String key)
    }
    
    class UploadEventConsumer {
        +listen(UploadEventMessage message)
    }
    
    class ScreenshotService {
        +generate(String fileName)
    }
    
    class StorageClientService {
        +download(String fileName)
        +upload(String fileName, InputStream file, String contentType)
        +remove(String fileName)
    }
    
    class NotificationEventService {
        +notifyError(String id, String message)
        +notifySuccess(String id, String status)
    }
    
    ProcessFileUploadUseCase ..> ProcessFileUploadSpec
    ProcessFileUploadGateway ..|> ProcessFileUploadSpec
    UploadEventConsumer --> ProcessFileUploadUseCase
    ProcessFileUploadGateway --> ScreenshotService
    ProcessFileUploadGateway --> StorageClientService
    ProcessFileUploadGateway --> NotificationEventService
```

### Diagrama de Sequência do Processamento

```mermaid
sequenceDiagram
    participant SQS
    participant Consumer as UploadEventConsumer
    participant UseCase as ProcessFileUploadUseCase
    participant Gateway as ProcessFileUploadGateway
    participant S3
    participant Screenshot as ScreenshotService
    participant DynamoDB
    participant Notification as NotificationEventService

    SQS->>Consumer: Evento de Upload
    Consumer->>UseCase: Processa Mensagem
    UseCase->>Gateway: Executa Processamento
    Gateway->>S3: Download do Vídeo
    Gateway->>Screenshot: Gera Snapshots
    Screenshot->>S3: Upload do ZIP
    Gateway->>DynamoDB: Atualiza Status
    Gateway->>Notification: Notifica Conclusão
    Notification->>SQS: Envia Notificação
```

## 🛠️ Tecnologias

### Core
- Java 21
- Spring Boot 3.2.0
- Spring Cloud AWS 3.2.0
- JavaCV 1.5.9 (processamento de vídeo)
- Project Lombok

### AWS
- Amazon S3 - Armazenamento
- Amazon SQS - Mensageria
- Amazon DynamoDB - Persistência
- Amazon ECR - Registro de Containers
- Amazon EKS - Orquestração de Containers

### DevOps
- Docker/Docker Compose
- Kubernetes
- GitHub Actions
- SonarCloud
- Maven

### Desenvolvimento Local
- LocalStack 4.0.3

## 📁 Estrutura do Projeto

```
src/
├── main/
│   ├── java/
│   │   └── com/fiap/hackaton/
│   │       ├── application/
│   │       │   ├── gateways/
│   │       │   └── usecases/
│   │       ├── domain/
│   │       │   ├── enums/
│   │       │   └── exceptions/
│   │       └── infrastructure/
│   │           ├── core/
│   │           ├── gateways/
│   │           ├── persistence/
│   │           ├── presentation/
│   │           └── services/
│   │           └── utils/
│   └── resources/
│       └── application.yml
│       └── application-local.yml
│       └── application-prod.yml
```

### Principais Componentes

- `UploadEventConsumer`: Consumidor de eventos SQS
- `ScreenshotService`: Serviço de processamento de vídeo
- `StorageClientService`: Cliente S3
- `NotificationEventService`: Serviço de notificação
- `UploadServiceDb`: Gerenciamento de uploads no DynamoDB

## ⚙️ Configuração do Ambiente

### Pré-requisitos

- Java 21
- Docker e Docker Compose
- Maven
- AWS CLI (opcional)

### Variáveis de Ambiente

```properties
SPRING_PROFILES_ACTIVE=local
AWS_DEFAULT_REGION=us-east-1
AWS_ACCESS_KEY_ID=fakeAccessKeyId
AWS_SECRET_ACCESS_KEY=fakeSecretAccessKey
AWS_REGION=us-east-1
DYNAMO_DB_ENDPOINT=https://localhost.localstack.cloud:4566
SQS_ENDPOINT=https://localhost.localstack.cloud:4566
S3_ENDPOINT=https://localhost.localstack.cloud:4566
```

### LocalStack

Recursos criados automaticamente:
```bash
# Bucket S3
bucket-fiap-hackaton-t32-files

# Filas SQS
upload-event-queue
notification-event-queue

# Tabela DynamoDB
fiap-hackaton-uploads
```

### Docker Compose

```bash
# Iniciar ambiente
docker-compose up -d

# Verificar logs
docker-compose logs -f
```

## 🔄 Fluxo de Processamento

1. **Recebimento do Evento**
   - Evento recebido via SQS com dados do arquivo
   - Formato da mensagem processado pelo `UploadEventMessage`

2. **Validação**
   - Verificação de extensão (.mp4)
   - Verificação de registro no DynamoDB

3. **Processamento**
   - Download do vídeo do S3
   - Geração de 5 snapshots em intervalos regulares
   - Criação de arquivo ZIP com os snapshots
   - Upload do ZIP para S3
   - Atualização de status no DynamoDB

4. **Notificação**
   - Envio de evento de conclusão via SQS
   - Status atualizado para PROCESSED/ERROR

### Estados de Processamento

- `PENDING`: Inicial, aguardando processamento
- `PROCESSED`: Processamento concluído com sucesso
- `ERROR`: Erro durante o processamento
- `NOTIFIED`: Notificação enviada com sucesso

## ☁️ Integração com AWS

### Amazon S3
- Bucket: `bucket-fiap-hackaton-t32-files`
- Estrutura de pastas:
  - `/videos` - Vídeos originais
  - `/zips` - Arquivos processados

### Amazon SQS
- `upload-event-queue`: Eventos de upload
- `notification-event-queue`: Notificações de processamento

### Amazon DynamoDB
- Tabela: `fiap-hackaton-uploads`
- Campos:
  - `id` (UUID - Partition Key)
  - `email` (String)
  - `status` (String)
  - `urlDownload` (String)
  - `createdAt` (Timestamp)

## 🚀 Deployment

### Build

```bash
# Maven
./mvnw clean package

# Docker
docker build -t file-process-consumer .
```

### Kubernetes

```bash
# Aplicar configuração
kubectl apply -f k8sdeploy.yaml

# Verificar pods
kubectl get pods -n fiap
```

### GitHub Actions

Pipeline automatizado para:
1. Build do projeto
2. Análise SonarCloud
3. Build da imagem Docker
4. Push para Amazon ECR
5. Deploy no Amazon EKS

## 📊 Monitoramento

- Health checks via Spring Actuator
- Logs estruturados
- Métricas de processamento
- Análise de qualidade via SonarCloud

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request
