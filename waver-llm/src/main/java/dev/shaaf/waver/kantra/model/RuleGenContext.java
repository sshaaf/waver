package dev.shaaf.waver.kantra.model;


import dev.shaaf.waver.tutorial.model.CodeFile;

import java.util.List;
import java.util.stream.Collectors;

public record RuleGenContext (List<CodeFile> rewriteRecipe, RuleList kanraRules){

    public RuleGenContext withKantraRules(RuleList kantraRules) {
        return new RuleGenContext(this.rewriteRecipe, kanraRules);
    }

    public String recipeAsString() {
        return rewriteRecipe.stream()
                .map(file -> "--- File: " + file.path() + " ---\n" + file.content())
                .collect(Collectors.joining("\n\n"));
    }
}
