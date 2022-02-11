package org.ingomohr.trac.in;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.hamcrest.CoreMatchers;
import org.ingomohr.trac.model.TracItem;
import org.ingomohr.trac.model.TracProtocol;
import org.ingomohr.trac.testutil.TracItemMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
    void read_TwoItemsWithBothTimes_HasStartAndEndTimesSet() throws Exception {
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

        TracItem item0 = protocol.items().get(0);
        TracItem item1 = protocol.items().get(1);

        assertThat(item0, TracItemMatchers.isItem("09:00", "10:05", "One"));
        assertThat(item1, TracItemMatchers.isItem("10:05", "11:00", "Two"));
    }

    @Test
    void read_FirstItemHasNoEndTime_StartTimeOfSuccessorFollowUpHasBeenAssignedAsEndTime() throws Exception {
        var doc = """
                # My Protocol
                09:00 One
                10:05-17:00 Two
                """;

        List<TracProtocol> ps = objUT.read(doc);

        TracProtocol protocol = ps.get(0);

        assertEquals("# My Protocol", protocol.title());
        assertEquals(2, protocol.items().size());

        TracItem item0 = protocol.items().get(0);
        TracItem item1 = protocol.items().get(1);

        assertThat(item0, TracItemMatchers.isItem("09:00", "10:05", "One"));
        assertThat(item1, TracItemMatchers.isItem("10:05", "17:00", "Two"));
    }

    @Test
    void read_ProtocolHasCommentLines_CommentIsIgnored() throws Exception {
        var doc = """
                # My Protocol with comments
                09:00 One
                # some comment
                10:05 Two
                11:00-11:30 Three
                # 12:00 this is a comment, too
                """;

        List<TracProtocol> ps = objUT.read(doc);
        assertEquals(1, ps.size());

        TracProtocol protocol = ps.get(0);
        assertEquals("# My Protocol with comments", protocol.title());
        assertEquals(3, protocol.items().size());

        TracItem item0 = protocol.items().get(0);
        TracItem item1 = protocol.items().get(1);
        TracItem item2 = protocol.items().get(2);

        assertThat(item0, TracItemMatchers.isItem("09:00", "10:05", "One"));
        assertThat(item1, TracItemMatchers.isItem("10:05", "11:00", "Two"));
        assertThat(item2, TracItemMatchers.isItem("11:00", "11:30", "Three"));
    }

    @Test
    void read_NoFirstComment_FirstLineWasTakenAsTitle() throws Exception {
        var doc = """
                09:00 One
                """;

        List<TracProtocol> ps = objUT.read(doc);
        assertEquals(1, ps.size());

        TracProtocol protocol = ps.get(0);
        assertEquals("09:00 One", protocol.title());
        assertEquals(1, protocol.items().size());

        TracItem item0 = protocol.items().get(0);
        assertThat(item0, TracItemMatchers.isItem("09:00", null, "One"));
    }

    @Test
    void read_HasOnlyComments_ProtocolHasNoItems() throws Exception {
        var doc = """
                # Hello World
                # The line below is also a comment
                -------
                # Another comment
                """;

        List<TracProtocol> ps = objUT.read(doc);

        TracProtocol protocol = ps.get(0);
        assertEquals("# Hello World", protocol.title());
        assertEquals(0, protocol.items().size());
    }

    @Test
    void read_StartsAndEndsWithEmptyLine_EmptyLeadingAndTrailingLinesAreIgnored() throws Exception {
        var doc = """

                # One
                ---
                08:00 Dev: Component A: So stuff
                08:30 Orga: X: D
                08:45-09:00 Review: R

                # Two
                ---
                07:32-08:00 Orga: D

                """;

        List<TracProtocol> ps = objUT.read(doc);
        assertEquals(2, ps.size());

        TracProtocol p0 = ps.get(0);
        assertEquals("# One", p0.title());
        assertEquals(3, p0.items().size());
        assertThat(p0.items().get(0), TracItemMatchers.isItem("08:00", "08:30", "Dev: Component A: So stuff"));
        assertThat(p0.items().get(1), TracItemMatchers.isItem("08:30", "08:45", "Orga: X: D"));
        assertThat(p0.items().get(2), TracItemMatchers.isItem("08:45", "09:00", "Review: R"));

        TracProtocol p1 = ps.get(1);
        assertEquals("# Two", p1.title());
        assertEquals(1, p1.items().size());
        assertThat(p1.items().get(0), TracItemMatchers.isItem("07:32", "08:00", "Orga: D"));
    }

    @Test
    void read_HasMultipleProtocols_AllProtocolsAreRead() throws Exception {
        var doc = """
                # One
                08:00 Dev: Component A: So stuff
                08:30 Orga: X: D
                08:45-09:00 Review: R

                # Two
                07:32-08:00 Orga: D
                """;

        List<TracProtocol> ps = objUT.read(doc);
        assertEquals(2, ps.size());

        TracProtocol p0 = ps.get(0);
        assertEquals("# One", p0.title());
        assertEquals(3, p0.items().size());
        assertThat(p0.items().get(0), TracItemMatchers.isItem("08:00", "08:30", "Dev: Component A: So stuff"));
        assertThat(p0.items().get(1), TracItemMatchers.isItem("08:30", "08:45", "Orga: X: D"));
        assertThat(p0.items().get(2), TracItemMatchers.isItem("08:45", "09:00", "Review: R"));

        TracProtocol p1 = ps.get(1);
        assertEquals("# Two", p1.title());
        assertEquals(1, p1.items().size());
        assertThat(p1.items().get(0), TracItemMatchers.isItem("07:32", "08:00", "Orga: D"));
    }

    @Test
    void read_EntriesHaveLeadingAndTrailingWhiteSpaces_LeadingAndTrailingSpacesAreIgnored() throws Exception {
        var doc = """
                  # One
                08:00 Dev: Component A: So stuff
                   08:30 Orga: X: D
                  08:45-09:00 Review: R
                """;

        List<TracProtocol> ps = objUT.read(doc);
        assertEquals(1, ps.size());

        TracProtocol p0 = ps.get(0);
        assertEquals("# One", p0.title());
        assertEquals(3, p0.items().size());
        assertThat(p0.items().get(0), TracItemMatchers.isItem("08:00", "08:30", "Dev: Component A: So stuff"));
        assertThat(p0.items().get(1), TracItemMatchers.isItem("08:30", "08:45", "Orga: X: D"));
        assertThat(p0.items().get(2), TracItemMatchers.isItem("08:45", "09:00", "Review: R"));
    }

    @Test
    void read_LastEntryHasNoEndTime_ShitInShitOutReadAsIs() throws Exception {
        var doc = """
                # Protocol X
                08:00 One
                09:00 Two
                """;

        List<TracProtocol> ps = objUT.read(doc);
        assertEquals(1, ps.size());

        TracProtocol p0 = ps.get(0);
        assertEquals("# Protocol X", p0.title());
        assertEquals(2, p0.items().size());
        assertThat(p0.items().get(0), TracItemMatchers.isItem("08:00", "09:00", "One"));
        assertThat(p0.items().get(1), TracItemMatchers.isItem("09:00", null, "Two"));
    }

    @Test
    void read_EntryHasNegativeDuration_ShitInShitOutReadAsIs() throws Exception {
        var doc = """
                # Protocol X
                08:00 One
                07:00 Two
                """;

        List<TracProtocol> ps = objUT.read(doc);
        assertEquals(1, ps.size());

        TracProtocol p0 = ps.get(0);
        assertEquals("# Protocol X", p0.title());
        assertEquals(2, p0.items().size());
        assertThat(p0.items().get(0), TracItemMatchers.isItem("08:00", "07:00", "One"));
        assertThat(p0.items().get(1), TracItemMatchers.isItem("07:00", null, "Two"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "A", "07.00 Two", "07.0a Two" })
    void read_EntryHasBadFormat_ReaderThrowsExceptionProvidingInfoOnLine(String line) throws Exception {
        String rawDoc = """
                # Protocol X
                $(s)
                # End
                """;
        String doc = rawDoc.replace("$(s)", line);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> objUT.read(doc));
        assertThat(ex.getMessage(),
                CoreMatchers.containsString("Unsupported format: Cannot read line: '" + line + "'"));
    }



}