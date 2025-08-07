# Cloud Events Integration

This document describes the cloud events integration for the Waver Site application.

## Overview

The Waver Site frontend integrates with a cloud event processing service to handle tutorial generation requests. When a user enters a GitHub repository URL and clicks "Generate", the application sends a cloud event to the processing service.

## Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────────┐
│   Frontend      │───▶│   Next.js API    │───▶│   Cloud Event       │
│   (User Input)  │    │   (/api/generate)│    │   Processing Service│
└─────────────────┘    └──────────────────┘    └─────────────────────┘
```

## Cloud Event Format

The application sends cloud events with the following specification:

### Headers
- `Content-Type: application/json`
- `ce-specversion: 1.0`
- `ce-type: dev.shaaf.waver.processing.request`
- `ce-source: /waver-site-frontend`
- `ce-id: <unique-uuid>`

### Payload
```json
{
  "sourceUrl": "https://github.com/username/repository.git"
}
```

### Example cURL Command
```bash
curl -X POST http://localhost:8080/requests \
  -H "Content-Type: application/json" \
  -H "ce-specversion: 1.0" \
  -H "ce-type: dev.shaaf.waver.processing.request" \
  -H "ce-source: /waver-site-frontend" \
  -H "ce-id: $(uuidgen)" \
  -d '{
    "sourceUrl": "https://github.com/sshaaf/gpt-java-chatbot.git"
  }'
```

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `CLOUD_EVENT_SERVICE_URL` | URL of the cloud event processing service | `http://localhost:8080` |

### Local Development

```bash
# Set the cloud event service URL
export CLOUD_EVENT_SERVICE_URL="http://localhost:8080"

# Start the Next.js application
npm run dev
```

### OpenShift Deployment

The cloud event service URL is configured via ConfigMap:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: waver-site-config
data:
  cloud-event-service-url: "http://cloud-event-service:8080"
```

## API Endpoint

### POST /api/generate-tutorial

**Request Body:**
```json
{
  "repositoryUrl": "https://github.com/username/repository.git"
}
```

**Response:**
```json
{
  "message": "Tutorial generation scheduled successfully",
  "repositoryUrl": "https://github.com/username/repository.git",
  "eventId": "123e4567-e89b-12d3-a456-426614174000"
}
```

**Error Response:**
```json
{
  "message": "Please provide a valid GitHub repository URL"
}
```

## Testing

### Test Script

Use the provided test script to verify cloud event functionality:

```bash
# Test with default settings
./scripts/test-cloud-event.sh

# Test with custom service URL
CLOUD_EVENT_SERVICE_URL="http://your-service:8080" ./scripts/test-cloud-event.sh

# Test with custom repository
TEST_REPO_URL="https://github.com/your-username/your-repo.git" ./scripts/test-cloud-event.sh
```

### Using Makefile

```bash
# Test cloud event functionality
make test-cloud-event
```

### Manual Testing

1. Start the Next.js application:
   ```bash
   npm run dev
   ```

2. Open http://localhost:3000

3. Enter a GitHub repository URL in the "Generate Your Own Tutorial" section

4. Click "Generate"

5. Check the browser's developer tools Network tab to see the API call

6. Check the server logs for cloud event details

## Error Handling

### Common Issues

1. **Cloud Event Service Unavailable**
   - Error: `Failed to schedule tutorial generation. Please try again.`
   - Solution: Ensure the cloud event processing service is running

2. **Invalid GitHub URL**
   - Error: `Please provide a valid GitHub repository URL`
   - Solution: Ensure the URL follows the pattern: `https://github.com/username/repository`

3. **Network Connectivity**
   - Error: `Failed to connect to the server. Please try again.`
   - Solution: Check network connectivity and firewall settings

### Debugging

Enable detailed logging by checking the server console output:

```bash
# The API logs will show:
# - Cloud event service URL being used
# - Event ID being generated
# - Repository URL being processed
# - Response status from the cloud event service
```

## Security Considerations

1. **Input Validation**: GitHub URLs are validated to ensure they follow the correct format
2. **Error Handling**: Sensitive information is not exposed in error messages
3. **Rate Limiting**: Consider implementing rate limiting for the API endpoint
4. **Authentication**: The cloud event service should implement appropriate authentication

## Monitoring

### Logs to Monitor

- API endpoint access logs
- Cloud event service response times
- Error rates and types
- Event ID generation and tracking

### Metrics to Track

- Number of tutorial generation requests
- Success/failure rates
- Response times
- Cloud event service availability

## Troubleshooting

### Cloud Event Service Not Responding

1. Check if the service is running:
   ```bash
   curl -X GET http://localhost:8080/health
   ```

2. Verify the service URL configuration:
   ```bash
   echo $CLOUD_EVENT_SERVICE_URL
   ```

3. Test connectivity:
   ```bash
   telnet localhost 8080
   ```

### Invalid Cloud Event Format

1. Check the cloud event headers are correct
2. Verify the payload structure matches the expected format
3. Ensure the event ID is a valid UUID

### GitHub URL Validation

The application validates GitHub URLs using the pattern:
```
^https://github\.com/[^\/]+\/[^\/]+$
```

This ensures:
- HTTPS protocol is used
- GitHub.com domain
- Username and repository name are present
- No trailing slashes or additional path components
