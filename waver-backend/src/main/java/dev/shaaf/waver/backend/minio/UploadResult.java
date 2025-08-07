package dev.shaaf.waver.backend.minio;

import java.util.List;

public record UploadResult(List<String> successfulUploads, List<String> failedUploads) {

    public int getSuccessCount() {
        return successfulUploads.size();
    }

    public int getFailureCount() {
        return failedUploads.size();
    }
}