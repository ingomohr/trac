package org.ingomohr.trac;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.ingomohr.trac.in.TracMultiProtocolParser;
import org.ingomohr.trac.model.TracItem;
import org.ingomohr.trac.model.TracProtocol;
import org.ingomohr.trac.util.FileReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestTracMultiProtocolParser {

    private TracMultiProtocolParser objUT;

    private String document;

    @BeforeEach
    void prep() throws Exception {
        objUT = new TracMultiProtocolParser();
        document = readText();
    }

    @Test
    void test() {
        final List<TracProtocol> protocols = objUT.parse(document);

        assertEquals(2, protocols.size());

        TracProtocol protocol1 = protocols.get(0);
        assertEquals("Mo", protocol1.getTitle());
        assertIsItem("08:27 Topic A", "08:27", "08:44", 17, protocol1.getItems().get(0));

        TracProtocol protocol2 = protocols.get(1);
        assertEquals("Di", protocol2.getTitle());
        assertIsItem("08:00 Topic A", "08:00", "08:30", 30, protocol2.getItems().get(0));
        assertIsItem("08:30-10:30 Topic B", "08:30", "10:30", 120, protocol2.getItems().get(1));
        assertEquals(2, protocol2.getItems().size());

    }

    private void assertIsItem(String raw, String start, String end, int timeSpentInMin, TracItem actualItem) {
        assertEquals(raw, actualItem.getRawText());
        assertEquals(start, actualItem.getStartTime());
        assertEquals(end, actualItem.getEndTime());
        assertEquals(timeSpentInMin, actualItem.getTimeSpentInMinutes());
    }

    private String readText() throws Exception {
        Path path = Paths.get("src/test/resources/org/ingomohr/trac/trac-multi.txt");
        List<String> lines = new FileReader().readAllLines(path);
        String allLines = String.join(System.lineSeparator(), lines);
        return allLines;
    }

}
