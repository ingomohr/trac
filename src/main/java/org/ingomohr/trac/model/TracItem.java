package org.ingomohr.trac.model;

import java.time.temporal.TemporalAccessor;

/**
 * An immutable item in a protocol. An item can have a start time, end time and
 * a text.
 * <p>
 * Items are immutable.
 * </p>
 */
public record TracItem(

		TemporalAccessor startTime,

		TemporalAccessor endTime,

		String text

) {

}
