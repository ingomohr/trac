package org.ingomohr.trac.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.temporal.TemporalAccessor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestDurationCalculator {

	private DurationCalculator objUT;

	@BeforeEach
	void prep() {
		objUT = new DurationCalculator();
	}

	@Test
	void zero2Zero_ReturnsZero() {
		test("00:00", "00:00", 0);
	}

	@Test
	void positiveDiffSameHour_ReturnsPositiveDiff() {
		test("07:03", "07:59", 56 * 60);
	}

	@Test
	void positiveDiffWithOverflow_ReturnsPositivDiff() {
		test("07:03", "08:01", 58 * 60);
	}

	@Test
	void negativeDiff_ReturnsPositiveDayWithDayOverflow() {
		test("22:03", "00:02", (2 * 60 * 60) - 60);
	}

	private void test(String startHHmm, String endHHmm, long expectedDurationInSeconds) {
		TemporalAccessor start = mk(startHHmm);
		TemporalAccessor end = mk(endHHmm);

		Duration actual = objUT.calculateDuration(start, end);
		assertEquals(expectedDurationInSeconds, actual.getSeconds());
	}

	private TemporalAccessor mk(String HHmm) {
		return new TimeConverter().toTime(HHmm);
	}

}
