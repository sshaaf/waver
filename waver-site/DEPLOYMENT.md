# Waver Site Deployment Guide

This guide covers deploying the Waver Site Next.js application to OpenShift using Quay.io as the container registry.

## 🏗️ Architecture Overview

```
┌─────────────────┐    ┌──────────────┐    ┌─────────────────┐
│   Local Build   │───▶│   Quay.io    │───▶│   OpenShift     │
│   (Docker)      │    │   Registry   │    │   Cluster       │
└─────────────────┘    └──────────────┘    └─────────────────┘
```

## 📋 Prerequisites

1. **Docker** - For building container images
2. **OpenShift CLI (oc)** - For deploying to OpenShift
3. **Quay.io Account** - Container registry account
4. **OpenShift Cluster Access** - Access to an OpenShift cluster

## 🚀 Quick Start

### 1. Setup Authentication

```bash
# Login to Quay.io
docker login quay.io

# Login to OpenShift
oc login --token=<your-token> --server=<your-cluster-url>
```

### 2. Configure Environment

```bash
export QUAY_NAMESPACE="sshaaf"
export IMAGE_TAG="latest"
export PROJECT_NAME="waver-site"
export CLUSTER_DOMAIN="your-cluster-domain.com"
```

### 3. Deploy Everything

```bash
# Complete deployment (build → push → deploy)
./scripts/deploy-to-openshift.sh
```

## 🛠️ Deployment Options

### Option 1: Complete Pipeline (Recommended)

```bash
./scripts/deploy-to-openshift.sh
```

This single command:
- Builds the Docker image
- Pushes it to Quay.io
- Deploys to OpenShift
- Shows the application URL

### Option 2: Using Makefile

```bash
# Show available commands
make help

# Complete deployment
make deploy-full

# Deploy only (skip build)
make deploy

# Build and push only
make build-push
```

### Option 3: Step by Step

```bash
# Step 1: Build and push
./scripts/build-and-push.sh

# Step 2: Deploy to OpenShift
cd openshift
./deploy.sh
```

## 🔧 Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `QUAY_NAMESPACE` | Your Quay.io namespace | `sshaaf` |
| `IMAGE_NAME` | Docker image name | `waver-site` |
| `IMAGE_TAG` | Docker image tag | `latest` |
| `PROJECT_NAME` | OpenShift project name | `waver-site` |
| `CLUSTER_DOMAIN` | OpenShift cluster domain | `your-cluster-domain.com` |
| `SKIP_BUILD` | Skip Docker build step | `false` |
| `CLOUD_EVENT_SERVICE_URL` | Cloud event processing service URL | `http://localhost:8080` |

### MinIO Configuration

Update the MinIO credentials in `openshift/secret.yaml`:

```bash
# Generate base64 encoded values
echo -n "your-access-key" | base64
echo -n "your-secret-key" | base64
```

Then update the secret file:

```yaml
data:
  minio-access-key: <your-base64-encoded-access-key>
  minio-secret-key: <your-base64-encoded-secret-key>
```

## 📊 Monitoring

### Check Deployment Status

```bash
# Check pods
oc get pods -l app=waver-site

# Check routes
oc get routes

# Check services
oc get services
```

### View Logs

```bash
# Application logs
oc logs -f deployment/waver-site

# Specific pod logs
oc logs -f <pod-name>
```

### Health Checks

The deployment includes:
- **Liveness Probe**: Checks if the application is running
- **Readiness Probe**: Checks if the application is ready to serve traffic

## 🔄 Updating the Application

### For Code Changes

```bash
# Build and deploy with new tag
export IMAGE_TAG="v1.1.0"
./scripts/deploy-to-openshift.sh
```

### For Configuration Changes

```bash
# Update ConfigMap or Secret
oc apply -f openshift/configmap.yaml
oc apply -f openshift/secret.yaml

# Restart deployment to pick up changes
oc rollout restart deployment/waver-site
```

## 🧹 Cleanup

### Remove Deployment

```bash
# Delete all resources
oc delete all -l app=waver-site

# Delete project
oc delete project waver-site
```

### Clean Local Images

```bash
make clean
```

## 🐛 Troubleshooting

### Build Issues

**Problem**: Docker build fails
```bash
# Check build logs
docker build -t quay.io/your-namespace/waver-site:latest . --progress=plain
```

**Problem**: Can't push to Quay.io
```bash
# Check authentication
docker login quay.io
```

### Deployment Issues

**Problem**: Pods not starting
```bash
# Check pod events
oc describe pod <pod-name>

# Check resource limits
oc get pods -o wide
```

**Problem**: Application not responding
```bash
# Check application logs
oc logs -f deployment/waver-site

# Check environment variables
oc set env deployment/waver-site --list
```

### Network Issues

**Problem**: Can't connect to MinIO
```bash
# Check MinIO service
oc get services | grep minio

# Test connectivity from pod
oc exec -it <pod-name> -- curl http://minio-service:9000
```

## 🔒 Security Considerations

1. **Image Security**: Use trusted base images and scan for vulnerabilities
2. **Secrets Management**: Store sensitive data in OpenShift secrets
3. **Network Policies**: Consider implementing network policies
4. **RBAC**: Use appropriate service accounts and role bindings
5. **TLS**: Routes are configured with edge TLS termination

## 📈 Scaling

### Horizontal Scaling

```bash
# Scale to 3 replicas
oc scale deployment waver-site --replicas=3

# Auto-scaling (if HPA is configured)
oc autoscale deployment waver-site --min=2 --max=10 --cpu-percent=80
```

### Resource Scaling

Update resource limits in `openshift/deployment.yaml`:

```yaml
resources:
  requests:
    memory: "512Mi"
    cpu: "500m"
  limits:
    memory: "1Gi"
    cpu: "1000m"
```

## 🔄 CI/CD Integration

### GitHub Actions Example

```yaml
name: Deploy to OpenShift
on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    
    - name: Login to Quay.io
      uses: docker/login-action@v1
      with:
        registry: quay.io
        username: ${{ secrets.QUAY_USERNAME }}
        password: ${{ secrets.QUAY_PASSWORD }}
    
    - name: Build and push
      run: |
        export QUAY_NAMESPACE=${{ secrets.QUAY_NAMESPACE }}
        export IMAGE_TAG=${{ github.sha }}
        ./scripts/build-and-push.sh
    
    - name: Deploy to OpenShift
      run: |
        export QUAY_NAMESPACE=${{ secrets.QUAY_NAMESPACE }}
        export IMAGE_TAG=${{ github.sha }}
        export SKIP_BUILD=true
        ./scripts/deploy-to-openshift.sh
```

## 📚 Additional Resources

- [OpenShift Documentation](https://docs.openshift.com/)
- [Quay.io Documentation](https://docs.quay.io/)
- [Next.js Deployment](https://nextjs.org/docs/deployment)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
