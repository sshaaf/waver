import { S3Client, GetObjectCommand, ListObjectsV2Command, HeadObjectCommand } from '@aws-sdk/client-s3';

// Check if we're on the server side
const isServer = typeof window === 'undefined';

// MinIO configuration from environment variables
const MINIO_ENDPOINT = process.env.MINIO_ENDPOINT || 'http://localhost:9000';
const MINIO_ACCESS_KEY = process.env.MINIO_ACCESS_KEY || 'minioadmin';
const MINIO_SECRET_KEY = process.env.MINIO_SECRET_KEY || 'minioadmin';
const MINIO_BUCKET = process.env.MINIO_BUCKET || 'waver-bucket';

// Create S3 client configured for MinIO (only on server side)
let s3Client: S3Client | null = null;

if (isServer) {
  s3Client = new S3Client({
    endpoint: MINIO_ENDPOINT,
    region: 'us-east-1', // MinIO doesn't require a specific region
    credentials: {
      accessKeyId: MINIO_ACCESS_KEY,
      secretAccessKey: MINIO_SECRET_KEY,
    },
    forcePathStyle: true, // Required for MinIO
  });
}

/**
 * Get an object from MinIO S3
 */
export async function getObject(key: string): Promise<string | null> {
  if (!isServer || !s3Client) {
    console.warn('MinIO client is only available on the server side');
    return null;
  }

  try {
    const command = new GetObjectCommand({
      Bucket: MINIO_BUCKET,
      Key: key,
    });

    const response = await s3Client.send(command);
    
    if (!response.Body) {
      return null;
    }

    // Convert stream to string
    const chunks: Uint8Array[] = [];
    const reader = response.Body.transformToWebStream().getReader();
    
    while (true) {
      const { done, value } = await reader.read();
      if (done) break;
      chunks.push(value);
    }
    
    const buffer = Buffer.concat(chunks);
    return buffer.toString('utf-8');
  } catch (error) {
    console.error(`Error getting object ${key} from MinIO:`, error);
    return null;
  }
}

/**
 * List objects in a prefix (directory-like structure)
 */
export async function listObjects(prefix: string = ''): Promise<string[]> {
  if (!isServer || !s3Client) {
    console.warn('MinIO client is only available on the server side');
    return [];
  }

  try {
    const command = new ListObjectsV2Command({
      Bucket: MINIO_BUCKET,
      Prefix: prefix,
      Delimiter: '/',
    });

    const response = await s3Client.send(command);
    
    const objects: string[] = [];
    
    // Add files
    if (response.Contents) {
      objects.push(...response.Contents.map(obj => obj.Key!).filter(key => key !== prefix));
    }
    
    // Add directories
    if (response.CommonPrefixes) {
      objects.push(...response.CommonPrefixes.map(prefix => prefix.Prefix!));
    }
    
    return objects;
  } catch (error) {
    console.error(`Error listing objects with prefix ${prefix} from MinIO:`, error);
    return [];
  }
}

/**
 * Check if an object exists
 */
export async function objectExists(key: string): Promise<boolean> {
  if (!isServer || !s3Client) {
    console.warn('MinIO client is only available on the server side');
    return false;
  }

  try {
    const command = new HeadObjectCommand({
      Bucket: MINIO_BUCKET,
      Key: key,
    });

    await s3Client.send(command);
    return true;
  } catch (error) {
    return false;
  }
}

/**
 * Get all tutorial directories (top-level folders)
 */
export async function getTutorialDirectories(): Promise<string[]> {
  if (!isServer || !s3Client) {
    console.warn('MinIO client is only available on the server side');
    return [];
  }

  try {
    const objects = await listObjects();
    
    // Filter for directories (objects ending with /)
    const directories = objects
      .filter(obj => obj.endsWith('/'))
      .map(dir => dir.replace(/\/$/, '')); // Remove trailing slash
    
    return directories;
  } catch (error) {
    console.error('Error getting tutorial directories from MinIO:', error);
    return [];
  }
}

/**
 * Get all files in a tutorial directory
 */
export async function getTutorialFiles(tutorialId: string): Promise<string[]> {
  if (!isServer || !s3Client) {
    console.warn('MinIO client is only available on the server side');
    return [];
  }

  try {
    const prefix = `${tutorialId}/`;
    const objects = await listObjects(prefix);
    
    // Filter for files (not ending with /) and remove the prefix
    return objects
      .filter(obj => !obj.endsWith('/'))
      .map(file => file.replace(prefix, ''));
  } catch (error) {
    console.error(`Error getting files for tutorial ${tutorialId} from MinIO:`, error);
    return [];
  }
}
