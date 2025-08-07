#!/bin/bash

# OpenShift Deployment Script for Waver Site
set -e

# Configuration
PROJECT_NAME=${PROJECT_NAME:-"waver-site"}
QUAY_NAMESPACE=${QUAY_NAMESPACE:-"sshaaf"}
IMAGE_TAG=${IMAGE_TAG:-"latest"}
CLUSTER_DOMAIN=${CLUSTER_DOMAIN:-"your-cluster-domain.com"}

echo "🚀 Deploying Waver Site to OpenShift..."

# Check if logged into OpenShift
if ! oc whoami >/dev/null 2>&1; then
    echo "❌ Not logged into OpenShift. Please run 'oc login' first."
    exit 1
fi

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
find . -name "*.yaml" -type f -exec sed -i "s/\${QUAY_NAMESPACE}/$QUAY_NAMESPACE/g" {} \;
find . -name "*.yaml" -type f -exec sed -i "s/\${IMAGE_TAG}/$IMAGE_TAG/g" {} \;
find . -name "*.yaml" -type f -exec sed -i "s/\${CLUSTER_DOMAIN}/$CLUSTER_DOMAIN/g" {} \;

# Apply resources using kustomize
echo "📦 Applying OpenShift resources..."
oc apply -k .

# Wait for deployment to be ready
echo "⏳ Waiting for deployment to be ready..."
oc rollout status deployment/waver-site

# Get the route URL
ROUTE_URL=$(oc get route waver-site-route -o jsonpath='{.spec.host}')
echo "✅ Deployment complete!"
echo "🌐 Your application is available at: https://$ROUTE_URL"

# Show deployment status
echo "📊 Deployment status:"
oc get pods -l app=waver-site
oc get routes
