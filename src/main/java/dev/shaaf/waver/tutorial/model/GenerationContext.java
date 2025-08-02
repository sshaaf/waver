package dev.shaaf.waver.tutorial.model;

import java.util.List;
import java.util.stream.Collectors;


public record GenerationContext(
        List<CodeFile> codeFiles,
        AbstractionList abstractions,
        RelationshipAnalysis relationshipAnalysis,
        ChapterList chapterList
) {

    public GenerationContext withCodeFiles(List<CodeFile> codeFiles) {
        return new GenerationContext(codeFiles, this.abstractions, this.relationshipAnalysis, this.chapterList);
    }

    public GenerationContext withAbstractions(AbstractionList abstractions) {
        return new GenerationContext(this.codeFiles, abstractions, this.relationshipAnalysis, this.chapterList);
    }

    public GenerationContext withRelationshipAnalysis(RelationshipAnalysis relationshipAnalysis) {
        return new GenerationContext(this.codeFiles, this.abstractions, relationshipAnalysis, this.chapterList);
    }

    public GenerationContext withChapterList(ChapterList chapterList) {
        return new GenerationContext(this.codeFiles, this.abstractions, this.relationshipAnalysis, chapterList);
    }


    public String codeAsString() {
        return codeFiles.stream()
                .map(file -> "--- File: " + file.path() + " ---\n" + file.content())
                .collect(Collectors.joining("\n\n"));
    }

    public String abstractionsAsString() {
        return abstractions.abstractions().stream()
                .map(Abstraction::name)
                .collect(Collectors.joining(", "));
    }

}