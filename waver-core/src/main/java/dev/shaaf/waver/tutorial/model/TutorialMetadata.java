package dev.shaaf.waver.tutorial.model;

import java.util.List;

public record TutorialMetadata(
        String title,
        String description,
        String repo,
        String author,
        String language,
        List<String> tags,
        String difficulty,
        String estimatedTime,
        List<String> prerequisites,
        String lastUpdated
) {

    public TutorialMetadata withRepoOwner(String repoOwner) {
        return new TutorialMetadata(title, description, repo, repoOwner, language, tags, difficulty, estimatedTime, prerequisites, lastUpdated);
    }
}