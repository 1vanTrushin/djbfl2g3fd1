# API Testing Examples

## Prerequisites
- Ensure PostgreSQL Docker container is running: `docker ps`
- Ensure Spring Boot application is running on port 8081

## Test Endpoints

### 1. Save a Checkpoint

```powershell
# PowerShell
$body = @{
    threadId = "test-thread-1"
    context = @{
        messages = @(
            @{ role = "user"; content = "Hello" }
            @{ role = "assistant"; content = "Hi there!" }
        )
        currentState = "waiting_for_input"
    }
} | ConvertTo-Json -Depth 10

Invoke-RestMethod -Uri "http://localhost:8081/api/chat/checkpoints" `
    -Method POST `
    -ContentType "application/json" `
    -Body $body
```

```bash
# Bash/CMD (using curl)
curl -X POST http://localhost:8081/api/chat/checkpoints \
  -H "Content-Type: application/json" \
  -d "{\"threadId\": \"test-thread-1\", \"context\": {\"messages\": [{\"role\": \"user\", \"content\": \"Hello\"}, {\"role\": \"assistant\", \"content\": \"Hi there!\"}], \"currentState\": \"waiting_for_input\"}}"
```

### 2. Load a Checkpoint

```powershell
# PowerShell
Invoke-RestMethod -Uri "http://localhost:8081/api/chat/checkpoints/test-thread-1" -Method GET
```

```bash
# Bash/CMD
curl http://localhost:8081/api/chat/checkpoints/test-thread-1
```

### 3. Check if Checkpoint Exists

```powershell
# PowerShell
Invoke-WebRequest -Uri "http://localhost:8081/api/chat/checkpoints/test-thread-1" -Method HEAD
```

```bash
# Bash/CMD
curl -I http://localhost:8081/api/chat/checkpoints/test-thread-1
```

### 4. Delete Checkpoint

```powershell
# PowerShell
Invoke-RestMethod -Uri "http://localhost:8081/api/chat/checkpoints/test-thread-1" -Method DELETE
```

```bash
# Bash/CMD
curl -X DELETE http://localhost:8081/api/chat/checkpoints/test-thread-1
```

### 5. Send Chat Message (requires OpenAI API key)

```powershell
# PowerShell
$chatBody = @{
    threadId = "chat-session-1"
    message = "Tell me about Spring Boot"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8081/api/chat/message" `
    -Method POST `
    -ContentType "application/json" `
    -Body $chatBody
```

```bash
# Bash/CMD
curl -X POST http://localhost:8081/api/chat/message \
  -H "Content-Type: application/json" \
  -d "{\"threadId\": \"chat-session-1\", \"message\": \"Tell me about Spring Boot\"}"
```

## Verify Data in PostgreSQL

### Connect to database:
```powershell
docker exec -it chatapp-postgres psql -U chatapp -d chat_db
```

### View all checkpoints:
```sql
SELECT id, thread_id, checkpoint_id, created_at 
FROM checkpoints 
ORDER BY created_at DESC;
```

### View checkpoint data:
```sql
SELECT id, thread_id, checkpoint_data 
FROM checkpoints 
WHERE thread_id = 'test-thread-1';
```

### Count checkpoints:
```sql
SELECT COUNT(*) FROM checkpoints;
```

### Delete all test data:
```sql
DELETE FROM checkpoints WHERE thread_id LIKE 'test-%';
```

## Complete Test Scenario

```powershell
# 1. Save first checkpoint
$checkpoint1 = @{
    threadId = "demo-thread"
    context = @{
        messages = @(@{ role = "user"; content = "What is Docker?" })
        step = 1
    }
} | ConvertTo-Json -Depth 10

Invoke-RestMethod -Uri "http://localhost:8081/api/chat/checkpoints" `
    -Method POST -ContentType "application/json" -Body $checkpoint1

# 2. Save second checkpoint (updates the first one)
$checkpoint2 = @{
    threadId = "demo-thread"
    context = @{
        messages = @(
            @{ role = "user"; content = "What is Docker?" }
            @{ role = "assistant"; content = "Docker is a containerization platform" }
        )
        step = 2
    }
} | ConvertTo-Json -Depth 10

Invoke-RestMethod -Uri "http://localhost:8081/api/chat/checkpoints" `
    -Method POST -ContentType "application/json" -Body $checkpoint2

# 3. Load the checkpoint
$loaded = Invoke-RestMethod -Uri "http://localhost:8081/api/chat/checkpoints/demo-thread" -Method GET
$loaded | ConvertTo-Json -Depth 10

# 4. Check if exists
$exists = Invoke-WebRequest -Uri "http://localhost:8081/api/chat/checkpoints/demo-thread" -Method HEAD
Write-Host "Status Code: $($exists.StatusCode)"

# 5. Delete checkpoint
Invoke-RestMethod -Uri "http://localhost:8081/api/chat/checkpoints/demo-thread" -Method DELETE

# 6. Verify deletion
try {
    Invoke-RestMethod -Uri "http://localhost:8081/api/chat/checkpoints/demo-thread" -Method GET
} catch {
    Write-Host "Checkpoint deleted successfully - returned 404"
}
```

## Expected Response Examples

### Successful Checkpoint Save:
```json
{
  "threadId": "test-thread-1",
  "checkpointId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "context": {
    "messages": [
      {"role": "user", "content": "Hello"},
      {"role": "assistant", "content": "Hi there!"}
    ],
    "currentState": "waiting_for_input"
  },
  "createdAt": "2025-10-30T05:49:00.123Z"
}
```

### Successful Checkpoint Load:
```json
{
  "messages": [
    {"role": "user", "content": "Hello"},
    {"role": "assistant", "content": "Hi there!"}
  ],
  "currentState": "waiting_for_input"
}
```

### Not Found (404):
```json
{}
```

## Troubleshooting

If requests fail:
1. Check if application is running: `netstat -an | findstr 8081`
2. Check application logs in the terminal
3. Check PostgreSQL is running: `docker ps`
4. Verify database connection: `docker exec -it chatapp-postgres pg_isready -U chatapp -d chat_db`
