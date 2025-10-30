# Chat Application with LangGraph4j# Chat Application with LangGraph4j# Chat Application with LangGraph4j Checkpoints



Spring Boot приложение для управления чат-сессиями с использованием **LangGraph4j 1.6.3** и **PostgreSQL** для хранения checkpoint'ов.



## 🎯 ОсобенностиSpring Boot приложение для управления чат-сессиями с использованием **LangGraph4j 1.6.3** и **PostgreSQL** для хранения checkpoint'ов.This is a Spring Boot application that demonstrates how to save and manage chat checkpoints using langgraph4j-postgres-saver.



- **LangGraph4j PostgresSaver**: Автоматическое сохранение состояний графа в PostgreSQL

- **Spring AI**: Интеграция с OpenAI для чат-функционала

- **Session-based isolation**: Каждая сессия имеет изолированную цепочку checkpoint'ов## 🎯 Особенности## Features

- **StateGraph**: Граф обработки сообщений с узлами `process_message` → `save_context`

- **Persistent chat context**: Контекст сохраняется по sessionId, не удаляется

- **Context reset**: Возможность сброса контекста через создание нового sessionId

- **LangGraph4j PostgresSaver**: Автоматическое сохранение состояний графа в PostgreSQL- Save chat checkpoints with context to PostgreSQL

## 🛠 Технологии

- **Spring AI**: Интеграция с OpenAI для чат-функционала- Load chat checkpoints by thread ID

- Java 17

- Spring Boot 3.2.0- **Thread-based isolation**: Каждый thread имеет изолированную цепочку checkpoint'ов- Delete chat checkpoints

- LangGraph4j 1.6.3 (PostgresSaver)

- PostgreSQL 15- **StateGraph**: Граф обработки сообщений с узлами `process_message` → `save_context`- REST API for checkpoint management

- Spring AI 1.0.0-M3

- Docker & Docker Compose- Integration with Spring AI for chat functionality



## 📋 Структура данных## 🛠 Технологии



### Входящий запрос## Prerequisites



```json- Java 17

{

  "chat": {- Spring Boot 3.2.0- Java 17 or higher

    "chatId": "00000000-0000-0000-0000-00002c19afe6",

    "sessionId": "3782d0e6-0ba2-4969-a867-d5c4448eqv23",- LangGraph4j 1.6.3 (PostgresSaver)- PostgreSQL database

    "messageId": "4782d0e6-0ba2-4969-a867-d5c4448eqv43"

  },- PostgreSQL 15- Maven

  "messages": [

    {- Spring AI 1.0.0-M3

      "role": "USER",

      "content": "Как получить доступ в jira"- Docker & Docker Compose## Setup

    }

  ]

}

```## 📦 Структура проекта1. **Clone the repository**



### Параметры   ```bash



- **chatId** - Постоянный идентификатор пользователя (никогда не меняется)```   git clone <repository-url>

- **sessionId** - Идентификатор сессии чата (при сбросе контекста создается новый)

- **messageId** - Идентификатор сообщенияchatapp/   cd chatapp

- **role** - Роль отправителя: `USER`, `SYSTEM`, `ASSISTANT`, `FUNCTION`

├── src/main/java/com/example/chatapp/   ```

## 📦 Структура проекта

│   ├── ChatApplication.java           # Главный класс приложения

```

chatapp/│   ├── config/2. **Configure PostgreSQL**

├── src/main/java/com/example/chatapp/

│   ├── ChatApplication.java           # Главный класс приложения│   │   ├── LangGraphConfig.java       # PostgresSaver bean   - Create a database named `chat_db`

│   ├── config/

│   │   ├── LangGraphConfig.java       # PostgresSaver bean│   │   └── ChatGraphConfig.java       # StateGraph конфигурация   - Update `src/main/resources/application.properties` with your database credentials:

│   │   └── ChatGraphConfig.java       # StateGraph конфигурация

│   ├── controller/│   ├── controller/     ```properties

│   │   ├── ChatController.java        # REST API для AI чата

│   │   └── ChatCheckpointController.java  # REST API для checkpoint'ов│   │   ├── ChatController.java        # REST API для чата     spring.datasource.url=jdbc:postgresql://localhost:5432/chat_db

│   ├── model/

│   │   ├── ChatCheckpoint.java        # Модель checkpoint│   │   └── ChatCheckpointController.java  # REST API для checkpoint'ов     spring.datasource.username=your_username

│   │   ├── ChatInfo.java              # Модель chat/session/message ID

│   │   ├── ChatMessage.java           # Модель сообщения│   ├── model/     spring.datasource.password=your_password

│   │   └── ChatRequest.java           # Модель запроса

│   ├── service/│   │   └── ChatCheckpoint.java        # Модель checkpoint     ```

│   │   ├── ChatService.java           # Сервис AI чата

│   │   └── ChatCheckpointService.java # Сервис checkpoint'ов (LangGraph4j)│   ├── service/

│   └── state/

│       └── ChatAgentState.java        # AgentState для LangGraph4j│   │   ├── ChatService.java           # Сервис чата3. **Build the application**

├── src/main/resources/

│   └── application.properties         # Конфигурация приложения│   │   └── ChatCheckpointService.java # Сервис checkpoint'ов (LangGraph4j)   ```bash

├── docker-compose.yml                 # PostgreSQL контейнер

└── pom.xml                           # Maven зависимости│   └── state/   mvn clean install

```

│       └── ChatAgentState.java        # AgentState для LangGraph4j   ```

## 🚀 Быстрый старт

├── src/main/resources/

### 1. Запуск PostgreSQL

│   └── application.properties         # Конфигурация приложения4. **Run the application**

```bash

docker-compose up -d├── docker-compose.yml                 # PostgreSQL контейнер   ```bash

```

└── pom.xml                           # Maven зависимости   mvn spring-boot:run

### 2. Запуск приложения

```   ```

```bash

mvn spring-boot:run

```

## 🚀 Быстрый стартThe application will start on `http://localhost:8080`.

Приложение будет доступно на `http://localhost:8081`



### 3. Тестирование API

### 1. Запуск PostgreSQL## API Endpoints

#### Сохранить сообщение:

```bash

curl -X POST http://localhost:8081/api/chat/message \

  -H "Content-Type: application/json" \```bash### Checkpoint Management

  -d '{

    "chat": {docker-compose up -d

      "chatId": "00000000-0000-0000-0000-00002c19afe6",

      "sessionId": "3782d0e6-0ba2-4969-a867-d5c4448eqv23",```- **POST** `/api/chat/checkpoints` - Save a checkpoint

      "messageId": "4782d0e6-0ba2-4969-a867-d5c4448eqv43"

    },- **GET** `/api/chat/checkpoints/{threadId}` - Load latest checkpoint for thread

    "messages": [

      {### 2. Запуск приложения- **GET** `/api/chat/checkpoints/{threadId}/{checkpointId}` - Load specific checkpoint

        "role": "USER",

        "content": "Как получить доступ в jira"- **DELETE** `/api/chat/checkpoints/{threadId}` - Delete all checkpoints for thread

      }

    ]```bash- **DELETE** `/api/chat/checkpoints/{threadId}/{checkpointId}` - Delete specific checkpoint

  }'

```mvn spring-boot:run- **HEAD** `/api/chat/checkpoints/{threadId}` - Check if checkpoint exists



#### Обработать сообщение с AI (требует OpenAI API key):```

```bash

curl -X POST http://localhost:8081/api/chat/process \### Chat Processing

  -H "Content-Type: application/json" \

  -d '{Приложение будет доступно на `http://localhost:8081`

    "chat": {

      "chatId": "00000000-0000-0000-0000-00002c19afe6",- **POST** `/api/chat/message` - Process a chat message and save checkpoint

      "sessionId": "3782d0e6-0ba2-4969-a867-d5c4448eqv23",

      "messageId": "4782d0e6-0ba2-4969-a867-d5c4448eqv43"### 3. Тестирование API

    },

    "messages": [## Usage Examples

      {

        "role": "USER",#### Сохранить checkpoint:

        "content": "Привет!"

      }```bash### Save a Checkpoint

    ]

  }'curl -X POST http://localhost:8081/api/chat/checkpoints \```bash

```

  -H "Content-Type: application/json" \curl -X POST http://localhost:8080/api/chat/checkpoints \

#### Загрузить сессию:

```bash  -d '{  -H "Content-Type: application/json" \

curl http://localhost:8081/api/chat/session/3782d0e6-0ba2-4969-a867-d5c4448eqv23

```    "threadId": "test-thread-1",  -d '{



#### Сбросить контекст (создать новую сессию):    "context": {    "threadId": "chat-session-123",

```bash

curl -X POST http://localhost:8081/api/chat/reset-context \      "messages": [    "context": {

  -H "Content-Type: application/json" \

  -d '{"chatId": "00000000-0000-0000-0000-00002c19afe6"}'        {"role": "user", "content": "Hello LangGraph4j!"}      "messages": [

```

      ]        {"role": "user", "content": "Hello"},

#### Удалить сессию:

```bash    }        {"role": "assistant", "content": "Hi there!"}

curl -X DELETE http://localhost:8081/api/chat/session/3782d0e6-0ba2-4969-a867-d5c4448eqv23

```  }'      ],



## 📚 LangGraph4j PostgresSaver```      "currentState": "waiting_for_input"



PostgresSaver автоматически:    }

- Создает необходимые таблицы в PostgreSQL при первом запуске

- Сохраняет все изменения состояния графа#### Загрузить checkpoint:  }'

- Обеспечивает изоляцию между разными сессиями (sessionId)

- Сериализует состояние с помощью `ObjectStreamStateSerializer````bash```



### Структура StateGraphcurl http://localhost:8081/api/chat/checkpoints/test-thread-1



``````### Load a Checkpoint

START → process_message → save_context → END

``````bash



- **process_message**: Обрабатывает входящие сообщения#### Получить историю:curl http://localhost:8080/api/chat/checkpoints/chat-session-123

- **save_context**: Сохраняет контекст с метаданными

```bash```

## 💡 Логика работы с контекстом

curl http://localhost:8081/api/chat/checkpoints/test-thread-1/history

1. **Постоянный chatId** - привязан к пользователю, не меняется

2. **SessionId для изоляции** - каждая сессия хранит свой контекст независимо```### Process a Chat Message

3. **Сброс контекста** - создается новый sessionId, старые данные остаются в базе

4. **MessageId** - уникальный идентификатор каждого сообщения```bash



### Пример workflow:## 📚 LangGraph4j PostgresSavercurl -X POST http://localhost:8080/api/chat/message \



```  -H "Content-Type: application/json" \

1. Пользователь отправляет сообщение с chatId=A, sessionId=S1

   → Создается checkpoint с историей сообщений для S1PostgresSaver автоматически:  -d '{



2. Пользователь продолжает диалог с тем же sessionId=S1- Создает необходимые таблицы в PostgreSQL при первом запуске    "threadId": "chat-session-123",

   → Checkpoint S1 обновляется, добавляются новые сообщения

- Сохраняет все изменения состояния графа    "message": "Tell me about Spring Boot"

3. Пользователь хочет начать новый диалог (сбросить контекст)

   → Вызывается /reset-context с chatId=A- Обеспечивает изоляцию между разными thread'ами  }'

   → Создается новый sessionId=S2

   → Checkpoint S1 остается в базе, S2 начинается с пустой истории- Сериализует состояние с помощью `ObjectStreamStateSerializer````



4. Пользователь может вернуться к старому диалогу

   → Отправляет сообщение с sessionId=S1

   → Загружается старый checkpoint S1 с полной историей### Структура StateGraph## Dependencies

```



## ⚙️ Конфигурация

```- Spring Boot 3.2.0

### application.properties

START → process_message → save_context → END- LangGraph4j 1.6.3 (org.bsc.langgraph4j)

```properties

# PostgreSQL (используется LangGraph4j)```- PostgreSQL Driver

spring.datasource.url=jdbc:postgresql://localhost:5432/chat_db

spring.datasource.username=chatapp- Spring AI Core

spring.datasource.password=chatapp_password

- **process_message**: Обрабатывает входящие сообщения

# Spring AI OpenAI

spring.ai.openai.api-key=${OPENAI_API_KEY:your-api-key-here}- **save_context**: Сохраняет контекст с метаданными## Project Structure

spring.ai.openai.chat.options.model=gpt-3.5-turbo



# Server

server.port=8081## ⚙️ Конфигурация```

```

src/

### docker-compose.yml

### application.properties├── main/

```yaml

services:│   ├── java/com/example/chatapp/

  postgres:

    image: postgres:15-alpine```properties│   │   ├── ChatApplication.java

    container_name: chatapp-postgres

    environment:# PostgreSQL (используется LangGraph4j)│   │   ├── config/

      POSTGRES_DB: chat_db

      POSTGRES_USER: chatappspring.datasource.url=jdbc:postgresql://localhost:5432/chat_db│   │   │   └── LangGraphConfig.java

      POSTGRES_PASSWORD: chatapp_password

    ports:spring.datasource.username=chatapp│   │   ├── controller/

      - "5432:5432"

```spring.datasource.password=chatapp_password│   │   │   ├── ChatCheckpointController.java



## 🧪 Разработка│   │   │   └── ChatController.java



### Компиляция# Spring AI OpenAI│   │   ├── model/



```bashspring.ai.openai.api-key=${OPENAI_API_KEY:your-api-key-here}│   │   │   └── ChatCheckpoint.java

mvn clean compile

```spring.ai.openai.chat.options.model=gpt-3.5-turbo│   │   └── service/



### Упаковка│   │       ├── ChatCheckpointService.java



```bash# Server│   │       └── ChatService.java

mvn clean package

```server.port=8081│   └── resources/



### Запуск тестов```│       └── application.properties



```bash└── test/

mvn test

```### docker-compose.yml    └── java/com/example/chatapp/



## 📖 API Endpoints        └── ChatApplicationTests.java



### Chat Processing (с AI)```yaml```



- `POST /api/chat/process` - Обработать сообщение с OpenAI и сохранить checkpointservices:



### Checkpoint Management  postgres:## License



- `POST /api/chat/message` - Сохранить сообщение без AI обработки    image: postgres:15-alpine

- `GET /api/chat/session/{sessionId}` - Загрузить checkpoint по sessionId

- `DELETE /api/chat/session/{sessionId}` - Удалить checkpoint сессии    container_name: chatapp-postgresThis project is licensed under the MIT License.

- `HEAD /api/chat/session/{sessionId}` - Проверить существование сессии    environment:

      POSTGRES_DB: chat_db

### Context Management      POSTGRES_USER: chatapp

      POSTGRES_PASSWORD: chatapp_password

- `POST /api/chat/reset-context` - Сбросить контекст (создать новый sessionId)    ports:

      - "5432:5432"

## 🔍 Troubleshooting```



### PostgreSQL не запускается## 🧪 Разработка



```bash### Компиляция

docker ps

docker logs chatapp-postgres```bash

```mvn clean compile

```

### Приложение не подключается к БД

### Упаковка

Проверьте, что PostgreSQL запущен и доступен:

```bash```bash

docker exec -it chatapp-postgres psql -U chatapp -d chat_db -c "SELECT 1"mvn clean package

``````



### Просмотр таблиц LangGraph4j### Запуск тестов



```bash```bash

docker exec -it chatapp-postgres psql -U chatapp -d chat_db -c "\dt"mvn test

``````



### Просмотр checkpoint'ов## 📖 API Endpoints



```bash### Checkpoint Management

docker exec -it chatapp-postgres psql -U chatapp -d chat_db -c "SELECT * FROM checkpoints LIMIT 10;"

```- `POST /api/chat/checkpoints` - Сохранить checkpoint

- `GET /api/chat/checkpoints/{threadId}` - Загрузить checkpoint

## 📝 Лицензия- `GET /api/chat/checkpoints/{threadId}/history` - История checkpoint'ов

- `DELETE /api/chat/checkpoints/{threadId}` - Удалить checkpoint

MIT License- `HEAD /api/chat/checkpoints/{threadId}` - Проверить существование


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
