package dev.shaaf.waver.tutorial.task;

import dev.shaaf.waver.core.PipelineContext;
import dev.shaaf.waver.tutorial.model.GenerationContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;


class CodeCrawlerTaskTest {

    @TempDir
    Path tempDir; // A temporary directory created by JUnit for each test

    @Nested
    @DisplayName("Tests for checkInputType method")
    class CheckInputTypeTests {

        @Test
        @DisplayName("Should return LOCAL_DIRECTORY for an existing directory")
        void checkInputType_WhenPathIsDirectory_ShouldReturnLocalDirectory() throws IOException {
            // Arrange
            Path subDir = Files.createDirectory(tempDir.resolve("my_project"));

            // Act
            CodeCrawlerTask.InputType result = CodeCrawlerTask.checkInputType(subDir.toString());

            // Assert
            assertEquals(CodeCrawlerTask.InputType.LOCAL_DIRECTORY, result);
        }

        @Test
        @DisplayName("Should throw InvalidPathException for a path that is a file")
        void checkInputType_WhenPathIsFile_ShouldThrowException() throws IOException {
            // Arrange
            Path testFile = Files.createFile(tempDir.resolve("my_file.txt"));

            // Act & Assert
            assertThrows(InvalidPathException.class, () -> {
                CodeCrawlerTask.checkInputType(testFile.toString());
            });
        }

        @Test
        @DisplayName("Should not fail, even if the git repo doesnt exits")
        void checkInputType_WhenGivenGitUrl_ShouldThrowException() {
            String validGitUrl = "https://github.com/user/repo.git";
            assertEquals(CodeCrawlerTask.InputType.GIT_URL, CodeCrawlerTask.checkInputType(validGitUrl));
        }
    }

    @Nested
    @DisplayName("Tests for crawl method")
    class CrawlTests {

        @Test
        @DisplayName("Nothing to crawl")
        void crawl_WhenInputIsLocalDirectory_ShouldSucceed() throws IOException {
            // Arrange
            Path projectDir = Files.createDirectory(tempDir.resolve("local_project"));

            // Act
            CodeCrawlerTask task = new CodeCrawlerTask();
            GenerationContext result = task.execute(projectDir.toString(), new PipelineContext());

            // Assert
            assertNotNull(result);
            assertEquals(0, result.codeFiles().size());
        }

        @Test
        @DisplayName("Should throw InvalidPathException for a path that is not a directory")
        void crawl_WhenInputIsNotDirectory_ShouldThrowException() throws IOException {
            // Arrange
            Path testFile = Files.createFile(tempDir.resolve("a_file.txt"));

            // Act & Assert
            assertThrows(InvalidPathException.class, () -> {
                CodeCrawlerTask task = new CodeCrawlerTask();
                GenerationContext result = task.execute(testFile.toString(), new PipelineContext());
            });
        }
    }
}