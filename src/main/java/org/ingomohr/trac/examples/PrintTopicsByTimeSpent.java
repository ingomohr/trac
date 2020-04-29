package org.ingomohr.trac.examples;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.ingomohr.trac.TracProtocolParser;
import org.ingomohr.trac.model.TracProtocol;
import org.ingomohr.trac.out.TracProtocolWriterByTopic;
import org.ingomohr.trac.util.FileReader;

public class PrintTopicsByTimeSpent {

	public static void main(String[] args) {

		final String path = "/Users/ingomohr/Desktop/trac.txt";
		List<String> lines;
		try {
			lines = new FileReader().readAllLines(Paths.get(path));
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
