package org.ingomohr.trac.adapt.adapters.timespent;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Collections;

import org.ingomohr.trac.model.TracProtocol;
import org.ingomohr.trac.util.TimeConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TracTimeSpentModelToStringAdapter}.
 */
public class TestTracTimeSpentModelToStringAdapter {

	private TracTimeSpentModelToStringAdapter objUT;

	@BeforeEach
	void prep() {
		objUT = new TracTimeSpentModelToStringAdapter();
	}

	@Test
	void adapt_ModelIsEmpty_ReturnsInfoThatNoInformationWasFound() {

		String expected = """
				# Time spent
				no information found
				""";

		String actual = objUT.adapt(new TracTimeSpentModel(Collections.emptyList()));
		assertEquals(expected, actual);
	}

	@Test
	void adapt_ModelHasProtocol_ReturnsInfoForProtocol() {
		String expected = """
				# Time spent
				------------------------------------------
				  1. MyTitle 10:00 - 12:00 -- EWT:  1h  0m

				EWT: Effective working time (i.e. w/o breaks)
				""";

		TracProtocol prot = new TracProtocol("MyTitle");
		TracTimeSpentModelEntry entry = new TracTimeSpentModelEntry(prot, mkTime("10:00"), mkTime("12:00"),
				mkDuration(60));

		TracTimeSpentModel model = new TracTimeSpentModel(Arrays.asList(entry));

		String actual = objUT.adapt(model);

		assertEquals(expected, actual);
	}

	@Test
	void adapt_ModelMultipleProtocolsTheSecondOfWhichHasNoValues_ReturnsInfosForAllProtocolsAndDisplaysNoInfoStringsForSecondProtocol() {
		String expected = """
				# Time spent
				---------------------------------------------------------------------
				  1. MyTitle    10:00           - 12:00         -- EWT:        1h  0m
				  2. <no title> <no start time> - <no end time> -- EWT: <no duration>

				EWT: Effective working time (i.e. w/o breaks)
				""";

		TracProtocol prot0 = new TracProtocol("MyTitle");
		TracTimeSpentModelEntry entry0 = new TracTimeSpentModelEntry(prot0, mkTime("10:00"), mkTime("12:00"),
				mkDuration(60));

		TracProtocol prot1 = new TracProtocol(null);
		TracTimeSpentModelEntry entry1 = new TracTimeSpentModelEntry(prot1, null, null, null);

		TracTimeSpentModel model = new TracTimeSpentModel(Arrays.asList(entry0, entry1));

		String actual = objUT.adapt(model);
		assertEquals(expected, actual);
	}

	private TemporalAccessor mkTime(String HHmm) {
		if (HHmm != null) {
			return new TimeConverter().toTime(HHmm);
		}
		return null;
	}

	private Duration mkDuration(int minutes) {
		return Duration.ofMinutes(minutes);
	}

}
