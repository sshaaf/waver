package dev.shaaf.waver.files;

import dev.shaaf.waver.model.CodeFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileInputHandler {

    public enum InputType {
        GIT_URL,
        LOCAL_DIRECTORY,
        INVALID
    }

    public static List<CodeFile> crawl(String inputString) throws IOException {

        InputType inputType = checkInputType(inputString);
        System.out.println("Input type: " + inputType);

        if(inputType == InputType.GIT_URL){
            return FetchRepo.crawl(GitHubRepoFetcher.getRepositoryName(inputString));
        }
        else if(inputType == InputType.LOCAL_DIRECTORY){
            return FetchRepo.crawl(inputString);
        }
        else
            throw new InvalidPathException(inputString, "Invalid input type");

    }

    public static InputType checkInputType(String inputString) throws InvalidPathException {

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
