import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  output: 'standalone',
  env: {
    MINIO_ENDPOINT: process.env.MINIO_ENDPOINT || 'http://localhost:9000',
    MINIO_ACCESS_KEY: process.env.MINIO_ACCESS_KEY || 'minioadmin',
    MINIO_SECRET_KEY: process.env.MINIO_SECRET_KEY || 'minioadmin',
    MINIO_BUCKET: process.env.MINIO_BUCKET || 'waver-bucket',
  },
};

export default nextConfig;
