package dev.shaaf.waver.util;

import dev.shaaf.waver.log.LogConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GitHubRepoFetcher {
    
    private static final Logger logger = Logger.getLogger(GitHubRepoFetcher.class.getName());
    private static final int CLONE_TIMEOUT_MINUTES = 10;
    
    static {
        // Initialize logger with custom configuration
        LogConfig.setLoggingConfig(logger);
    }
    
    public static Path fetchRepository(String githubUrl, Path baseOutputDir) throws IOException {
        GitHubUrlParser parser = new GitHubUrlParser(githubUrl);
        String cloneUrl = parser.getCloneUrl();
        String repoName = parser.getRepositoryName();
        
        // Ensure the base output directory exists
        if (!Files.exists(baseOutputDir)) {
            Files.createDirectories(baseOutputDir);
        }
        
        Path tempDir = Files.createTempDirectory(baseOutputDir, "waver-github-");
        Path repoDir = tempDir.resolve(repoName);
        
        logger.info(String.format("Base output dir: %s (exists: %s)", baseOutputDir, Files.exists(baseOutputDir)));
        logger.info(String.format("Temp dir created: %s", tempDir));
        logger.info(String.format("Cloning repository from %s to %s", cloneUrl, repoDir));
        
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                "git", "clone", "--depth", "1", cloneUrl, repoDir.toString()
            );
            processBuilder.redirectErrorStream(true);
            
            Process process = processBuilder.start();
            
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                    logger.info("Git: " + line);
                }
            }
            
            boolean finished = process.waitFor(CLONE_TIMEOUT_MINUTES, TimeUnit.MINUTES);
            if (!finished) {
                process.destroyForcibly();
                throw new IOException("Git clone operation timed out after " + CLONE_TIMEOUT_MINUTES + " minutes");
            }
            
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                throw new IOException("Git clone failed with exit code: " + exitCode + 
                    ". Output: " + output.toString());
            }
            
            if (!Files.exists(repoDir) || !Files.isDirectory(repoDir)) {
                throw new IOException("Repository directory was not created successfully");
            }
            
            logger.info("Repository cloned successfully to: " + repoDir);
            return repoDir;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            deleteQuietly(tempDir);
            throw new IOException("Git clone operation was interrupted", e);
        } catch (IOException e) {
            deleteQuietly(tempDir);
            throw e;
        } catch (Exception e) {
            deleteQuietly(tempDir);
            throw new IOException("Unexpected error during git clone: " + e.getMessage(), e);
        }
    }
    
    private static void deleteQuietly(Path path) {
        try {
            if (Files.exists(path)) {
                Files.walk(path)
                    .sorted((p1, p2) -> -p1.compareTo(p2))
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            logger.log(Level.WARNING, "Failed to delete: " + p, e);
                        }
                    });
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to delete directory: " + path, e);
        }
    }
    
    public static boolean isGitAvailable() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("git", "--version");
            Process process = processBuilder.start();
            process.waitFor(5, TimeUnit.SECONDS);
            return process.exitValue() == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }
}