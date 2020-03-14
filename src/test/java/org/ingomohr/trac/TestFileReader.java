package org.ingomohr.trac;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * TestFileReader
 */
public class TestFileReader {

    private FileReader objUT;

    @BeforeEach
    void prep() {
        objUT = new FileReader();
    }

    @Test
    void readAllLines() throws IOException {
        Path path = Paths.get("src/test/resources/org/ingomohr/trac/trac.txt");
        List<String> lines = objUT.readAllLines(path);

        assertEquals(5, lines.size());

    }
}