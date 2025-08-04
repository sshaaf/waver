package dev.shaaf.waver.util.files;

import dev.shaaf.waver.config.AppConfig;
import dev.shaaf.waver.core.TaskRunException;
import dev.shaaf.waver.util.log.LogConfig;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitHubRepoFetcher {

    private static final Logger logger = Logger.getLogger(GitHubRepoFetcher.class.getName());

    static {
        // Initialize logger with custom configuration
        LogConfig.setLoggingConfig(logger);
    }

    public static String getAndCloneRepo(String gitRemoteUrl) throws IOException {

        Path tempDir = Path.of(AppConfig.DEFAULT_GITHUB_CLONE_PATH + getNameFromRemoteURL(gitRemoteUrl));

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

        return tempDir.toAbsolutePath().toString();
    }


    public static String getNameFromRemoteURL(String urlString) {
        Pattern REPO_PATTERN = Pattern.compile("^https?://[^/]+/(.*?)\\.git$");
        Matcher matcher = REPO_PATTERN.matcher(urlString);

        if (matcher.find()) {
            // Group 1 contains our desired string
            return matcher.group(1);
        }

        throw new TaskRunException("Unable to parse URL to create local copy: " + urlString);

    }
}