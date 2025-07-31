package dev.shaaf.waver.files;

import dev.shaaf.waver.model.CodeFile;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FetchRepo {

    public static List<CodeFile> crawl(String rootDirectory) throws IOException {
        List<Path> files = new ArrayList<>();
        Files.walkFileTree(Paths.get(rootDirectory), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (!Files.isDirectory(file) && file.toString().endsWith(".java")) {
                    files.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        return files.stream().map(path -> {
            try {
                return new CodeFile(path.toString(), Files.readString(path));
            } catch (IOException e) {
                // Handle exceptions appropriately
                return new CodeFile(path.toString(), "Error reading file: " + e.getMessage());
            }
        }).collect(Collectors.toList());
    }
}