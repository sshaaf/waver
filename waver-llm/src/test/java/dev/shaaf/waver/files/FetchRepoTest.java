package dev.shaaf.waver.files;

import dev.shaaf.waver.tutorial.model.CodeFile;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FetchRepoTest {

    // JUnit 5 will create and clean up this temporary directory for each test.
    @TempDir
    Path tempDir;

    @TempDir
    Path emptyDir;

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
        Files.writeString(emptyDir.resolve("README.md"), "# Project");
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
        List<CodeFile> results = FetchRepo.crawl(emptyDir.toString());

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
    @DisabledOnOs(OS.WINDOWS)
        // File.setReadable(false) is not reliable on Windows
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


    Path getTempDir() throws IOException {
        // Create root level files
        createFile(tempDir, "main.java", "public class Main {}");
        createFile(tempDir, "component.kt", "fun main() {}");
        createFile(tempDir, "config.xml", "<config></config>");
        createFile(tempDir, "ignore.txt", "some text");

        // Create nested directories and files
        Path nestedDir = tempDir.resolve("nested");
        Files.createDirectory(nestedDir);
        createFile(nestedDir, "Helper.java", "class Helper {}");
        createFile(nestedDir, "model.kt", "data class Model()");

        Path buildDir = nestedDir.resolve("build");
        Files.createDirectory(buildDir);
        createFile(buildDir, "main.class", "class bytecode");
        return tempDir;
    }

    /**
     * Helper method to create a file with specific content in a given directory.
     */
    private void createFile(Path directory, String fileName, String content) throws IOException {
        Files.writeString(directory.resolve(fileName), content);
    }

    @Test
    void testCrawl_onlyJavaFiles() throws IOException {
        System.out.println("Running test: testCrawl_onlyJavaFiles");
        List<String> includes = Collections.singletonList(".java");
        List<String> excludes = Collections.emptyList();

        List<CodeFile> result = FetchRepo.crawl(getTempDir().toString(), includes, excludes);

        assertEquals(2, result.size(), "Should find 2 Java files.");
        assertFileFound(result, "main.java");
        assertFileFound(result, "Helper.java");
    }

    @Test
    void testCrawl_multipleIncludeExtensions() throws IOException {
        System.out.println("Running test: testCrawl_multipleIncludeExtensions");
        List<String> includes = Arrays.asList(".java", ".kt");
        List<String> excludes = Collections.emptyList();

        List<CodeFile> result = FetchRepo.crawl(getTempDir().toString(), includes, excludes);

        assertEquals(4, result.size(), "Should find 4 files (.java and .kt).");
        assertFileFound(result, "main.java");
        assertFileFound(result, "Helper.java");
        assertFileFound(result, "component.kt");
        assertFileFound(result, "model.kt");
    }

    @Test
    void testCrawl_withExclusion() throws IOException {
        System.out.println("Running test: testCrawl_withExclusion");
        // Include all files by leaving the include list empty
        List<String> includes = Collections.emptyList();
        // Exclude .class and .txt files
        List<String> excludes = Arrays.asList(".class", ".txt");

        List<CodeFile> result = FetchRepo.crawl(getTempDir().toString(), includes, excludes);

        assertEquals(5, result.size(), "Should find 5 files, excluding .class and .txt.");
        assertFileFound(result, "main.java");
        assertFileFound(result, "Helper.java");
        assertFileFound(result, "component.kt");
        assertFileFound(result, "model.kt");
        assertFileFound(result, "config.xml");
        assertFileIsMissing(result, "main.class");
        assertFileIsMissing(result, "ignore.txt");
    }

    @Test
    void testCrawl_exclusionTakesPrecedence() throws IOException {
        System.out.println("Running test: testCrawl_exclusionTakesPrecedence");
        // Try to include .java, but also exclude it. Exclusion should win.
        List<String> includes = Collections.singletonList(".java");
        List<String> excludes = Collections.singletonList(".java");

        List<CodeFile> result = FetchRepo.crawl(tempDir.toString(), includes, excludes);

        assertEquals(0, result.size(), "Should find 0 files as .java is excluded.");
    }

    @Test
    void testCrawl_emptyIncludeList_shouldReturnAllNonExcluded() throws IOException {
        System.out.println("Running test: testCrawl_emptyIncludeList_shouldReturnAllNonExcluded");
        List<String> includes = Collections.emptyList();
        List<String> excludes = Collections.singletonList(".class"); // Only exclude .class

        List<CodeFile> result = FetchRepo.crawl(getTempDir().toString(), includes, excludes);

        assertEquals(6, result.size(), "Should find all 6 files except the .class file.");
        assertFileIsMissing(result, "main.class");
    }

    @Test
    void testCrawl_withNullLists_shouldBehaveAsEmpty() throws IOException {
        System.out.println("Running test: testCrawl_withNullLists_shouldBehaveAsEmpty");
        // Passing null should be handled gracefully and act like an empty list.
        // This should return all files since there are no effective include/exclude rules.
        List<CodeFile> result = FetchRepo.crawl(getTempDir().toString(), null, null);

        assertEquals(7, result.size(), "Should find all 7 files when lists are null.");
    }

    @Test
    void testCrawl_noMatchingFiles() throws IOException {
        System.out.println("Running test: testCrawl_noMatchingFiles");
        List<String> includes = Collections.singletonList(".nonexistent");
        List<String> excludes = Collections.emptyList();

        List<CodeFile> result = FetchRepo.crawl(tempDir.toString(), includes, excludes);

        assertTrue(result.isEmpty(), "Should return an empty list if no files match.");
    }

    @Test
    void testCrawl_emptyDirectory() throws IOException {
        System.out.println("Running test: testCrawl_emptyDirectory");
        Path emptyDir = Files.createDirectory(tempDir.resolve("empty"));
        List<String> includes = Collections.singletonList(".java");

        List<CodeFile> result = FetchRepo.crawl(emptyDir.toString(), includes, null);

        assertTrue(result.isEmpty(), "Should return an empty list for an empty directory.");
    }

    @Test
    void testCrawl_readsFileContentCorrectly() throws IOException {
        System.out.println("Running test: testCrawl_readsFileContentCorrectly");
        List<String> includes = Collections.singletonList(".xml");
        List<CodeFile> result = FetchRepo.crawl(getTempDir().toString(), includes, null);

        assertEquals(1, result.size());
        CodeFile xmlFile = result.get(0);

        assertTrue(xmlFile.path().endsWith("config.xml"));
        assertEquals("<config></config>", xmlFile.content(), "File content should be read correctly.");
    }


    // Helper assertion methods for cleaner tests

    private void assertFileFound(List<CodeFile> files, String fileName) {
        assertTrue(files.stream().anyMatch(f -> f.path().endsWith(fileName)),
                "File '" + fileName + "' should have been found.");
    }

    private void assertFileIsMissing(List<CodeFile> files, String fileName) {
        assertFalse(files.stream().anyMatch(f -> f.path().endsWith(fileName)),
                "File '" + fileName + "' should NOT have been found.");
    }
}
