package dev.shaaf.waver.backend;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    public static String getFolderNameFromInputPath(String pathString) {
        if (pathString == null || pathString.trim().isEmpty()) {
            return null;
        }
        Path path = Paths.get(pathString);
        Path fileNamePath = path.getFileName();
        if (fileNamePath == null) {
            return null;
        }

        String projectName = fileNamePath.toString();
        if (projectName.endsWith(".git")) {
            return projectName.substring(0, projectName.length() - 4);
        } else {
            return projectName;
        }
    }
}
