#!/bin/bash

# Build and Push Script for Waver Site to Quay.io
set -e

# Configuration
QUAY_NAMESPACE=${QUAY_NAMESPACE:-"sshaaf"}
IMAGE_NAME=${IMAGE_NAME:-"waver-site"}
IMAGE_TAG=${IMAGE_TAG:-"latest"}
QUAY_REPOSITORY="quay.io/${QUAY_NAMESPACE}/${IMAGE_NAME}"

echo "🐳 Building and pushing Docker image to Quay.io..."

# Check if Docker is running
if ! docker info >/dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Check if logged into Quay.io
if ! docker images | grep -q "quay.io"; then
    echo "⚠️  You may need to login to Quay.io first:"
    echo "   docker login quay.io"
fi

# Build the Docker image
echo "🔨 Building Docker image..."
docker build -t ${QUAY_REPOSITORY}:${IMAGE_TAG} .

# Tag with latest if using a specific tag
if [ "$IMAGE_TAG" != "latest" ]; then
    echo "🏷️  Tagging as latest..."
    docker tag ${QUAY_REPOSITORY}:${IMAGE_TAG} ${QUAY_REPOSITORY}:latest
fi

# Push to Quay.io
echo "📤 Pushing to Quay.io..."
docker push ${QUAY_REPOSITORY}:${IMAGE_TAG}

if [ "$IMAGE_TAG" != "latest" ]; then
    docker push ${QUAY_REPOSITORY}:latest
fi

echo "✅ Successfully pushed to Quay.io!"
echo "📦 Image: ${QUAY_REPOSITORY}:${IMAGE_TAG}"
echo ""
echo "🚀 To deploy to OpenShift, run:"
echo "   cd openshift"
echo "   QUAY_NAMESPACE=${QUAY_NAMESPACE} IMAGE_TAG=${IMAGE_TAG} ./deploy.sh"
