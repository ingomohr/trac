package org.ingomohr.trac.in.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Objects;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
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
        assertThat(protocol.getItems().get(1), isWorkItem("10:05", "11:00", "Two"));
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
        assertThat(protocol.getItems().get(1), isWorkItem("10:05", "11:00", "Two"));
    }

    @Test
    void read_NonAdjacentWorklogItemsShortNotation_HasStartAndEndTimesSet() throws Exception {
        var doc = """
                09:00 One
                # some comment
                10:05 Two
                11:00-11:30 Three
                """;

        List<ITracProtocol> ps = objUT.read(doc);
        assertEquals(1, ps.size());

        ITracProtocol protocol = ps.get(0);

        assertThat(protocol.getItems().get(0), isWorkItem("09:00", "10:05", "One"));
        assertThat(protocol.getItems().get(1), Matchers.isA(ITracItem.class));
        assertThat(protocol.getItems().get(2), isWorkItem("10:05", "11:00", "Two"));
        assertThat(protocol.getItems().get(3), isWorkItem("11:00", "11:30", "Three"));
    }

    @Test
    void read_NoFirstComment_ProtocolHasNoTitle() throws Exception {
        var doc = """
                09:00 One
                """;

        List<ITracProtocol> ps = objUT.read(doc);

        assertNull(ps.get(0).getTitle());
    }

    @Test
    void read_HasFirstNonWorklogLine_ProtocolHasFirstLineAsTitle() throws Exception {
        var doc = """
                Hello World
                # This is a text
                """;

        List<ITracProtocol> ps = objUT.read(doc);

        assertEquals("Hello World", (ps.get(0).getTitle()));
    }

    @Test
    void read_StartsAndEndsWithEmptyLine_EmptyLeadingAndTrailingLinesAreIgnored() throws Exception {
        var doc = """

                One
                ---
                08:00 Dev: Component A: So stuff
                08:30 Orga: X: D
                08:45-09:00 Review: R

                Two
                ---
                07:32-08:00 Orga: D

                """;

        List<ITracProtocol> ps = objUT.read(doc);
        assertEquals(2, ps.size());

        ITracProtocol p0 = ps.get(0);
        assertEquals("One", (p0.getTitle()));
        assertEquals(5, p0.getItems().size());

        ITracProtocol p1 = ps.get(1);
        assertEquals("Two", (p1.getTitle()));
        assertEquals(3, p1.getItems().size());
    }

    @Test
    void read_HasDuplicateProtocols_AllProtocolsAreRead() throws Exception {
        var doc = """
                # One
                08:00 Dev: Component A: So stuff
                08:30 Orga: X: D
                08:45-09:00 Review: R

                # Two
                07:32-08:00 Orga: D
                """;

        List<ITracProtocol> ps = objUT.read(doc);
        assertEquals(2, ps.size());

        ITracProtocol p0 = ps.get(0);
        assertEquals("# One", (p0.getTitle()));
        assertEquals(4, p0.getItems().size());
        assertThat(p0.getItems().get(0).getText(), CoreMatchers.is("# One"));
        assertThat(p0.getItems().get(1), isWorkItem("08:00", "08:30", "Dev: Component A: So stuff"));
        assertThat(p0.getItems().get(2), isWorkItem("08:30", "08:45", "Orga: X: D"));
        assertThat(p0.getItems().get(3), isWorkItem("08:45", "09:00", "Review: R"));

        ITracProtocol p1 = ps.get(1);
        assertEquals("# Two", (p1.getTitle()));
        assertEquals(2, p1.getItems().size());
        assertThat(p1.getItems().get(0).getText(), CoreMatchers.is("# Two"));
        assertThat(p1.getItems().get(1), isWorkItem("07:32", "08:00", "Orga: D"));
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