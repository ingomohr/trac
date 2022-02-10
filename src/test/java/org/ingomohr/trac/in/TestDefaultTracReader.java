package org.ingomohr.trac.in;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Objects;

import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Matcher;
import org.ingomohr.trac.model.TracItem;
import org.ingomohr.trac.model.TracProtocol;
import org.ingomohr.trac.util.TracItemInspector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestDefaultTracReader {

    private DefaultTracReader objUT;

    @BeforeEach
    void prep() {
        objUT = new DefaultTracReader();
    }

    @Test
    void read_EmptyDocument_ReturnsEmptyListOfProtocol() throws Exception {
        List<TracProtocol> protocols = objUT.read("");
        assertEquals(0, protocols.size());
    }

    @Test
    void read_AdjacentWorklogItems_HasStartAndEndTimesSet() throws Exception {
        var doc = """
                # My Protocol
                09:00-10:05 One
                10:05-11:00 Two
                """;

        List<TracProtocol> ps = objUT.read(doc);
        assertEquals(1, ps.size());

        TracProtocol protocol = ps.get(0);
        assertEquals("# My Protocol", protocol.title());
        assertEquals(2, protocol.items().size());

        assertThat(protocol.items().get(0), isItem("09:00", "10:05", "One"));
        assertThat(protocol.items().get(1), isItem("10:05", "11:00", "Two"));
    }

//
//    @Test
//    void read_AdjacentWorklogItemsShortNotation_HasStartAndEndTimesSet() throws Exception {
//        var doc = """
//                09:00 One
//                10:05-17:00 Two
//                """;
//
//        List<TracProtocol> ps = objUT.read(doc);
//        assertEquals(1, ps.size());
//
//        TracProtocol protocol = ps.get(0);
//        assertEquals(null, protocol.getTitle());
//        assertEquals(2, protocol.items().size());
//
//        assertThat(protocol.items().get(0), isWorkItem("09:00", "10:05", "One"));
//        assertThat(protocol.items().get(1), isWorkItem("10:05", "17:00", "Two"));
//    }
//
//    @Test
//    void read_NonAdjacentWorklogItemsShortNotation_HasStartAndEndTimesSet() throws Exception {
//        var doc = """
//                09:00 One
//                # some comment
//                10:05 Two
//                11:00-11:30 Three
//                """;
//
//        List<TracProtocol> ps = objUT.read(doc);
//        assertEquals(1, ps.size());
//
//        TracProtocol protocol = ps.get(0);
//
//        assertThat(protocol.items().get(0), isWorkItem("09:00", "10:05", "One"));
//        assertThat(protocol.items().get(1), Matchers.isA(TracItem.class));
//        assertThat(protocol.items().get(2), isWorkItem("10:05", "11:00", "Two"));
//        assertThat(protocol.items().get(3), isWorkItem("11:00", "11:30", "Three"));
//    }
//
//    @Test
//    void read_NoFirstComment_ProtocolHasNoTitle() throws Exception {
//        var doc = """
//                09:00 One
//                """;
//
//        List<TracProtocol> ps = objUT.read(doc);
//
//        assertNull(ps.get(0).getTitle());
//    }
//
//    @Test
//    void read_HasFirstNonWorklogLine_ProtocolHasFirstLineAsTitle() throws Exception {
//        var doc = """
//                Hello World
//                # This is a text
//                """;
//
//        List<TracProtocol> ps = objUT.read(doc);
//
//        assertEquals("Hello World", (ps.get(0).getTitle()));
//    }
//
//    @Test
//    void read_StartsAndEndsWithEmptyLine_EmptyLeadingAndTrailingLinesAreIgnored() throws Exception {
//        var doc = """
//
//                One
//                ---
//                08:00 Dev: Component A: So stuff
//                08:30 Orga: X: D
//                08:45-09:00 Review: R
//
//                Two
//                ---
//                07:32-08:00 Orga: D
//
//                """;
//
//        List<TracProtocol> ps = objUT.read(doc);
//        assertEquals(2, ps.size());
//
//        TracProtocol p0 = ps.get(0);
//        assertEquals("One", (p0.getTitle()));
//        assertEquals(5, p0.items().size());
//
//        TracProtocol p1 = ps.get(1);
//        assertEquals("Two", (p1.getTitle()));
//        assertEquals(3, p1.items().size());
//    }
//
//    @Test
//    void read_HasDuplicateProtocols_AllProtocolsAreRead() throws Exception {
//        var doc = """
//                # One
//                08:00 Dev: Component A: So stuff
//                08:30 Orga: X: D
//                08:45-09:00 Review: R
//
//                # Two
//                07:32-08:00 Orga: D
//                """;
//
//        List<TracProtocol> ps = objUT.read(doc);
//        assertEquals(2, ps.size());
//
//        TracProtocol p0 = ps.get(0);
//        assertEquals("# One", (p0.getTitle()));
//        assertEquals(4, p0.items().size());
//        assertThat(p0.items().get(0).getText(), CoreMatchers.is("# One"));
//        assertThat(p0.items().get(1), isWorkItem("08:00", "08:30", "Dev: Component A: So stuff"));
//        assertThat(p0.items().get(2), isWorkItem("08:30", "08:45", "Orga: X: D"));
//        assertThat(p0.items().get(3), isWorkItem("08:45", "09:00", "Review: R"));
//
//        TracProtocol p1 = ps.get(1);
//        assertEquals("# Two", (p1.getTitle()));
//        assertEquals(2, p1.items().size());
//        assertThat(p1.items().get(0).getText(), CoreMatchers.is("# Two"));
//        assertThat(p1.items().get(1), isWorkItem("07:32", "08:00", "Orga: D"));
//    }
//
    private Matcher<TracItem> isItem(String start, String end, String message) {
        return new DiagnosingMatcher<TracItem>() {

            @Override
            public void describeTo(Description description) {
                describeItem(start, end, message, description);
            }

            @Override
            protected boolean matches(Object obj, Description mismatchDescription) {
                TracItem item = (TracItem) obj;

                TracItemInspector inspector = new TracItemInspector();
                String actualStart = inspector.getStartTimeAsString(item);
                String actualEnd = inspector.getEndTimeAsString(item);
                String actualText = item.text();

                boolean matchesStart = Objects.equals(start, actualStart);
                boolean matchesEnd = Objects.equals(end, actualEnd);
                boolean matchesMsg = Objects.equals(message, actualText);

                if (matchesStart && matchesEnd && matchesMsg) {
                    return true;
                } else {
                    mismatchDescription.appendText("was ");
                    describeItem(actualStart, actualEnd, actualText, mismatchDescription);
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