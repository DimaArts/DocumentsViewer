package ru.dimaarts.documentsreader.utils;

import java.io.File;

/**
 * Created by gorshunovdv on 1/20/2017.
 */
public class FileUtils {
    public static final char EXTENSION_SEPARATOR = '.';
    private static final String ANDROID_SEPARATOR = File.separator;

    public static int indexOfLastSeparator(String filename) {
        if (filename == null) {
            return -1;
        }
        return filename.lastIndexOf(ANDROID_SEPARATOR);
    }

    public static int indexOfExtension(String filename) {
        if (filename == null) {
            return -1;
        }
        int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);
        int lastSeparator = indexOfLastSeparator(filename);
        return (lastSeparator > extensionPos ? -1 : extensionPos);
    }

    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int index = indexOfExtension(filename);
        if (index == -1) {
            return "";
            } else {
                return filename.substring(index + 1);
            }
        }
}
