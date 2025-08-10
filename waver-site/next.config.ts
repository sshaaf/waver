import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  output: 'standalone',
  env: {
    MINIO_ENDPOINT: process.env.MINIO_ENDPOINT || 'http://localhost:9000',
    MINIO_ACCESS_KEY: process.env.MINIO_ACCESS_KEY || 'minioadmin',
    MINIO_SECRET_KEY: process.env.MINIO_SECRET_KEY || 'minioadmin',
    MINIO_BUCKET: process.env.MINIO_BUCKET || 'waver-bucket',
    CLOUD_EVENT_SERVICE_URL: process.env.CLOUD_EVENT_SERVICE_URL || 'http://localhost:8080',
  },
};

export default nextConfig;
