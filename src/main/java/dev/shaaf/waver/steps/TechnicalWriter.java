package dev.shaaf.waver.steps;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.shaaf.waver.model.Abstraction;
import dev.shaaf.waver.model.AbstractionList;
import dev.shaaf.waver.model.Chapter;
import dev.shaaf.waver.model.CodeFile;
import dev.shaaf.waver.prompts.TutorialWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Generates the content for each chapter of the tutorial.
 * <p>
 * This class is responsible for using the LLM to write the content of each chapter
 * based on the identified abstractions and relevant code files.
 * </p>
 */
public class TechnicalWriter {
    // Get a logger instance for this class
    private static final Logger logger = Logger.getLogger(TechnicalWriter.class.getName());

    /**
     * Builds the content for each chapter of the tutorial.
     * <p>
     * This method:
     * <ul>
     *   <li>Creates an AI service for writing tutorials</li>
     *   <li>For each chapter, finds the relevant abstraction and code files</li>
     *   <li>Uses the LLM to generate the content for each chapter</li>
     * </ul>
     * </p>
     *
     * @param codeFiles       the list of code files in the repository
     * @param chapterOrder    the ordered list of chapters
     * @param abstractionList the list of abstractions identified in the code
     * @param model           the LLM model to use for generating content
     * @return a list of strings, each containing the content of a chapter
     */
    public static List<String> build(List<CodeFile> codeFiles, List<Chapter> chapterOrder, AbstractionList abstractionList, ChatModel model) {
        TutorialWriter writer = AiServices.create(TutorialWriter.class, model);
        List<String> chapters = new ArrayList<>();
        for (Chapter chapter : chapterOrder) {
            logger.info("     - Writing chapter: " + chapter.name());
            Abstraction currentAbstraction = abstractionList.abstractions().stream()
                    .filter(a -> a.name().equals(chapter.name())).findFirst().orElseThrow();

            String relevantCode = currentAbstraction.relevantFiles().stream()
                    .flatMap(filePath -> codeFiles.stream().filter(cf -> cf.path().equals(filePath)))
                    .map(cf -> "--- File: " + cf.path() + " ---\n" + cf.content())
                    .collect(Collectors.joining("\n\n"));

            String chapterContent = writer.writeChapter(abstractionsAsString(abstractionList), chapter.name(), relevantCode);
            chapters.add(chapterContent);
        }
        return chapters;
    }

    /**
     * Converts a list of abstractions to a comma-separated string.
     * <p>
     * This utility method extracts the names of all abstractions and joins them
     * with commas for easier processing by the LLM.
     * </p>
     *
     * @param abstractionList the list of abstractions to convert
     * @return a comma-separated string of abstraction names
     */
    public static String abstractionsAsString(AbstractionList abstractionList) {
        return   abstractionList.abstractions().stream()
                .map(Abstraction::name)
                .collect(Collectors.joining(", "));
    }

}
