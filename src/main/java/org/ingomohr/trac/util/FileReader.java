package org.ingomohr.trac.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Reads files.
 */
public class FileReader {

    /**
     * Reads all lines from the file under the given path.
     * 
     * @param path the path. Cannot be <code>null</code>
     * @return all lines. Never <code>null</code>, probably empty.
     * @throws IOException if reading fails.
     */
    public List<String> readAllLines(Path path) throws IOException {
        return Files.readAllLines(Objects.requireNonNull(path));
    }

}