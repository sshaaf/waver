# Makefile for Waver Site Deployment

# Configuration
QUAY_NAMESPACE ?= sshaaf
IMAGE_NAME ?= waver-site
IMAGE_TAG ?= latest
PROJECT_NAME ?= waver-site
CLUSTER_DOMAIN ?= your-cluster-domain.com

# Quay.io repository
QUAY_REPOSITORY = quay.io/$(QUAY_NAMESPACE)/$(IMAGE_NAME)

.PHONY: help build push deploy deploy-full clean

help: ## Show this help message
	@echo "Waver Site Deployment Commands:"
	@echo ""
	@echo "Build and Push:"
	@echo "  build          Build Docker image"
	@echo "  push           Push image to Quay.io"
	@echo "  build-push     Build and push image"
	@echo ""
	@echo "Deployment:"
	@echo "  deploy         Deploy to OpenShift (skip build)"
	@echo "  deploy-full    Build, push, and deploy"
	@echo ""
	@echo "Utility:"
	@echo "  clean          Clean up local Docker images"
	@echo "  help           Show this help message"
	@echo ""
	@echo "Environment Variables:"
	@echo "  QUAY_NAMESPACE=$(QUAY_NAMESPACE)"
	@echo "  IMAGE_NAME=$(IMAGE_NAME)"
	@echo "  IMAGE_TAG=$(IMAGE_TAG)"
	@echo "  PROJECT_NAME=$(PROJECT_NAME)"
	@echo "  CLUSTER_DOMAIN=$(CLUSTER_DOMAIN)"

build: ## Build Docker image
	@echo "🔨 Building Docker image..."
	docker build -t $(QUAY_REPOSITORY):$(IMAGE_TAG) .
	@if [ "$(IMAGE_TAG)" != "latest" ]; then \
		echo "🏷️  Tagging as latest..."; \
		docker tag $(QUAY_REPOSITORY):$(IMAGE_TAG) $(QUAY_REPOSITORY):latest; \
	fi

push: ## Push image to Quay.io
	@echo "📤 Pushing to Quay.io..."
	docker push $(QUAY_REPOSITORY):$(IMAGE_TAG)
	@if [ "$(IMAGE_TAG)" != "latest" ]; then \
		docker push $(QUAY_REPOSITORY):latest; \
	fi
	@echo "✅ Successfully pushed to Quay.io!"

build-push: build push ## Build and push image

deploy: ## Deploy to OpenShift (skip build)
	@echo "☸️  Deploying to OpenShift..."
	SKIP_BUILD=true QUAY_NAMESPACE=$(QUAY_NAMESPACE) IMAGE_TAG=$(IMAGE_TAG) PROJECT_NAME=$(PROJECT_NAME) CLUSTER_DOMAIN=$(CLUSTER_DOMAIN) ./scripts/deploy-to-openshift.sh

deploy-full: build-push deploy ## Build, push, and deploy

clean: ## Clean up local Docker images
	@echo "🧹 Cleaning up Docker images..."
	docker rmi $(QUAY_REPOSITORY):$(IMAGE_TAG) 2>/dev/null || true
	@if [ "$(IMAGE_TAG)" != "latest" ]; then \
		docker rmi $(QUAY_REPOSITORY):latest 2>/dev/null || true; \
	fi
	@echo "✅ Cleanup complete!"

# Development helpers
dev: ## Start development server
	npm run dev

install: ## Install dependencies
	npm install

lint: ## Run linting
	npm run lint

test: ## Run tests (if available)
	@echo "🧪 No tests configured yet"

# Quick deployment with common settings
deploy-prod: ## Deploy production version
	@$(MAKE) deploy-full IMAGE_TAG=v1.0.0

deploy-staging: ## Deploy staging version
	@$(MAKE) deploy-full IMAGE_TAG=staging

# Testing
test-cloud-event: ## Test cloud event functionality
	@./scripts/test-cloud-event.sh
