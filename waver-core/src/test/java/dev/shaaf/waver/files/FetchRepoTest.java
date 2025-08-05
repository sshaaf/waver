package dev.shaaf.waver.files;

import dev.shaaf.waver.tutorial.model.CodeFile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FetchRepoTest {

    // JUnit 5 will create and clean up this temporary directory for each test.
    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Should find all .java files in nested directories")
    void crawl_WithJavaFilesInNestedDirectories_ReturnsAllJavaFiles() throws IOException {
        // Arrange: Create a directory structure with various file types
        Path subDir = Files.createDirectory(tempDir.resolve("subdir"));
        Files.writeString(tempDir.resolve("Root.java"), "class Root {}");
        Files.writeString(subDir.resolve("Nested.java"), "class Nested {}");
        Files.writeString(tempDir.resolve("notes.txt"), "some text"); // Should be ignored

        // Act
        List<CodeFile> results = FetchRepo.crawl(tempDir.toString());

        // Assert
        assertEquals(2, results.size(), "Should find exactly two .java files.");

        assertTrue(results.stream().anyMatch(f -> f.path().endsWith("Root.java") && f.content().equals("class Root {}")),
                "Should find and read Root.java correctly.");

        assertTrue(results.stream().anyMatch(f -> f.path().endsWith("Nested.java") && f.content().equals("class Nested {}")),
                "Should find and read Nested.java correctly.");
    }

    @Test
    @DisplayName("Should return an empty list for a directory with no .java files")
    void crawl_WithNoJavaFiles_ReturnsEmptyList() throws IOException {
        // Arrange: Create only non-Java files
        Files.writeString(tempDir.resolve("README.md"), "# Project");
        Files.writeString(tempDir.resolve("config.json"), "{}");

        // Act
        List<CodeFile> results = FetchRepo.crawl(tempDir.toString());

        // Assert
        assertTrue(results.isEmpty(), "The returned list should be empty.");
    }

    @Test
    @DisplayName("Should return an empty list for an empty directory")
    void crawl_WithEmptyDirectory_ReturnsEmptyList() throws IOException {
        // Act
        List<CodeFile> results = FetchRepo.crawl(tempDir.toString());

        // Assert
        assertTrue(results.isEmpty(), "The returned list should be empty for an empty directory.");
    }

    @Test
    @DisplayName("Should throw NoSuchFileException for a non-existent directory")
    void crawl_WithNonExistentDirectory_ThrowsException() {
        // Arrange
        String nonExistentPath = tempDir.resolve("non_existent_dir").toString();

        // Act & Assert
        assertThrows(NoSuchFileException.class, () -> {
            FetchRepo.crawl(nonExistentPath);
        }, "Should throw an exception when the root directory does not exist.");
    }

    @Test
    @DisplayName("Should create CodeFile with error message for unreadable file")
    @DisabledOnOs(OS.WINDOWS) // File.setReadable(false) is not reliable on Windows
    void crawl_WithUnreadableFile_ReturnsFileWithErrorContent() throws IOException {
        // Arrange
        Path unreadableFile = tempDir.resolve("Unreadable.java");
        Files.writeString(unreadableFile, "class Unreadable {}");

        File javaIoFile = unreadableFile.toFile();
        if (!javaIoFile.setReadable(false)) {
            fail("Failed to set file as unreadable. Cannot complete test.");
        }

        try {
            // Act
            List<CodeFile> results = FetchRepo.crawl(tempDir.toString());

            // Assert
            assertEquals(1, results.size());
            Optional<CodeFile> foundFile = results.stream().findFirst();
            assertTrue(foundFile.isPresent());
            assertTrue(foundFile.get().content().startsWith("Error reading file:"), "Content should be an error message.");

        } finally {
            // Cleanup: Ensure the file is readable again so JUnit can delete it.
            javaIoFile.setReadable(true);
        }
    }
}