package dev.shaaf.waver.files;

import dev.shaaf.waver.tutorial.model.CodeFile;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FetchRepo {

    public static List<CodeFile> crawl(String rootDirectory) throws IOException {
        return crawl(rootDirectory, List.of(".java"), new ArrayList<>());
    }

    public static List<CodeFile> crawl(String rootDirectory, List<String> fileIncludeExtensions, List<String> fileExtensionsExclude) throws IOException {
        List<Path> files = new ArrayList<>();

        // Handle null lists gracefully by treating them as empty.
        final List<String> includes = (fileIncludeExtensions == null) ? Collections.emptyList() : fileIncludeExtensions;
        final List<String> excludes = (fileExtensionsExclude == null) ? Collections.emptyList() : fileExtensionsExclude;

        Files.walkFileTree(Paths.get(rootDirectory), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (!Files.isDirectory(file)) {
                    String fileName = file.toString();

                    // Check for exclusion first, as it has higher priority.
                    boolean isExcluded = excludes.stream().anyMatch(fileName::endsWith);

                    if (!isExcluded) {
                        // If the include list is empty, include all files not excluded.
                        // Otherwise, check if the file matches any include extension.
                        boolean isIncluded = includes.isEmpty() || includes.stream().anyMatch(fileName::endsWith);

                        if (isIncluded) {
                            files.add(file);
                        }
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });

        // The rest of the function remains the same.
        return files.stream().map(path -> {
            try {
                return new CodeFile(path.toString(), Files.readString(path));
            } catch (IOException e) {
                // Handle exceptions appropriately. For this example, we create a
                // CodeFile with an error message in its content.
                return new CodeFile(path.toString(), "Error reading file: " + e.getMessage());
            }
        }).collect(Collectors.toList());
    }
}