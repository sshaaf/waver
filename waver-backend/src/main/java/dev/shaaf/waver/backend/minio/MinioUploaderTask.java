package dev.shaaf.waver.backend.minio;


import java.util.concurrent.CompletableFuture;
import dev.shaaf.waver.backend.config.MinioConfig;
import dev.shaaf.waver.core.PipelineContext;
import dev.shaaf.waver.core.Task;
import dev.shaaf.waver.core.TaskRunException;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import jakarta.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MinioUploaderTask implements Task<String, UploadResult> {
    @Inject
    MinioClient minioClient;

    @Inject
    MinioConfig minioConfig;

    private Path sourceDirectory;

    public MinioUploaderTask(Path sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    @Override
    public CompletableFuture<UploadResult> execute(String input, PipelineContext context) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return uploadDirectory(sourceDirectory.resolve(input));
            } catch (Exception e) {
                throw new TaskRunException("Failed to upload to MinIO", e);
            }
        });
    }

    public UploadResult uploadDirectory(Path sourceDirectory) {
        if (!Files.exists(sourceDirectory) || !Files.isDirectory(sourceDirectory)) {
            throw new IllegalArgumentException("Source path must be an existing directory: " + sourceDirectory);
        }

        List<String> successfulUploads = new ArrayList<>();
        List<String> failedUploads = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(sourceDirectory)) {
            stream
                    .filter(Files::isRegularFile)
                    .forEach(filePath -> {
                        try {
                            Path relativePath = sourceDirectory.relativize(filePath);
                            String objectName = relativePath.toString().replace("\\", "/");

                            minioClient.uploadObject(
                                    UploadObjectArgs.builder()
                                            .bucket(minioConfig.bucketName())
                                            .object(objectName)
                                            .filename(filePath.toString())
                                            .build());

                            successfulUploads.add(objectName);

                        } catch (Exception e) {
                            failedUploads.add(filePath.toString());
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("Failed to read the source directory: " + sourceDirectory, e);
        }

        return new UploadResult(successfulUploads, failedUploads);
    }

}


