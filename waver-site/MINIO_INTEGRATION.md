# MinIO S3 Integration for Tutorial Loading

This document describes the MinIO S3 integration that has been implemented to replace the filesystem-based tutorial loading system.

## Overview

The tutorial loading system has been migrated from using local filesystem storage to using MinIO as an S3-compatible object storage backend. This provides better scalability, centralized storage, and easier deployment options.

## Configuration

### Environment Variables

The following environment variables are used to configure the MinIO connection:

```bash
MINIO_ENDPOINT=http://localhost:9000
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin
MINIO_BUCKET=waver-bucket
```

### Next.js Configuration

The environment variables are configured in `next.config.ts`:

```typescript
const nextConfig: NextConfig = {
  env: {
    MINIO_ENDPOINT: process.env.MINIO_ENDPOINT || 'http://localhost:9000',
    MINIO_ACCESS_KEY: process.env.MINIO_ACCESS_KEY || 'minioadmin',
    MINIO_SECRET_KEY: process.env.MINIO_SECRET_KEY || 'minioadmin',
    MINIO_BUCKET: process.env.MINIO_BUCKET || 'waver-bucket',
  },
};
```

## Implementation Details

### MinIO Client (`src/lib/minio-client.ts`)

The MinIO client provides the following functions:

- `getObject(key: string)`: Retrieves an object from MinIO
- `listObjects(prefix: string)`: Lists objects with a given prefix
- `objectExists(key: string)`: Checks if an object exists
- `getTutorialDirectories()`: Gets all tutorial directories
- `getTutorialFiles(tutorialId: string)`: Gets all files in a tutorial directory

### Updated Tutorial Loader (`src/lib/tutorial-loader.ts`)

The tutorial loader has been updated to use MinIO instead of the filesystem:

- `loadTutorialContent()`: Loads tutorial content from MinIO
- `loadTutorialChapter()`: Loads specific chapters from MinIO
- `getAllAvailableTutorials()`: Lists all available tutorials from MinIO
- `tutorialExists()`: Checks if a tutorial exists in MinIO
- `getTutorialChapters()`: Gets tutorial chapters from MinIO

## Data Structure

Tutorials are stored in MinIO with the following structure:

```
waver-bucket/
├── tutorial-id-1/
│   ├── waver-config.json
│   ├── index.md
│   ├── chapter1.md
│   ├── chapter2.md
│   └── ...
├── tutorial-id-2/
│   ├── waver-config.json
│   ├── index.md
│   └── ...
└── ...
```

## Dependencies

The following AWS SDK packages are used for S3 compatibility:

```json
{
  "@aws-sdk/client-s3": "^3.x.x",
  "@aws-sdk/s3-request-presigner": "^3.x.x"
}
```

## Testing

To test the MinIO integration:

1. Ensure MinIO is running with the configured endpoint and credentials
2. Verify that the `waver-bucket` exists and contains tutorial data
3. Start the Next.js development server: `npm run dev`
4. Navigate to a tutorial page to verify content loads from MinIO

## Migration from Filesystem

The migration involved:

1. Replacing `fs` and `path` imports with MinIO client functions
2. Converting synchronous filesystem operations to asynchronous MinIO operations
3. Updating function signatures to handle async operations
4. Maintaining the same API interface for existing components

## Benefits

- **Scalability**: Can handle large numbers of tutorials without filesystem limitations
- **Centralized Storage**: All tutorial data is stored in one location
- **Deployment Flexibility**: Can deploy to different environments without file system dependencies
- **S3 Compatibility**: Can easily migrate to AWS S3 or other S3-compatible services
- **Backup and Replication**: MinIO provides built-in backup and replication capabilities

## Troubleshooting

### Common Issues

1. **Connection Errors**: Verify MinIO is running and accessible at the configured endpoint
2. **Authentication Errors**: Check that the access key and secret key are correct
3. **Bucket Not Found**: Ensure the configured bucket exists in MinIO
4. **Permission Errors**: Verify the MinIO user has read permissions for the bucket

### Debugging

Enable debug logging by checking the console output for MinIO-related error messages. The client includes comprehensive error handling and logging.
