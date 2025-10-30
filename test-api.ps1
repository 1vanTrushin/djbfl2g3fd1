# Test the chat API
$body = Get-Content -Path "test-request2.json" -Raw
$headers = @{
    "Content-Type" = "application/json"
}

Write-Host "Testing POST /api/chat/message..." -ForegroundColor Green
$response = Invoke-WebRequest -Uri "http://localhost:8081/api/chat/message" `
    -Method POST `
    -Headers $headers `
    -Body $body `
    -UseBasicParsing

Write-Host "Status Code: $($response.StatusCode)" -ForegroundColor Cyan
Write-Host "Response Body:" -ForegroundColor Cyan
$response.Content | ConvertFrom-Json | ConvertTo-Json -Depth 10

Write-Host "`nTesting GET /api/chat/session/22222222-2222-2222-2222-222222222222..." -ForegroundColor Green
$response2 = Invoke-WebRequest -Uri "http://localhost:8081/api/chat/session/22222222-2222-2222-2222-222222222222" `
    -Method GET `
    -UseBasicParsing

Write-Host "Status Code: $($response2.StatusCode)" -ForegroundColor Cyan
Write-Host "Response Body:" -ForegroundColor Cyan
$response2.Content | ConvertFrom-Json | ConvertTo-Json -Depth 10
