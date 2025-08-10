# Waver Site

Modern web interface built with Next.js that provides a user-friendly way to generate and view AI-generated code tutorials.

## Overview

`waver-site` is a Next.js web application that:
- **Provides Web Interface**: User-friendly interface for tutorial generation requests
- **Sends Cloud Events**: Dispatches tutorial generation requests to waver-backend
- **Displays Tutorials**: Renders generated tutorials with interactive features
- **Integrates with MinIO**: Loads tutorials from S3-compatible storage
- **Supports Multiple Formats**: Displays tutorials in various output formats

## Architecture

### Core Components

#### Frontend Interface
- **`HomePageClient`**: Main page with tutorial generation form
- **`TutorialDisplay`**: Renders generated tutorials
- **`TutorialCard`**: Displays tutorial metadata and previews
- **`MermaidRenderer`**: Renders Mermaid diagrams in tutorials

#### Backend Integration
- **`tutorial-generator`**: Sends cloud events to waver-backend
- **`minio-client`**: Integrates with MinIO/S3 storage
- **`github-client`**: GitHub API integration for repository information

#### Data Management
- **`tutorial-loader`**: Loads tutorials from storage
- **`markdown-processor`**: Processes markdown content
- **`curated-tutorials`**: Pre-curated tutorial content

### Data Flow
```
User Input → Web Form → Cloud Event → waver-backend → MinIO Storage
    ↓           ↓          ↓            ↓              ↓
  Git URL   Validation   Dispatch   Processing    Store Results
    ↓           ↓          ↓            ↓              ↓
  Tutorial   Display     Load from   Render with   Interactive
  Request    Results     Storage     Components    Features
```

## Features

- **Modern UI**: Built with Next.js 15 and React 19
- **Responsive Design**: Mobile-first approach with Tailwind CSS
- **Interactive Tutorials**: Mermaid diagrams, syntax highlighting, and navigation
- **Cloud Event Integration**: Seamless integration with waver-backend
- **Storage Integration**: Direct access to MinIO/S3 storage
- **GitHub Integration**: Repository information and metadata display

## Prerequisites

- Node.js 18 or higher
- npm, yarn, pnpm, or bun package manager
- Access to waver-backend service
- MinIO or S3-compatible storage access

## Installation

### Clone and Install Dependencies
```bash
# Navigate to waver-site directory
cd waver-site

# Install dependencies
npm install
# or
yarn install
# or
pnpm install
# or
bun install
```

### Environment Configuration
Create a `.env.local` file with the following variables:

```bash
# Backend Service Configuration
NEXT_PUBLIC_BACKEND_URL=http://localhost:8080
NEXT_PUBLIC_CLOUD_EVENT_ENDPOINT=http://localhost:8080

# MinIO/S3 Configuration
NEXT_PUBLIC_MINIO_ENDPOINT=http://localhost:9000
NEXT_PUBLIC_MINIO_BUCKET=waver-bucket
NEXT_PUBLIC_MINIO_REGION=us-east-1

# GitHub Integration (Optional)
GITHUB_TOKEN=your-github-token
```

## Development

### Running in Development Mode
```bash
# Start development server
npm run dev
# or
yarn dev
# or
pnpm dev
# or
bun dev
```

Open [http://localhost:3000](http://localhost:3000) with your browser to see the result.

### Development Features
- **Hot Reloading**: Automatic page updates as you edit
- **TypeScript Support**: Full type safety and IntelliSense
- **ESLint Integration**: Code quality and consistency
- **Tailwind CSS**: Utility-first CSS framework

## Building

### Production Build
```bash
# Build for production
npm run build
# or
yarn build
# or
pnpm build
# or
bun build
```

### Build Output
- **Static Files**: `out/` directory with static assets
- **Optimized Bundles**: Minified and optimized JavaScript/CSS
- **Server Components**: Server-side rendered components

## Deployment

### Local Production Server
```bash
# Start production server
npm start
# or
yarn start
# or
pnpm start
# or
bun start
```

### Docker Deployment
```bash
# Build Docker image
docker build -t waver-site:latest .

# Run container
docker run -p 3000:3000 \
  -e NEXT_PUBLIC_BACKEND_URL="http://backend:8080" \
  waver-site:latest
```

### OpenShift/Kubernetes Deployment
```bash
# Apply deployment manifests
kubectl apply -f openshift/

# Or use the deployment script
./openshift/deploy.sh
```

## Project Structure

```
waver-site/
├── src/
│   ├── app/                    # Next.js App Router
│   │   ├── api/               # API routes
│   │   ├── tutorial/          # Tutorial display pages
│   │   ├── globals.css        # Global styles
│   │   ├── layout.tsx         # Root layout
│   │   └── page.tsx           # Home page
│   ├── components/            # React components
│   │   ├── ui/               # Reusable UI components
│   │   └── tutorial/         # Tutorial-specific components
│   ├── lib/                   # Utility libraries
│   │   ├── github/           # GitHub API client
│   │   ├── minio-client.ts   # MinIO integration
│   │   └── tutorial-generator.ts
│   ├── types/                 # TypeScript type definitions
│   └── data/                  # Static data and configurations
├── public/                    # Static assets
├── openshift/                 # Deployment configurations
└── package.json               # Dependencies and scripts
```

## Key Components

### Tutorial Generation
- **`tutorial-generator.ts`**: Handles cloud event dispatch to backend
- **`minio-client.ts`**: Manages MinIO/S3 storage operations
- **`github-client.ts`**: Fetches repository information from GitHub

### Tutorial Display
- **`TutorialDisplay.tsx`**: Main tutorial rendering component
- **`MermaidRenderer.tsx`**: Renders Mermaid diagrams
- **`TutorialCard.tsx`**: Displays tutorial metadata and previews

### UI Components
- **`ui/`**: Reusable components (buttons, cards, inputs)
- **`HomePageClient.tsx`**: Main page with tutorial generation form
- **`layout.tsx`**: Root layout with navigation and styling

## API Integration

### Cloud Events
The site sends cloud events to waver-backend for tutorial generation:

```typescript
// Example cloud event structure
{
  type: "dev.shaaf.waver.tutorial.request",
  source: "waver-site",
  data: {
    gitUrl: "https://github.com/user/repo.git",
    projectName: "My Project",
    llmProvider: "Gemini",
    outputFormat: "MARKDOWN"
  }
}
```

### MinIO Integration
Direct integration with MinIO/S3 for loading tutorials:

```typescript
// Load tutorial from storage
const tutorial = await loadTutorialFromStorage(tutorialId);
const metadata = await loadTutorialMetadata(tutorialId);
```

## Styling and UI

### Design System
- **Tailwind CSS**: Utility-first CSS framework
- **Radix UI**: Accessible component primitives
- **Lucide Icons**: Consistent iconography
- **Responsive Design**: Mobile-first approach

### Theme and Colors
- **Light/Dark Mode**: Automatic theme detection
- **Consistent Palette**: Brand colors and semantic colors
- **Typography**: Optimized font loading with Next.js

## Testing

### Running Tests
```bash
# Run linting
npm run lint
# or
yarn lint

# Run type checking
npm run type-check
# or
yarn type-check
```

### Testing Strategy
- **ESLint**: Code quality and consistency
- **TypeScript**: Compile-time type checking
- **Component Testing**: React component testing (when implemented)

## Performance

### Optimization Features
- **Next.js 15**: Latest performance optimizations
- **Turbopack**: Fast development bundler
- **Image Optimization**: Automatic image optimization
- **Code Splitting**: Automatic code splitting and lazy loading

### Monitoring
- **Core Web Vitals**: Performance metrics tracking
- **Bundle Analysis**: Bundle size monitoring
- **Runtime Performance**: Client-side performance monitoring

## Troubleshooting

### Common Issues

#### Backend Connection Failures
- Verify `NEXT_PUBLIC_BACKEND_URL` is correct
- Check backend service is running
- Ensure network connectivity

#### MinIO Storage Issues
- Verify MinIO endpoint and bucket configuration
- Check storage credentials and permissions
- Ensure bucket exists and is accessible

#### Build Failures
- Clear `.next` directory: `rm -rf .next`
- Clear node modules: `rm -rf node_modules && npm install`
- Check Node.js version compatibility

## Related Components

- **[waver-core](../waver-core/README.md)**: Core logic used by the backend
- **[waver-backend](../waver-backend/README.md)**: Backend service that processes requests
- **[waver-cli](../waver-cli/README.md)**: Alternative CLI interface

## Contributing

When contributing to waver-site:

1. **Follow Next.js best practices**
2. **Use TypeScript for all new code**
3. **Follow the existing component patterns**
4. **Add proper error handling**
5. **Ensure responsive design**
6. **Update this README for new features**

## Learn More

To learn more about the technologies used:

- [Next.js Documentation](https://nextjs.org/docs) - Next.js features and API
- [React Documentation](https://react.dev/) - React features and best practices
- [Tailwind CSS](https://tailwindcss.com/docs) - Utility-first CSS framework
- [TypeScript](https://www.typescriptlang.org/docs/) - TypeScript language reference

## License

This component is part of the Waver project and is distributed under the MIT License.
