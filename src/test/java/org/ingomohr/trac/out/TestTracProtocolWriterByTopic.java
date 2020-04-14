package org.ingomohr.trac.out;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.ingomohr.trac.FileReader;
import org.ingomohr.trac.TracProtocolParser;
import org.ingomohr.trac.model.TracProtocol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestTracProtocolWriterByTopic {

    private TracProtocolWriterByTopic objUT;

    @BeforeEach
    void prep() {
	objUT = new TracProtocolWriterByTopic();
    }

    @Test
    void test() throws Exception {

	String lines = String.join(System.lineSeparator(), Arrays.asList(

	// @formatter:off

				
				"08:24 Dev: Trac",
				"08:44 Break: Pomodoro",
				"08:54 Chore: Mail-Inbox",
				"09:00-11:02 Dev: Trac"
				
	// @formatter:on

	));

	/*
	 * TODO drop the use of the parser here (once there is a simple API to create a
	 * new model).
	 */
	final TracProtocol protocol = new TracProtocolParser().parse(lines);

	final String home = System.getProperty("user.home");
	final Path outPath = Paths.get(home, "TestTracProtocolWriterByTopic.txt");
	final OutputStream out = Files.newOutputStream(outPath);

	objUT.write(protocol, new PrintStream(out));

	String expected = String.join(System.lineSeparator(), Arrays.asList(

	// @formatter:off
				
				"Protocol by Topics",
				"------------------",
				"Total time spent: 2:38",
				"",
				"2:22 ##################..  90% Dev",
				"0:10 #...................   6% Break",
				"0:06 ....................   4% Chore"
				
	// @formatter:on

	));

	final String actual = readActualLines(outPath);
	assertEquals(expected, actual);

    }

    private String readActualLines(final Path outPath) throws IOException {
	final List<String> writtenLines = new FileReader().readAllLines(outPath);
	final String actual = String.join(System.lineSeparator(), writtenLines);
	return actual;
    }

}
