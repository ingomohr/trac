package org.ingomohr.trac.examples;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.ingomohr.trac.TracProtocolParser;
import org.ingomohr.trac.model.TracProtocol;
import org.ingomohr.trac.out.TracProtocolWriterByTopic;
import org.ingomohr.trac.util.FileReader;

/**
 * This is an example on how to use Trac to give you an overview of how much
 * time you spent for what topic.
 */
public class PrintTopicsByTimeSpent {

    public static void main(String[] args) {

        try {
            Path path = Paths.get("src/test/java/org/ingomohr/trac/examples/trac.txt");
            List<String> lines = new FileReader().readAllLines(path);
            String allLines = String.join(System.lineSeparator(), lines);

            TracProtocolParser parser = new TracProtocolParser();
            TracProtocol protocol = parser.parse(allLines);

            TracProtocolWriterByTopic writer = new TracProtocolWriterByTopic();
            writer.write(protocol, System.out);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
