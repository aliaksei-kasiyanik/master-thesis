package com.akasiyanik.trip.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author akasiyanik
 *         5/11/17
 */
public final class IOUtils {

    public static String readFileAsString(String filename) {
        try {
            return new String(Files.readAllBytes(Paths.get(filename)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeToFile(String filename, String s) {
        Path path = Paths.get(filename);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
