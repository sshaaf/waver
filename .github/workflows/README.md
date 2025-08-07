# GitHub Actions Workflows

This directory contains GitHub Actions workflows for the Waver project.

## Build and Push Workflows

### Frontend (Waver Site)

The `build-and-push.yml` workflow automatically builds and pushes Docker images to Quay.io when changes are made to the `waver-site` directory.

### Backend (Waver Backend)

The `backend-build-and-push.yml` workflow automatically builds and pushes Docker images to Quay.io when changes are made to the `waver-backend` directory.

### Triggers

- **Push** to `main` or `develop` branches
- **Pull Request** to `main` or `develop` branches
- Only triggers when files in the `waver-site/` directory are modified

### What it does

1. **Checkout** the code
2. **Setup Docker Buildx** for multi-platform builds
3. **Extract Git SHA** for tagging
4. **Login to Quay.io** using secrets
5. **Build and push** the Docker image with the Git SHA as the tag
6. **Output** image information for reference

### Image Tagging

- Images are tagged with the short Git SHA (e.g., `a1b2c3d`)
- No `latest` tag is applied (as per requirements)
- Frontend images are pushed to: `quay.io/sshaaf/waver-site:<git-sha>`
- Backend images are pushed to: `quay.io/sshaaf/waver-backend:<git-sha>`

### Required Secrets

You need to configure the following secrets in your GitHub repository:

1. **QUAY_USERNAME**: Your Quay.io username
2. **QUAY_PASSWORD**: Your Quay.io password or token

### How to set up secrets

1. Go to your GitHub repository
2. Navigate to **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Add the two secrets mentioned above

### Example output

**Frontend:**
```
✅ Successfully built and pushed image:
   Repository: quay.io/sshaaf/waver-site
   Tag: a1b2c3d
   Full SHA: a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0
   Image URL: quay.io/sshaaf/waver-site:a1b2c3d
```

**Backend:**
```
✅ Successfully built and pushed image:
   Repository: quay.io/sshaaf/waver-backend
   Tag: a1b2c3d
   Full SHA: a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0
   Image URL: quay.io/sshaaf/waver-backend:a1b2c3d
```

### Multi-platform builds

The frontend workflow builds for both `linux/amd64` and `linux/arm64` architectures to support different deployment environments.

The backend workflow uses Quarkus's native container build capabilities.

### Caching

Docker layer caching is enabled using GitHub Actions cache to speed up subsequent builds.

Maven dependencies are cached for faster backend builds.
