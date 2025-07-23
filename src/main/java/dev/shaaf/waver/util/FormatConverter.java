package dev.shaaf.waver.util;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.pdf.converter.PdfConverterExtension;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * Utility class for converting between different document formats.
 * <p>
 * This class provides methods for converting Markdown files to HTML and PDF formats.
 * It uses the flexmark-java library for parsing Markdown and rendering HTML and PDF.
 * </p>
 */
public class FormatConverter {
    private static final Logger logger = Logger.getLogger(FormatConverter.class.getName());
    
    /**
     * Supported output formats.
     */
    public enum OutputFormat {
        MARKDOWN,
        HTML,
        PDF
    }
    
    /**
     * Converts a Markdown file to the specified output format.
     *
     * @param inputFile the Markdown file to convert
     * @param outputFormat the desired output format
     * @return the path to the converted file, or null if conversion failed
     * @throws IOException if an error occurs during conversion
     */
    public static String convertFile(File inputFile, OutputFormat outputFormat) throws IOException {
        if (!inputFile.exists() || !inputFile.isFile()) {
            throw new IOException("Input file does not exist or is not a file: " + inputFile.getAbsolutePath());
        }
        
        if (outputFormat == OutputFormat.MARKDOWN) {
            // No conversion needed
            return inputFile.getAbsolutePath();
        }
        
        String content = Files.readString(inputFile.toPath());
        String outputPath = getOutputPath(inputFile, outputFormat);
        
        switch (outputFormat) {
            case HTML:
                convertToHtml(content, outputPath);
                break;
            case PDF:
                convertToPdf(content, outputPath);
                break;
            default:
                throw new IllegalArgumentException("Unsupported output format: " + outputFormat);
        }
        
        return outputPath;
    }
    
    /**
     * Converts all Markdown files in a directory to the specified output format.
     *
     * @param inputDir the directory containing Markdown files
     * @param outputFormat the desired output format
     * @return the number of files successfully converted
     * @throws IOException if an error occurs during conversion
     */
    public static int convertDirectory(File inputDir, OutputFormat outputFormat) throws IOException {
        if (!inputDir.exists() || !inputDir.isDirectory()) {
            throw new IOException("Input directory does not exist or is not a directory: " + inputDir.getAbsolutePath());
        }
        
        if (outputFormat == OutputFormat.MARKDOWN) {
            // No conversion needed
            return 0;
        }
        
        int count = 0;
        File[] files = inputDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".md"));
        
        if (files == null || files.length == 0) {
            logger.warning("No Markdown files found in directory: " + inputDir.getAbsolutePath());
            return 0;
        }
        
        for (File file : files) {
            try {
                convertFile(file, outputFormat);
                count++;
            } catch (IOException e) {
                logger.warning("Failed to convert file: " + file.getAbsolutePath() + " - " + e.getMessage());
            }
        }
        
        return count;
    }
    
    /**
     * Converts Markdown content to HTML and writes it to the specified output path.
     *
     * @param markdown the Markdown content to convert
     * @param outputPath the path to write the HTML output to
     * @throws IOException if an error occurs during conversion
     */
    private static void convertToHtml(String markdown, String outputPath) throws IOException {
        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        
        Node document = parser.parse(markdown);
        String html = renderer.render(document);
        
        // Add HTML boilerplate
        html = "<!DOCTYPE html>\n" +
               "<html>\n" +
               "<head>\n" +
               "    <meta charset=\"UTF-8\">\n" +
               "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
               "    <style>\n" +
               "        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif; line-height: 1.6; max-width: 800px; margin: 0 auto; padding: 20px; }\n" +
               "        pre { background-color: #f5f5f5; padding: 10px; border-radius: 5px; overflow-x: auto; }\n" +
               "        code { font-family: 'Courier New', Courier, monospace; }\n" +
               "        img { max-width: 100%; }\n" +
               "        table { border-collapse: collapse; width: 100%; }\n" +
               "        th, td { border: 1px solid #ddd; padding: 8px; }\n" +
               "        tr:nth-child(even) { background-color: #f2f2f2; }\n" +
               "    </style>\n" +
               "</head>\n" +
               "<body>\n" +
               html +
               "</body>\n" +
               "</html>";
        
        Files.writeString(Paths.get(outputPath), html);
    }
    
    /**
     * Converts Markdown content to PDF and writes it to the specified output path.
     *
     * @param markdown the Markdown content to convert
     * @param outputPath the path to write the PDF output to
     * @throws IOException if an error occurs during conversion
     */
    private static void convertToPdf(String markdown, String outputPath) throws IOException {
        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        Node document = parser.parse(markdown);
        
        // Convert to PDF
        PdfConverterExtension.exportToPdf(outputPath, markdown, "", options);
    }
    
    /**
     * Gets the output path for a converted file.
     *
     * @param inputFile the input file
     * @param outputFormat the output format
     * @return the output path
     */
    private static String getOutputPath(File inputFile, OutputFormat outputFormat) {
        String inputPath = inputFile.getAbsolutePath();
        String extension = switch (outputFormat) {
            case HTML -> ".html";
            case PDF -> ".pdf";
            default -> ".md";
        };
        
        // Replace .md extension with the new extension
        if (inputPath.toLowerCase().endsWith(".md")) {
            return inputPath.substring(0, inputPath.length() - 3) + extension;
        } else {
            return inputPath + extension;
        }
    }
}