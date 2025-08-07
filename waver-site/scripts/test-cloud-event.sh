#!/bin/bash

# Test Cloud Event Script
set -e

# Configuration
CLOUD_EVENT_SERVICE_URL=${CLOUD_EVENT_SERVICE_URL:-"http://localhost:8080"}
TEST_REPO_URL=${TEST_REPO_URL:-"https://github.com/sshaaf/gpt-java-chatbot.git"}

echo "🧪 Testing Cloud Event Functionality"
echo "===================================="

# Check if uuidgen is available
if ! command -v uuidgen &> /dev/null; then
    echo "❌ uuidgen is not available. Please install uuidgen or use a system that has it."
    exit 1
fi

# Generate event ID
EVENT_ID=$(uuidgen)

echo "📡 Cloud Event Service URL: $CLOUD_EVENT_SERVICE_URL"
echo "🔗 Test Repository URL: $TEST_REPO_URL"
echo "🆔 Event ID: $EVENT_ID"
echo ""

# Prepare the cloud event payload
PAYLOAD=$(cat <<EOF
{
  "sourceUrl": "$TEST_REPO_URL"
}
EOF
)

echo "📤 Sending Cloud Event..."
echo "Payload: $PAYLOAD"
echo ""

# Send the cloud event
RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
  -X POST "$CLOUD_EVENT_SERVICE_URL/requests" \
  -H "Content-Type: application/json" \
  -H "ce-specversion: 1.0" \
  -H "ce-type: dev.shaaf.waver.processing.request" \
  -H "ce-source: /test-script" \
  -H "ce-id: $EVENT_ID" \
  -d "$PAYLOAD")

# Extract HTTP status and response body
HTTP_STATUS=$(echo "$RESPONSE" | grep "HTTP_STATUS:" | cut -d: -f2)
RESPONSE_BODY=$(echo "$RESPONSE" | sed '/HTTP_STATUS:/d')

echo "📥 Response Status: $HTTP_STATUS"
echo "📥 Response Body: $RESPONSE_BODY"
echo ""

if [ "$HTTP_STATUS" = "200" ] || [ "$HTTP_STATUS" = "202" ]; then
    echo "✅ Cloud event sent successfully!"
    echo "🎉 The processing service should now be handling your request."
else
    echo "❌ Cloud event failed with status: $HTTP_STATUS"
    echo "🔍 Check if the cloud event service is running at: $CLOUD_EVENT_SERVICE_URL"
fi

echo ""
echo "💡 To test the full flow, you can also:"
echo "   1. Start your Next.js application: npm run dev"
echo "   2. Go to http://localhost:3000"
echo "   3. Enter a GitHub repository URL and click 'Generate'"
