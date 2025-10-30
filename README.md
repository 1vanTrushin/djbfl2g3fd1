# Chat Application with LangGraph4j# Chat Application with LangGraph4j Checkpoints



Spring Boot приложение для управления чат-сессиями с использованием **LangGraph4j 1.6.3** и **PostgreSQL** для хранения checkpoint'ов.This is a Spring Boot application that demonstrates how to save and manage chat checkpoints using langgraph4j-postgres-saver.



## 🎯 Особенности## Features



- **LangGraph4j PostgresSaver**: Автоматическое сохранение состояний графа в PostgreSQL- Save chat checkpoints with context to PostgreSQL

- **Spring AI**: Интеграция с OpenAI для чат-функционала- Load chat checkpoints by thread ID

- **Thread-based isolation**: Каждый thread имеет изолированную цепочку checkpoint'ов- Delete chat checkpoints

- **StateGraph**: Граф обработки сообщений с узлами `process_message` → `save_context`- REST API for checkpoint management

- Integration with Spring AI for chat functionality

## 🛠 Технологии

## Prerequisites

- Java 17

- Spring Boot 3.2.0- Java 17 or higher

- LangGraph4j 1.6.3 (PostgresSaver)- PostgreSQL database

- PostgreSQL 15- Maven

- Spring AI 1.0.0-M3

- Docker & Docker Compose## Setup



## 📦 Структура проекта1. **Clone the repository**

   ```bash

```   git clone <repository-url>

chatapp/   cd chatapp

├── src/main/java/com/example/chatapp/   ```

│   ├── ChatApplication.java           # Главный класс приложения

│   ├── config/2. **Configure PostgreSQL**

│   │   ├── LangGraphConfig.java       # PostgresSaver bean   - Create a database named `chat_db`

│   │   └── ChatGraphConfig.java       # StateGraph конфигурация   - Update `src/main/resources/application.properties` with your database credentials:

│   ├── controller/     ```properties

│   │   ├── ChatController.java        # REST API для чата     spring.datasource.url=jdbc:postgresql://localhost:5432/chat_db

│   │   └── ChatCheckpointController.java  # REST API для checkpoint'ов     spring.datasource.username=your_username

│   ├── model/     spring.datasource.password=your_password

│   │   └── ChatCheckpoint.java        # Модель checkpoint     ```

│   ├── service/

│   │   ├── ChatService.java           # Сервис чата3. **Build the application**

│   │   └── ChatCheckpointService.java # Сервис checkpoint'ов (LangGraph4j)   ```bash

│   └── state/   mvn clean install

│       └── ChatAgentState.java        # AgentState для LangGraph4j   ```

├── src/main/resources/

│   └── application.properties         # Конфигурация приложения4. **Run the application**

├── docker-compose.yml                 # PostgreSQL контейнер   ```bash

└── pom.xml                           # Maven зависимости   mvn spring-boot:run

```   ```



## 🚀 Быстрый стартThe application will start on `http://localhost:8080`.



### 1. Запуск PostgreSQL## API Endpoints



```bash### Checkpoint Management

docker-compose up -d

```- **POST** `/api/chat/checkpoints` - Save a checkpoint

- **GET** `/api/chat/checkpoints/{threadId}` - Load latest checkpoint for thread

### 2. Запуск приложения- **GET** `/api/chat/checkpoints/{threadId}/{checkpointId}` - Load specific checkpoint

- **DELETE** `/api/chat/checkpoints/{threadId}` - Delete all checkpoints for thread

```bash- **DELETE** `/api/chat/checkpoints/{threadId}/{checkpointId}` - Delete specific checkpoint

mvn spring-boot:run- **HEAD** `/api/chat/checkpoints/{threadId}` - Check if checkpoint exists

```

### Chat Processing

Приложение будет доступно на `http://localhost:8081`

- **POST** `/api/chat/message` - Process a chat message and save checkpoint

### 3. Тестирование API

## Usage Examples

#### Сохранить checkpoint:

```bash### Save a Checkpoint

curl -X POST http://localhost:8081/api/chat/checkpoints \```bash

  -H "Content-Type: application/json" \curl -X POST http://localhost:8080/api/chat/checkpoints \

  -d '{  -H "Content-Type: application/json" \

    "threadId": "test-thread-1",  -d '{

    "context": {    "threadId": "chat-session-123",

      "messages": [    "context": {

        {"role": "user", "content": "Hello LangGraph4j!"}      "messages": [

      ]        {"role": "user", "content": "Hello"},

    }        {"role": "assistant", "content": "Hi there!"}

  }'      ],

```      "currentState": "waiting_for_input"

    }

#### Загрузить checkpoint:  }'

```bash```

curl http://localhost:8081/api/chat/checkpoints/test-thread-1

```### Load a Checkpoint

```bash

#### Получить историю:curl http://localhost:8080/api/chat/checkpoints/chat-session-123

```bash```

curl http://localhost:8081/api/chat/checkpoints/test-thread-1/history

```### Process a Chat Message

```bash

## 📚 LangGraph4j PostgresSavercurl -X POST http://localhost:8080/api/chat/message \

  -H "Content-Type: application/json" \

PostgresSaver автоматически:  -d '{

- Создает необходимые таблицы в PostgreSQL при первом запуске    "threadId": "chat-session-123",

- Сохраняет все изменения состояния графа    "message": "Tell me about Spring Boot"

- Обеспечивает изоляцию между разными thread'ами  }'

- Сериализует состояние с помощью `ObjectStreamStateSerializer````



### Структура StateGraph## Dependencies



```- Spring Boot 3.2.0

START → process_message → save_context → END- LangGraph4j 1.6.3 (org.bsc.langgraph4j)

```- PostgreSQL Driver

- Spring AI Core

- **process_message**: Обрабатывает входящие сообщения

- **save_context**: Сохраняет контекст с метаданными## Project Structure



## ⚙️ Конфигурация```

src/

### application.properties├── main/

│   ├── java/com/example/chatapp/

```properties│   │   ├── ChatApplication.java

# PostgreSQL (используется LangGraph4j)│   │   ├── config/

spring.datasource.url=jdbc:postgresql://localhost:5432/chat_db│   │   │   └── LangGraphConfig.java

spring.datasource.username=chatapp│   │   ├── controller/

spring.datasource.password=chatapp_password│   │   │   ├── ChatCheckpointController.java

│   │   │   └── ChatController.java

# Spring AI OpenAI│   │   ├── model/

spring.ai.openai.api-key=${OPENAI_API_KEY:your-api-key-here}│   │   │   └── ChatCheckpoint.java

spring.ai.openai.chat.options.model=gpt-3.5-turbo│   │   └── service/

│   │       ├── ChatCheckpointService.java

# Server│   │       └── ChatService.java

server.port=8081│   └── resources/

```│       └── application.properties

└── test/

### docker-compose.yml    └── java/com/example/chatapp/

        └── ChatApplicationTests.java

```yaml```

services:

  postgres:## License

    image: postgres:15-alpine

    container_name: chatapp-postgresThis project is licensed under the MIT License.
    environment:
      POSTGRES_DB: chat_db
      POSTGRES_USER: chatapp
      POSTGRES_PASSWORD: chatapp_password
    ports:
      - "5432:5432"
```

## 🧪 Разработка

### Компиляция

```bash
mvn clean compile
```

### Упаковка

```bash
mvn clean package
```

### Запуск тестов

```bash
mvn test
```

## 📖 API Endpoints

### Checkpoint Management

- `POST /api/chat/checkpoints` - Сохранить checkpoint
- `GET /api/chat/checkpoints/{threadId}` - Загрузить checkpoint
- `GET /api/chat/checkpoints/{threadId}/history` - История checkpoint'ов
- `DELETE /api/chat/checkpoints/{threadId}` - Удалить checkpoint
- `HEAD /api/chat/checkpoints/{threadId}` - Проверить существование

### Chat

- `POST /api/chat/message` - Отправить сообщение (требует OpenAI API key)

## 🔍 Troubleshooting

### PostgreSQL не запускается

```bash
docker ps
docker logs chatapp-postgres
```

### Приложение не подключается к БД

Проверьте, что PostgreSQL запущен и доступен:
```bash
docker exec -it chatapp-postgres psql -U chatapp -d chat_db -c "SELECT 1"
```

### Просмотр таблиц LangGraph4j

```bash
docker exec -it chatapp-postgres psql -U chatapp -d chat_db -c "\dt"
```

## 📝 Лицензия

MIT License
