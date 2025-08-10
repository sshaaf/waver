package dev.shaaf.waver.backend.minio;

import dev.shaaf.waver.backend.config.MinioConfig;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@Startup
public class MinioBucketInitializer {

    @Inject
    MinioClient minioClient;

    @Inject
    MinioConfig minioConfig;


    @PostConstruct
    public void initBuckets() {
        boolean found = false;
        try {
            found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioConfig.bucketName()).build());

            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioConfig.bucketName()).build());
            } else {
                Log.infof("Bucket already exists: " + minioConfig.bucketName());
            }
        } catch (Exception e) {
            Log.error("Could not initialize bucket", e);
        }
    }

}
