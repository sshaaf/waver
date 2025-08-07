#!/bin/bash

# Complete Deployment Script: Build → Push to Quay → Deploy to OpenShift
set -e

# Configuration
QUAY_NAMESPACE=${QUAY_NAMESPACE:-"sshaaf"}
IMAGE_NAME=${IMAGE_NAME:-"waver-site"}
IMAGE_TAG=${IMAGE_TAG:-"latest"}
PROJECT_NAME=${PROJECT_NAME:-"waver-site"}
CLUSTER_DOMAIN=${CLUSTER_DOMAIN:-"your-cluster-domain.com"}
SKIP_BUILD=${SKIP_BUILD:-"false"}

echo "🚀 Complete OpenShift Deployment Pipeline"
echo "========================================"

# Check prerequisites
echo "🔍 Checking prerequisites..."

# Check if Docker is running
if ! docker info >/dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Check if logged into OpenShift
if ! oc whoami >/dev/null 2>&1; then
    echo "❌ Not logged into OpenShift. Please run 'oc login' first."
    exit 1
fi

# Check if logged into Quay.io
if ! docker images | grep -q "quay.io"; then
    echo "⚠️  You may need to login to Quay.io first:"
    echo "   docker login quay.io"
    read -p "Continue anyway? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# Build and push to Quay.io (unless skipped)
if [ "$SKIP_BUILD" != "true" ]; then
    echo ""
    echo "🐳 Step 1: Building and pushing to Quay.io"
    echo "=========================================="
    
    QUAY_REPOSITORY="quay.io/${QUAY_NAMESPACE}/${IMAGE_NAME}"
    
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
else
    echo ""
    echo "⏭️  Skipping build step (SKIP_BUILD=true)"
fi

# Deploy to OpenShift
echo ""
echo "☸️  Step 2: Deploying to OpenShift"
echo "=================================="

# Create project if it doesn't exist
if ! oc get project $PROJECT_NAME >/dev/null 2>&1; then
    echo "📁 Creating project: $PROJECT_NAME"
    oc new-project $PROJECT_NAME
else
    echo "📁 Using existing project: $PROJECT_NAME"
    oc project $PROJECT_NAME
fi

# Process templates and deploy
echo "🔧 Processing OpenShift templates..."

# Replace placeholders in templates
find openshift -name "*.yaml" -type f -exec sed -i "s/\${QUAY_NAMESPACE}/$QUAY_NAMESPACE/g" {} \;
find openshift -name "*.yaml" -type f -exec sed -i "s/\${IMAGE_TAG}/$IMAGE_TAG/g" {} \;
find openshift -name "*.yaml" -type f -exec sed -i "s/\${CLUSTER_DOMAIN}/$CLUSTER_DOMAIN/g" {} \;

# Apply resources using kustomize
echo "📦 Applying OpenShift resources..."
oc apply -k openshift/

# Wait for deployment to be ready
echo "⏳ Waiting for deployment to be ready..."
oc rollout status deployment/waver-site

# Get the route URL
ROUTE_URL=$(oc get route waver-site-route -o jsonpath='{.spec.host}')
echo ""
echo "✅ Deployment complete!"
echo "🌐 Your application is available at: https://$ROUTE_URL"

# Show deployment status
echo ""
echo "📊 Deployment status:"
oc get pods -l app=waver-site
oc get routes

echo ""
echo "🎉 Deployment pipeline completed successfully!"
echo ""
echo "📋 Summary:"
echo "   - Image: quay.io/${QUAY_NAMESPACE}/${IMAGE_NAME}:${IMAGE_TAG}"
echo "   - Project: ${PROJECT_NAME}"
echo "   - URL: https://${ROUTE_URL}"
