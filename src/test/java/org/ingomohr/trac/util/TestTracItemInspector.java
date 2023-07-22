package org.ingomohr.trac.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.temporal.TemporalAccessor;

import org.ingomohr.trac.model.TracItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestTracItemInspector {

	private TracItemInspector objUT;

	@BeforeEach
	void prep() {
		objUT = new TracItemInspector();
	}

	@Test
	void getStartTimeAsString_ForItemWithoutStartTime_ReturnsNull() {
		assertNull(objUT.getStartTimeAsString(mkItem(null, "13:10")));
	}

	@Test
	void getStartTimeAsString_ForItemWithStartTime_ReturnsStartTimeAsString() {
		assertEquals("05:23", objUT.getStartTimeAsString(mkItem("05:23", "13:10")));
	}

	@Test
	void getEndTimeAsString_ForItemWithEndTime_ReturnsEndTimeAsString() {
		assertEquals("13:10", objUT.getEndTimeAsString(mkItem("05:23", "13:10")));
	}

	@Test
	void getEndTimeAsString_ForItemWithoutEndTime_ReturnsNull() {
		assertNull(objUT.getEndTimeAsString(mkItem("13:10", null)));
	}

	@Test
	void getDuration() {
		assertAll(() -> {

			assertEquals(null, objUT.getDuration(mkItem(null, null)));
			assertEquals(null, objUT.getDuration(mkItem(null, "05:00")));
			assertEquals(null, objUT.getDuration(mkItem("04:00", null)));
			assertEquals(84, objUT.getDuration(mkItem("04:00", "05:24")).toMinutes());
		});

	}

	private TracItem mkItem(String startTime, String endTime) {
		TracItem item = mock(TracItem.class);
		when(item.startTime()).thenReturn(mkTime(startTime));
		when(item.endTime()).thenReturn(mkTime(endTime));
		return item;
	}

	private TemporalAccessor mkTime(String HHmm) {
		if (HHmm != null) {
			return new TimeConverter().toTime(HHmm);
		}
		return null;
	}

}
