package org.ingomohr.trac.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

        var expected = """
                Mo
                ---
                08:27 Topic A
                08:44-08:57 Topic B
                09:25-45 Topic C: Topic C1

                Di
                -----
                08:00 Topic A
                08:30-10:30 Topic B""";

        assertEquals(expected, String.join(System.lineSeparator(), lines));
    }
}