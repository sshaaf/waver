# OpenShift Deployment Guide

This guide explains how to deploy the Waver Site Next.js application to OpenShift using Quay.io container registry.

## Prerequisites

1. **OpenShift CLI (oc)** - Install the OpenShift CLI tool
2. **OpenShift Cluster Access** - You need access to an OpenShift cluster
3. **Docker** - For building and pushing images to Quay.io
4. **Quay.io Account** - Container registry account for storing images

## Quick Start

### 1. Login to OpenShift and Quay.io

```bash
# Login to OpenShift
oc login --token=<your-token> --server=<your-cluster-url>

# Login to Quay.io
docker login quay.io
```

### 2. Configure Environment Variables

Set the following environment variables before deployment:

```bash
export QUAY_NAMESPACE="sshaaf"
export IMAGE_TAG="latest"
export PROJECT_NAME="waver-site"
export CLUSTER_DOMAIN="your-cluster-domain.com"
```

### 3. Run Complete Deployment

```bash
# From the project root
./scripts/deploy-to-openshift.sh
```

Or run steps separately:

```bash
# Step 1: Build and push to Quay.io
./scripts/build-and-push.sh

# Step 2: Deploy to OpenShift
cd openshift
./deploy.sh
```

## Manual Deployment Steps

If you prefer to deploy manually, follow these steps:

### 1. Build and Push to Quay.io

```bash
# Build the Docker image
docker build -t quay.io/your-namespace/waver-site:latest .

# Push to Quay.io
docker push quay.io/your-namespace/waver-site:latest
```

### 2. Create Project

```bash
oc new-project waver-site
```

### 3. Create ConfigMap and Secret

```bash
oc apply -f configmap.yaml
oc apply -f secret.yaml
```

**Important**: Update the secret values in `secret.yaml` with your actual MinIO credentials:

```bash
# Generate base64 encoded values
echo -n "your-access-key" | base64
echo -n "your-secret-key" | base64
```

### 4. Deploy Application

```bash
# Update the image reference in deployment.yaml first
oc apply -f deployment.yaml
oc apply -f service.yaml
oc apply -f route.yaml
```

### 5. Wait for Deployment

```bash
oc rollout status deployment/waver-site
```

## Configuration

### Environment Variables

The application uses the following environment variables:

- `MINIO_ENDPOINT` - MinIO server endpoint
- `MINIO_ACCESS_KEY` - MinIO access key
- `MINIO_SECRET_KEY` - MinIO secret key
- `MINIO_BUCKET` - MinIO bucket name
- `CLOUD_EVENT_SERVICE_URL` - Cloud event processing service URL

### Resource Limits

The deployment is configured with the following resource limits:

- **CPU**: 250m request, 500m limit
- **Memory**: 256Mi request, 512Mi limit

### Scaling

To scale the application:

```bash
oc scale deployment waver-site --replicas=3
```

## Monitoring

### Check Pod Status

```bash
oc get pods -l app=waver-site
```

### View Logs

```bash
oc logs -f deployment/waver-site
```

### Check Routes

```bash
oc get routes
```

## Troubleshooting

### Build Issues

If the Docker build fails:

1. Check Docker build logs:
   ```bash
   docker build -t quay.io/your-namespace/waver-site:latest . --progress=plain
   ```

2. Verify Dockerfile syntax
3. Check if all dependencies are available

### Deployment Issues

If pods are not starting:

1. Check pod events:
   ```bash
   oc describe pod <pod-name>
   ```

2. Verify secrets and configmaps:
   ```bash
   oc get secrets
   oc get configmaps
   ```

3. Check resource limits and requests

### Application Issues

If the application is not responding:

1. Check application logs:
   ```bash
   oc logs -f deployment/waver-site
   ```

2. Verify environment variables:
   ```bash
   oc set env deployment/waver-site --list
   ```

3. Test connectivity to MinIO service

## Cleanup

To remove the deployment:

```bash
oc delete all -l app=waver-site
oc delete project waver-site
```

## Security Considerations

1. **Secrets Management**: Store sensitive data in OpenShift secrets, not in ConfigMaps
2. **Network Policies**: Consider implementing network policies to restrict pod-to-pod communication
3. **RBAC**: Use appropriate service accounts and role bindings
4. **Image Security**: Use trusted base images and scan for vulnerabilities

## Customization

### Custom Domain

To use a custom domain, update the route:

```bash
oc patch route waver-site-route -p '{"spec":{"host":"your-custom-domain.com"}}'
```

### SSL/TLS

The route is configured with edge TLS termination. For custom certificates:

```bash
oc create secret tls custom-tls --cert=cert.pem --key=key.pem
oc patch route waver-site-route -p '{"spec":{"tls":{"termination":"reencrypt","key":"custom-tls"}}}'
```

### Health Checks

The deployment includes liveness and readiness probes. Adjust the timing if needed:

```bash
oc patch deployment waver-site -p '{"spec":{"template":{"spec":{"containers":[{"name":"waver-site","livenessProbe":{"initialDelaySeconds":60}}]}}}}'
```
