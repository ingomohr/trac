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

import org.ingomohr.trac.in.TracProtocolParser;
import org.ingomohr.trac.model.TracProtocol;
import org.ingomohr.trac.util.FileReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestTracProtocolWriterByTopic {

    private TracProtocolWriterByTopic objUT;

    @BeforeEach
    void prep() {
        objUT = new TracProtocolWriterByTopic();
    }

    @Test
    void multiProtocol() throws Exception {

        String lines = String.join(System.lineSeparator(), Arrays.asList(

        // @formatter:off

               "Fr",
               "---",
               "08:00 Topic A",
               "08:30 Topic B",
               "09:25-10:00 Topic C: Topic C2",
               "",
               "",
               "Sa",
               "---",
               "08:27 Topic A",
               "08:44-08:57 Topic B",
               "09:25-10:10 Topic C: Topic C1",
               "11:00-12:00 Break"
                                
                                
        // @formatter:on

        ));

        String expected = String.join(System.lineSeparator(), Arrays.asList(

        // @formatter:off
                
                "Protocol by Topics",
                "-----------------------",
                "Start : 08:00 : Fr",
                "End   : 12:00 : Sa",
                "-----------------------",
                "Total time      :  4:15",
                "Breaks          :  1:00",
                "Time w/o breaks :  3:15",
                "-----------------------",
                "1:20 ######..............  31% Topic C",
                "1:08 #####...............  27% Topic B",
                "1:00 ####................  24% Break",
                "0:47 ###.................  18% Topic A"
                
                // @formatter:on

        ));

        test(lines, expected);

    }

    @Test
    void singleProtocol() throws Exception {

        String lines = String.join(System.lineSeparator(), Arrays.asList(

        // @formatter:off

				
				"02:24 Dev: Trac",
				"02:44 Break: Pomodoro",
				"02:54 Chore: Mail-Inbox",
				"03:00-19:02 Dev: Trac",
				"20:00-22:00 Rev: X"
				
				
	// @formatter:on

        ));

        String expected = String.join(System.lineSeparator(), Arrays.asList(

        // @formatter:off
                
                "Protocol by Topics",
                "-----------------------",
                "Start : 02:24",
                "End   : 22:00",
                "-----------------------",
                "Total time      : 18:38",
                "Breaks          :  0:10",
                "Time w/o breaks : 18:28",
                "-----------------------",
                "16:22 #################...  88% Dev",
                " 2:00 ##..................  11% Rev",
                " 0:10 ....................   1% Break",
                " 0:06 ....................   1% Chore"
                
                // @formatter:on

        ));

        test(lines, expected);

    }

    private void test(String input, String expected) throws IOException {
        /*
         * TODO drop the use of the parser here (once there is a simple API to create a
         * new model).
         */
        final TracProtocol protocol = new TracProtocolParser().parse(input);

        final String home = System.getProperty("user.home");
        final Path outPath = Paths.get(home, "TestTracProtocolWriterByTopic.txt");
        final OutputStream out = Files.newOutputStream(outPath);

        objUT.write(protocol, new PrintStream(out));

        final String actual = readActualLines(outPath);
        assertEquals(expected, actual);
    }

    private String readActualLines(final Path outPath) throws IOException {
        final List<String> writtenLines = new FileReader().readAllLines(outPath);
        final String actual = String.join(System.lineSeparator(), writtenLines);
        return actual;
    }

}
