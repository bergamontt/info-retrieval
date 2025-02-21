package utils;

import java.io.File;

public class FileLoader {

    public static File loadFolder(String directoryName) {
        File file = new File(directoryName);
        validateDirectoryFile(file);
        return file;
    }

    public static File loadFile(String fileName) {
        File file = new File(fileName);
        validateFile(file);
        return file;
    }

    private static void validateDirectoryFile(File directory) {
        if (!isValidDirectoryFile(directory))
            throw new IllegalArgumentException("Directory " + directory.getName() + " does not exist or is not a directory");
    }

    private static boolean isValidDirectoryFile(File directory) {
        return directory.exists() && directory.isDirectory();
    }

    private static void validateFile(File file) {
        if (!isValidFile(file))
            throw new IllegalArgumentException("File " + file.getName() + " does not exist or is not a file");
    }

    private static boolean isValidFile(File file) {
        return file.exists() && file.isFile();
    }

}