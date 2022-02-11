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
                    So (Feb 6 2021)
                    # some comment
                    # MiniSets: 0
                    # wfh
                    ---
                    01:38-02:15 Some content # + 0.75h Buffer""";

        assertEquals(expected, String.join(System.lineSeparator(), lines));
    }
}