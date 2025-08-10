package dev.shaaf.waver.backend.config;

import io.smallrye.config.ConfigMapping;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigMapping(prefix = "minio")
public interface MinioConfig{
    String endpoint();
    String accessKey();
    String secretKey();
    String bucketName();

}
