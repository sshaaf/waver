package dev.shaaf.waver.files;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GitHubRepoFetcherTest {

    // The hardcoded path that the class under test will create.
    private static final Path TEMP_CLONE_DIR = Path.of(System.getProperty("user.dir"), "tmp");

    // A small, public repository that is unlikely to be removed.
    private static final String VALID_PUBLIC_REPO_URL = "https://github.com/junit-team/junit4.git";
    private static final String NON_EXISTENT_REPO_URL = "https://github.com/no-such-user/no-such-repo-ever.git";

    /**
     * This cleanup logic runs before and after each test to ensure
     * the testing environment is clean.
     */
    @BeforeEach
    @AfterEach
    void cleanup() throws IOException {
        if (Files.exists(TEMP_CLONE_DIR)) {
            // Recursively delete the directory and its contents
            try (Stream<Path> walk = Files.walk(TEMP_CLONE_DIR)) {
                walk.sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                // Suppress errors during cleanup, but log them if necessary
                                System.err.println("Failed to delete path during cleanup: " + path);
                            }
                        });
            }
        }
    }


    @Test
    @DisplayName("Should throw RuntimeException for a non-existent repository")
    void getRepositoryName_withNonExistentRepository_throwsRuntimeException() {
        // Act & Assert
        // JGit throws a TransportException (subclass of GitAPIException) for a non-existent repo,
        // which the original code wraps in a RuntimeException.
        assertThrows(RuntimeException.class, () -> {
            GitHubRepoFetcher.getAndCloneRepo(NON_EXISTENT_REPO_URL);
        }, "Cloning a non-existent repository should throw a RuntimeException.");
    }

    @Test
    @DisplayName("Should throw an exception when the URL is null")
    void getAndCloneRepo_withNullUrl_throwsException() {
        // Act & Assert
        // The underlying JGit library will throw an exception if the URI is null.
        assertThrows(RuntimeException.class, () -> {
            GitHubRepoFetcher.getAndCloneRepo(null);
        }, "A null URL should cause an exception.");
    }

    @Test
    @DisplayName("Should successfully check a valid public repository and return the user/repo name without git")
    void getNameFromRemoteURL() {
        assertEquals("sshaaf/keycloak-mcp-server", GitHubRepoFetcher.getNameFromRemoteURL("https://github.com/sshaaf/keycloak-mcp-server.git"));

    }
}