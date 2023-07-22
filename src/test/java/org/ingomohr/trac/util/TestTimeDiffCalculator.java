package org.ingomohr.trac.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestTimeDiffCalculator {

	private TimeDiffCalculator objUT;

	@BeforeEach
	void prep() {
		objUT = new TimeDiffCalculator();
	}

	@Test
	void getDiffInMinutes_SameTime_DiffIsZero() {
		assertEquals(0, objUT.getDiffInMinutes(mkTime("15:22"), mkTime("15:22")));
	}

	@Test
	void getDiffInMinutes_EndTimeIsAfterStartTime_DiffIsPositive() {
		assertEquals(720 + 19, objUT.getDiffInMinutes(mkTime("10:22"), mkTime("22:41")));
	}

	@Test
	void getDiffInMinutes_EndTimeIsBeforeStartTime_DiffIsPositiveCalculatedForNextDay() {
		assertEquals(53, objUT.getDiffInMinutes(mkTime("23:30"), mkTime("00:23")));
	}

	private TemporalAccessor mkTime(String HHmm) {
		DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("HH:mm").toFormatter();
		return formatter.parse(HHmm);
	}

}
