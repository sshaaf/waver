package dev.shaaf.waver.tutorial.task;

import dev.shaaf.waver.core.PipelineContext;
import dev.shaaf.waver.core.Task;
import dev.shaaf.waver.core.TaskRunException;
import dev.shaaf.waver.util.files.FetchRepo;
import dev.shaaf.waver.util.files.GitHubRepoFetcher;
import dev.shaaf.waver.tutorial.model.GenerationContext;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class CodeCrawlerTask implements Task<String, GenerationContext> {

    @Override
    public GenerationContext execute(String inputString, PipelineContext context) throws TaskRunException {
        InputType inputType = checkInputType(inputString);
        System.out.println("Input type: " + inputType);

        if(inputType == InputType.GIT_URL){
            try {
                return new GenerationContext(FetchRepo.crawl(GitHubRepoFetcher.getAndCloneRepo(inputString)), null, null, null);
            } catch (IOException e) {
                e.printStackTrace();
                throw new TaskRunException(e);
            }
        }
        else if(inputType == InputType.LOCAL_DIRECTORY){
            try {
                return new GenerationContext(FetchRepo.crawl(inputString), null, null, null);
            } catch (IOException e) {
                throw new TaskRunException(e);
            }
        }
        else
            throw new TaskRunException(inputString, "Invalid input type");
    }

    public enum InputType {
        GIT_URL,
        LOCAL_DIRECTORY,
        INVALID
    }


    static InputType checkInputType(String inputString) throws InvalidPathException {

        if (inputString == null || inputString.isBlank()) {
            throw new InvalidPathException(inputString, "Invalid path");
        }

        if((inputString.startsWith("https://") || inputString.startsWith("http://") || inputString.startsWith("git@"))
                && inputString.endsWith(".git")){
            URI.create(inputString);
            return InputType.GIT_URL;
        }

        Path path = Paths.get(inputString);
        if (Files.isDirectory(path))
            return InputType.LOCAL_DIRECTORY;

        throw new InvalidPathException(inputString, "Invalid path");
    }



}
