package dev.shaaf.waver.prompts;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.shaaf.waver.model.RelationshipAnalysis;


public interface RelationshipAnalyzer {
    @SystemMessage("""
        You are a senior software architect. Based on the provided code and a list of abstractions,
        analyze and describe the relationships between these abstractions.
        Note relationships like composition, inheritance, method calls, etc.
        First, provide a high-level summary. Then, list the specific relationships.
        
        Based on the following abstractions and relevant code snippets from the projectName:
            
        Please provide:
                1. A high-level `summary` of the project's main purpose and functionality in a few beginner-friendly sentences{lang_hint}. Use markdown formatting with **bold** and *italic* text to highlight important concepts.
                2. A list (`relationships`) describing the key interactions between these abstractions. For each relationship, specify:
                    - `from`: Index of the source abstraction (e.g., `0 # AbstractionName1`)
                    - `to`: Index of the target abstraction (e.g., `1 # AbstractionName2`)
                    - `description`: A brief label for the interaction **in just a few words**{lang_hint} (e.g., "Manages", "Inherits", "Uses").
                    Ideally the relationship should be backed by one abstraction calling or passing parameters to another.
                    Simplify the relationship and exclude those non-important ones.
            
                IMPORTANT: Make sure EVERY abstraction is involved in at least ONE relationship (either as source or target). Each abstraction index must appear at least once across all relationships.
        
        Respond with a JSON object containing "summary" and "relationships".
    """)
    @UserMessage("""
        ProjectName: {{projectName}}
        Codebase:
        {{codebase}}
        ---
        Abstractions:
        {{abstractions}}
    """)
    RelationshipAnalysis analyzeRelationships(@V("codebase") String codebase, @V("abstractions") String abstractions, @V("projectName") String projectName);
}