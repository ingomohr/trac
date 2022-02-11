package org.ingomohr.trac.in;

import java.nio.file.Path;
import java.util.List;

import org.ingomohr.trac.model.TracProtocol;
import org.ingomohr.trac.util.FileReader;
import org.junit.jupiter.api.Test;

/**
 * Tests reading from a file.
 */
public class TestDefaultTracReaderFromFile {

    @Test
    void readFromFile() throws Exception {
        Path path = Path.of("src/test/resources/org/ingomohr/trac/trac.txt");
        List<String> lines = new FileReader().readAllLines(path);
        String doc = String.join(System.lineSeparator(), lines);

        ITracReader reader = new DefaultTracReader();
        List<TracProtocol> ps = reader.read(doc);




    }

}