package dev.shaaf.waver.files;

import dev.shaaf.waver.config.AppConfig;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GitHubRepoFetcher {

    private static final Logger logger = Logger.getLogger(GitHubRepoFetcher.class.getName());

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

        throw new RuntimeException("Unable to parse URL to create local copy: " + urlString);
    }


    public Set<String> getContributors(Path repoPath) throws IOException, GitAPIException {
        Set<PersonIdent> contributors = new HashSet<>();

        // Use try-with-resources to ensure the Git object is closed
        try (Git git = Git.open(repoPath.toFile())) {
            // Fetch all commits from the log
            Iterable<RevCommit> commits = git.log().all().call();

            // Iterate through commits and add unique authors
            for (RevCommit commit : commits) {
                contributors.add(commit.getAuthorIdent());
            }
        }

        // Format the output for readability
        return contributors.stream()
                .map(person -> String.format("%s <%s>", person.getName(), person.getEmailAddress()))
                .collect(Collectors.toSet());
    }


    public static Optional<String> getOwnerFromUrl(String repoUrl) {
        Pattern OWNER_REPO_PATTERN = Pattern.compile("[:/]([^/]+/[^/]+?)(\\.git)?$");
        Matcher matcher = OWNER_REPO_PATTERN.matcher(repoUrl);

        if (matcher.find()) {
            String ownerAndRepo = matcher.group(1); // e.g., "sshaaf/keycloak-mcp-server"
            String[] parts = ownerAndRepo.split("/");
            if (parts.length == 2) {
                return Optional.of(parts[0]); // The owner is the first part
            }
        }
        return Optional.empty();
    }

}