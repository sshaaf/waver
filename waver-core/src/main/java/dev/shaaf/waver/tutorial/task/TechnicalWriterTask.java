package dev.shaaf.waver.tutorial.task;

import java.util.concurrent.CompletableFuture;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.shaaf.waver.core.PipelineContext;
import dev.shaaf.waver.core.Task;
import dev.shaaf.waver.core.TaskRunException;
import dev.shaaf.waver.tutorial.model.Abstraction;
import dev.shaaf.waver.tutorial.model.Chapter;
import dev.shaaf.waver.tutorial.model.GenerationContext;
import dev.shaaf.waver.tutorial.prompt.IntroChapter;
import dev.shaaf.waver.tutorial.prompt.TutorialWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TechnicalWriterTask implements Task<GenerationContext, GenerationContext> {
    // Get a logger instance for this class
    private static final Logger logger = Logger.getLogger(TechnicalWriterTask.class.getName());

    private ChatModel chatModel;
    private Path outputDir;

    public TechnicalWriterTask(ChatModel chatModel, Path outputDir){
        this.chatModel = chatModel;
        this.outputDir = outputDir;
    }

    @Override
    public CompletableFuture<GenerationContext> execute(GenerationContext generationContext, PipelineContext context) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Files.createDirectories(outputDir);
                TutorialWriter writer = AiServices.create(TutorialWriter.class, chatModel);
                IntroChapter introChapter = AiServices.create(IntroChapter.class, chatModel);

                StringBuilder introChapterContent = new StringBuilder();
                introChapterContent.append(introChapter.writeChapter(generationContext.abstractionsAsString(), generationContext.codeAsString()));
                introChapterContent.append("## Chapters\n\n");
                for (Chapter chapter : generationContext.chapterList().chapterList()) {
                    logger.info("     - Writing chapter: " + chapter.name());
                    writeChapter(generationContext, writer, introChapterContent, chapter);
                }

                writeChapterToDisk(outputDir, "index.md", introChapterContent.toString());
                logger.info("✅ Tutorial generated at: " + outputDir.toAbsolutePath());

                return generationContext;
            } catch (Exception e) {
                throw new TaskRunException("Failed to write technical documentation", e);
            }
        });
    }


    private void writeChapter(GenerationContext generationContext, TutorialWriter writer, StringBuilder introChapterContent, Chapter chapter){
        Abstraction currentAbstraction = generationContext.abstractions().abstractions().stream()
                .filter(a -> a.name().equals(chapter.name())).findFirst().orElseThrow();

        String relevantCode = currentAbstraction.relevantFiles().stream()
                .flatMap(filePath -> generationContext.codeFiles().stream().filter(cf -> cf.path().equals(filePath)))
                .map(cf -> "--- File: " + cf.path() + " ---\n" + cf.content())
                .collect(Collectors.joining("\n\n"));

        String chapterContent = writer.writeChapter(generationContext.abstractionsAsString(), chapter.name(), relevantCode);
        introChapterContent.append(String.format("* [%s](./%s)\n", chapter.name(), chapter.name().replaceAll("\\s+", "-") + ".md"));

        writeChapterToDisk(outputDir, getChapterFileName(chapter.name()), chapterContent);
    }

    private String getChapterFileName(String chapterName) {
        return String.format("%s.md", chapterName.replaceAll("\\s+", "-"));
    }

    private void writeChapterToDisk(Path outputDir, String fileName, String content) {
        try {
            logger.info("     - Writing file: " + fileName);
            Files.writeString(outputDir.resolve(fileName), content);
        } catch (IOException e) {
            throw new TaskRunException(e);
        }
    }


}
