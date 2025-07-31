package dev.shaaf.waver.files;

import dev.shaaf.waver.log.LogConfig;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

public class GitHubRepoFetcher {

    private static final Logger logger = Logger.getLogger(GitHubRepoFetcher.class.getName());

    static {
        // Initialize logger with custom configuration
        LogConfig.setLoggingConfig(logger);
    }

    public static String getRepositoryName(String gitRemoteUrl) throws IOException {

        Path tempDir = Path.of(System.getProperty("user.dir")+"/tmp");

        Files.createDirectories(tempDir);
        // The try-with-resources block ensures the Git object is properly closed.
        try (Git git = Git.cloneRepository()
                .setURI(gitRemoteUrl)
                .setDirectory(tempDir.toFile())
                .setCloneAllBranches(false) // You can configure the clone
                .call()) {

            System.out.println("Repository cloned successfully!");
            System.out.println("Current branch: " + git.getRepository().getBranch());

        } catch (JGitInternalException e) {
            System.err.println("Error while cloning repository: " + e.getMessage());
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }

        return tempDir.getFileName().toString();
    }
}