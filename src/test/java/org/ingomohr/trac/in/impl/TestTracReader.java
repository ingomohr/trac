package org.ingomohr.trac.in.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Objects;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Matcher;
import org.ingomohr.trac.in.TracReaderException;
import org.ingomohr.trac.model.ITracItem;
import org.ingomohr.trac.model.ITracProtocol;
import org.ingomohr.trac.model.IWorklogItem;
import org.ingomohr.trac.util.TracWorklogItemInspector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestTracReader {

    private TracReader objUT;

    @BeforeEach
    void prep() {
        objUT = new TracReader();
    }

    @Test
    void read_EmptyDocument_ReturnsEmptyListOfProtocol() throws Exception {
        List<ITracProtocol> protocols = objUT.read("");
        assertEquals(0, protocols.size());
    }

    @Test
    void read_SingleLineOfUnsupportedFormat_ThrowsExceptionForLine0() throws Exception {

        TracReaderException ex = assertThrows(TracReaderException.class, () -> objUT.read("ajkhkjdh 88:44 #"));
        assertThat(ex.getMessage(), CoreMatchers.containsString("Unsupported"));
        assertThat(ex.getMessage(), CoreMatchers.containsString("in line 0"));
    }

    @Test
    void read_ThirdLineIsUnsupported_ThrowsExceptionForLine2() throws Exception {
        var doc = """
                # Doc X
                09:00-10:00 Hello
                This is an unsupported line
                """;

        TracReaderException ex = assertThrows(TracReaderException.class, () -> objUT.read(doc));
        assertThat(ex.getMessage(), CoreMatchers.containsString("Unsupported"));
        assertThat(ex.getMessage(), CoreMatchers.containsString("in line 2"));
    }

    @Test
    void read_AdjacentWorklogItems_HasStartAndEndTimesSet() throws Exception {
        var doc = """
                09:00-10:05 One
                10:05-11:00 Two
                """;

        List<ITracProtocol> ps = objUT.read(doc);
        assertEquals(1, ps.size());

        ITracProtocol protocol = ps.get(0);
        assertEquals(null, protocol.getTitle());
        assertEquals(2, protocol.getItems().size());

        assertThat(protocol.getItems().get(0), isWorkItem("09:00", "10:05", "One"));
    }

    @Test
    void read_AdjacentWorklogItemsShortNotation_HasStartAndEndTimesSet() throws Exception {
        var doc = """
                09:00 One
                10:05-11:00 Two
                """;

        List<ITracProtocol> ps = objUT.read(doc);
        assertEquals(1, ps.size());

        ITracProtocol protocol = ps.get(0);
        assertEquals(null, protocol.getTitle());
        assertEquals(2, protocol.getItems().size());

        assertThat(protocol.getItems().get(0), isWorkItem("09:00", "10:05", "One"));
    }

    private Matcher<ITracItem> isWorkItem(String start, String end, String message) {
        return new DiagnosingMatcher<ITracItem>() {

            @Override
            public void describeTo(Description description) {
                describeItem(start, end, message, description);
            }

            @Override
            protected boolean matches(Object item, Description mismatchDescription) {
                IWorklogItem worklogItem = (IWorklogItem) item;

                TracWorklogItemInspector inspector = new TracWorklogItemInspector();
                String actualStart = inspector.getStartTimeAsString(worklogItem);
                String actualEnd = inspector.getEndTimeAsString(worklogItem);
                String actualMessage = worklogItem.getMessage();

                boolean matchesStart = Objects.equals(start, actualStart);
                boolean matchesEnd = Objects.equals(end, actualEnd);
                boolean matchesMsg = Objects.equals(message, actualMessage);

                if (matchesStart && matchesEnd && matchesMsg) {
                    return true;
                } else {
                    mismatchDescription.appendText("was ");
                    describeItem(actualStart, actualEnd, actualMessage, mismatchDescription);
                    return false;
                }
            }

            private void describeItem(String actualStart, String actualEnd, String actualMessage,
                    Description description) {
                description.appendText(System.lineSeparator());
                description.appendText("start: " + actualStart + System.lineSeparator());
                description.appendText("end: " + actualEnd + System.lineSeparator());
                description.appendText("message: <" + actualMessage + ">");
            }
        };
    }

}