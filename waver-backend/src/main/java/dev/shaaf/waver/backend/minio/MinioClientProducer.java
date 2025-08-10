package dev.shaaf.waver.backend.minio;

import dev.shaaf.waver.backend.config.MinioConfig;
import io.minio.MinioClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class MinioClientProducer {

    @Inject
    MinioConfig minioConfig;

    @Produces
    @ApplicationScoped
    public MinioClient getMinioClient() {
        return MinioClient.builder()
                .endpoint(minioConfig.endpoint())
                .credentials(minioConfig.accessKey(), minioConfig.secretKey())
                .build();
    }

}
