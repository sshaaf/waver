package dev.shaaf.waver.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FormatConverterTest {

    @TempDir
    Path tempDir;
    
    private File markdownFile;
    private String markdownContent;
    
    @BeforeEach
    void setUp() throws IOException {
        // Create a temporary markdown file for testing
        markdownContent = "# Test Heading\n\nThis is a test paragraph.\n\n```java\nSystem.out.println(\"Hello World\");\n```";
        markdownFile = tempDir.resolve("test.md").toFile();
        Files.writeString(markdownFile.toPath(), markdownContent);
    }
    
    @Test
    void convertFile_markdownToMarkdown_returnsOriginalPath() throws IOException {
        // When converting markdown to markdown, it should return the original path
        String result = FormatConverter.convertFile(markdownFile, FormatConverter.OutputFormat.MARKDOWN);
        
        // Assert
        assertEquals(markdownFile.getAbsolutePath(), result);
    }
    
    @Test
    void convertFile_markdownToHtml_createsHtmlFile() throws IOException {
        // Convert markdown to HTML
        String htmlPath = FormatConverter.convertFile(markdownFile, FormatConverter.OutputFormat.HTML);
        
        // Assert
        File htmlFile = new File(htmlPath);
        assertTrue(htmlFile.exists());
        assertTrue(htmlPath.endsWith(".html"));
        
        // Check content
        String htmlContent = Files.readString(htmlFile.toPath());
        assertTrue(htmlContent.contains("<h1>Test Heading</h1>"));
        assertTrue(htmlContent.contains("<pre><code class=\"language-java\">"));
    }
    
    @Test
    void convertFile_markdownToPdf_createsPdfFile() throws IOException {
        // Convert markdown to PDF
        String pdfPath = FormatConverter.convertFile(markdownFile, FormatConverter.OutputFormat.PDF);
        
        // Assert
        File pdfFile = new File(pdfPath);
        assertTrue(pdfFile.exists());
        assertTrue(pdfPath.endsWith(".pdf"));
        
        // We can't easily check PDF content, but we can verify the file exists and has content
        assertTrue(pdfFile.length() > 0);
    }
    
    @Test
    void convertFile_nonExistentFile_throwsIOException() {
        // Try to convert a non-existent file
        File nonExistentFile = new File(tempDir.toFile(), "non-existent.md");
        
        // Assert
        assertThrows(IOException.class, () -> 
            FormatConverter.convertFile(nonExistentFile, FormatConverter.OutputFormat.HTML)
        );
    }
    
    @Test
    void convertDirectory_markdownToHtml_convertsAllMarkdownFiles() throws IOException {
        // Create additional markdown files
        File markdownFile2 = tempDir.resolve("test2.md").toFile();
        Files.writeString(markdownFile2.toPath(), "# Second Test\n\nAnother test file.");
        
        // Convert all markdown files in the directory to HTML
        int count = FormatConverter.convertDirectory(tempDir.toFile(), FormatConverter.OutputFormat.HTML);
        
        // Assert
        assertEquals(2, count);
        assertTrue(new File(tempDir.toFile(), "test.html").exists());
        assertTrue(new File(tempDir.toFile(), "test2.html").exists());
    }
    
    @Test
    void convertDirectory_emptyDirectory_returnsZero() throws IOException {
        // Create an empty directory
        Path emptyDir = tempDir.resolve("empty");
        Files.createDirectory(emptyDir);
        
        // Try to convert files in the empty directory
        int count = FormatConverter.convertDirectory(emptyDir.toFile(), FormatConverter.OutputFormat.HTML);
        
        // Assert
        assertEquals(0, count);
    }
    
    @Test
    void convertDirectory_nonExistentDirectory_throwsIOException() {
        // Try to convert files in a non-existent directory
        File nonExistentDir = new File(tempDir.toFile(), "non-existent-dir");
        
        // Assert
        assertThrows(IOException.class, () -> 
            FormatConverter.convertDirectory(nonExistentDir, FormatConverter.OutputFormat.HTML)
        );
    }
}