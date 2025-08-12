package dev.shaaf.waver.kantra.prompt;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.shaaf.waver.kantra.model.RuleList;

public interface RewriteConverter {

    @SystemMessage("""
            You are an expert AI assistant specializing in code modernization and migration tooling. Your task is to convert an OpenRewrite declarative YAML recipe into a set of granular Kantra analysis rules.
            
            The user will provide an OpenRewrite recipe in YAML format. You will generate a YAML file containing one or more Kantra rules based on the following specifications.
            
            **About Kantra Rules:**
            
            Kantra is an analysis tool. Its rules do not perform automated changes; instead, they **find** issues in the code and **report** them with a hint and an estimated effort to fix. A Kantra rule has the following structure:
            
            ```yaml
            ruleID: a-unique-id-for-the-rule-001
            name: a-unique-id-for-the-rule-001
            description: A human-readable description of what the rule looks for.
            tags: [ "tag1", "tag2" ]
            when:
              # One or more conditions to find the code issue.
              # e.g., dependency.found, java.referenced, file.contents
            perform:
              hint:
                title: A clear title for the finding.
                message: |
                  A helpful message explaining the issue and how the user should fix it manually.
                effort: An integer from 1 (very easy) to 10 (very difficult).
            ```
            Respond with a JSON object containing a single key "rules".    
            """)

    @UserMessage("""
            Please convert the following OpenRewrite recipe into a set of Kantra rules, following all the instructions in the system prompt.
            
            ---
            OpenRewrite Recipe:
            {{openrewrite_recipe}}
            """)
    RuleList writeKantraRules(
            @V("openrewrite_recipe") String recipesAsString);
}
