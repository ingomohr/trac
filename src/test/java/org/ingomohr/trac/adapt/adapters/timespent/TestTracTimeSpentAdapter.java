package org.ingomohr.trac.adapt.adapters.timespent;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.ingomohr.trac.in.DefaultTracReader;
import org.ingomohr.trac.model.TracProtocol;
import org.ingomohr.trac.util.TimeConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TracTimeSpentAdapter}.
 */
public class TestTracTimeSpentAdapter {

    private TracTimeSpentAdapter objUT;

    @BeforeEach
    void prep() {
        objUT = new TracTimeSpentAdapter();
    }

    @Test
    void adapt_NoProtocolsGiven_ReturnsEmptyModel() {
        TracTimeSpentModel model = objUT.adapt(Collections.emptyList());
        verifyModelIsEmpty(model);
    }

    @Test
    void adapt_SingleProtocolWithoutItems_ReturnsModelWithOneEntryThatHasNoValues() {
        TracProtocol p0 = new TracProtocol();
        TracTimeSpentModel model = objUT.adapt(Arrays.asList(p0));

        assertEquals(1, model.entries().size());
        TracTimeSpentModelEntry e0 = model.entries().get(0);
        assertEquals(null, e0.endTime());
        assertEquals(null, e0.startTime());
        assertEquals(null, e0.timeSpent());
    }

    @Test
    void adapt_SingleProtocolWithStartTimeButNoEndTime_ThrowsExceptionProvidingInfoThatDataIsInsufficient() {

        String rawP0 = """
                One
                11:00 Something
                12:00 Something else
                """;

        TracProtocol p0 = readSingleProtocol(rawP0);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            objUT.adapt(Arrays.asList(p0));
        });
        assertThat(ex.getMessage(), CoreMatchers.containsString(
                "Cannot compute duration. Item must have both start- and end time: TracItem[startTime={},ISO resolved to 12:00, endTime=null, text=Something else]"));
    }

    @Test
    void adapt_SingleProtocolWithinSameDay_ReturnsOneTryWithCorrectInformation() {
        String rawP0 = """
                One
                11:00 Something
                12:00-12:30 Something else
                """;

        TracProtocol p0 = readSingleProtocol(rawP0);
        TracTimeSpentModel model = objUT.adapt(Arrays.asList(p0));

        assertEquals(1, model.entries().size());
        TracTimeSpentModelEntry e0 = model.entries().get(0);
        assertEquals("11:00", toHHmm(e0.startTime()));
        assertEquals("12:30", toHHmm(e0.endTime()));

        assertEquals(90, e0.timeSpent().toMinutes());
    }

    @Test
    void adapt_SingleProtocolWithinSameDayAndSmallerMinutes_ReturnsOneTryWithCorrectInformation() {
        String rawP0 = """
                One
                11:00 Something
                12:00-13:15 Something else
                """;

        TracProtocol p0 = readSingleProtocol(rawP0);
        TracTimeSpentModel model = objUT.adapt(Arrays.asList(p0));

        assertEquals(1, model.entries().size());
        TracTimeSpentModelEntry e0 = model.entries().get(0);
        assertEquals("11:00", toHHmm(e0.startTime()));
        assertEquals("13:15", toHHmm(e0.endTime()));

        assertEquals(135, e0.timeSpent().toMinutes());
    }

    @Test
    void adapt_SingleProtocolWithinSameDayAndGaps_ReturnsOneTryWithCorrectInformation() {
        String rawP0 = """
                One
                11:00-11:15 Something
                12:30-13:15 Something else
                """;

        TracProtocol p0 = readSingleProtocol(rawP0);
        TracTimeSpentModel model = objUT.adapt(Arrays.asList(p0));

        assertEquals(1, model.entries().size());
        TracTimeSpentModelEntry e0 = model.entries().get(0);
        assertEquals("11:00", toHHmm(e0.startTime()));
        assertEquals("13:15", toHHmm(e0.endTime()));

        assertEquals(60, e0.timeSpent().toMinutes());
    }

    @Test
    void adapt_SingleProtocolWithDaySwitchAndGaps_ReturnsOneTryWithCorrectInformation() {
        String rawP0 = """
                One
                23:00-00:15 Something
                00:30-01:05 Something else
                """;

        TracProtocol p0 = readSingleProtocol(rawP0);
        TracTimeSpentModel model = objUT.adapt(Arrays.asList(p0));

        assertEquals(1, model.entries().size());
        TracTimeSpentModelEntry e0 = model.entries().get(0);
        assertEquals("23:00", toHHmm(e0.startTime()));
        assertEquals("01:05", toHHmm(e0.endTime()));

        assertEquals(110, e0.timeSpent().toMinutes());
    }

    @Test
    void adapt_SingleProtocolWithBreaks_BreakIsIgnored() {
        String rawP0 = """
                One
                23:00-00:15 Something
                00:30-01:05 bReaK # this line is to be ignored
                01:05-01:35 Something else
                """;

        TracProtocol p0 = readSingleProtocol(rawP0);
        TracTimeSpentModel model = objUT.adapt(Arrays.asList(p0));

        assertEquals(1, model.entries().size());
        TracTimeSpentModelEntry e0 = model.entries().get(0);
        assertEquals("23:00", toHHmm(e0.startTime()));
        assertEquals("01:35", toHHmm(e0.endTime()));

        assertEquals(105, e0.timeSpent().toMinutes());
    }

    @Test
    void adapt_SingleProtocol_ReturnedEntryHoldProtocol() {
        String rawP0 = """
                One
                23:00-00:15 Something
                """;

        TracProtocol p0 = readSingleProtocol(rawP0);
        TracTimeSpentModel model = objUT.adapt(Arrays.asList(p0));
        assertSame(p0, model.entries().get(0).protocol());
    }

    @Test
    void adapt_MultipleProtocols_ReturnsModelWithAllEntries() {
        String doc = """
                One
                10:30-11:15 Something
                11:15-12:00 bReaK # this line is to be ignored
                12:00-13:00 Something else

                Two
                10:45-11:15 Something
                """;
        List<TracProtocol> ps = new DefaultTracReader().read(doc);

        TracTimeSpentModel model = objUT.adapt(ps);

        assertEquals(2, model.entries().size());
        TracTimeSpentModelEntry e0 = model.entries().get(0);
        assertSame(ps.get(0), e0.protocol());
        assertEquals("10:30", toHHmm(e0.startTime()));
        assertEquals("13:00", toHHmm(e0.endTime()));
        assertEquals(105, e0.timeSpent().toMinutes());

        TracTimeSpentModelEntry e1 = model.entries().get(1);
        assertSame(ps.get(1), e1.protocol());
        assertEquals("10:45", toHHmm(e1.startTime()));
        assertEquals("11:15", toHHmm(e1.endTime()));
        assertEquals(30, e1.timeSpent().toMinutes());
    }

    private String toHHmm(TemporalAccessor time) {
        return new TimeConverter().toHHmm(time);
    }

    private TracProtocol readSingleProtocol(String doc) {
        return new DefaultTracReader().read(doc).get(0);
    }

    private void verifyModelIsEmpty(TracTimeSpentModel model) {
        assertEquals(0, model.entries().size());
    }

}
