package dev.shaaf.waver.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitHubUrlParser {
    
    private static final Pattern GITHUB_URL_PATTERN = Pattern.compile(
        "^(?:https?://)?(?:www\\.)?github\\.com/([^/]+)/([^/]+?)(?:\\.git)?(?:/.*)?$"
    );
    
    private final String owner;
    private final String repository;
    
    public GitHubUrlParser(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("GitHub URL cannot be null or empty");
        }
        
        Matcher matcher = GITHUB_URL_PATTERN.matcher(url.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid GitHub URL: " + url);
        }
        
        this.owner = matcher.group(1);
        this.repository = matcher.group(2);
    }
    
    public String getOwner() {
        return owner;
    }
    
    public String getRepository() {
        return repository;
    }
    
    public String getCloneUrl() {
        return String.format("https://github.com/%s/%s.git", owner, repository);
    }
    
    public String getRepositoryName() {
        return repository;
    }
    
    public static boolean isValidGitHubUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        return GITHUB_URL_PATTERN.matcher(url.trim()).matches();
    }
}