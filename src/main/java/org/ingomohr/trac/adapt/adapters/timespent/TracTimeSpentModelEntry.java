package org.ingomohr.trac.adapt.adapters.timespent;

import java.time.Duration;
import java.time.temporal.TemporalAccessor;

import org.ingomohr.trac.model.TracProtocol;

/**
 * Model to contain the information on a certain {@link TracProtocol} (and the
 * protocol reference itself),
 */
public record TracTimeSpentModelEntry(

		TracProtocol protocol,

		TemporalAccessor startTime,

		TemporalAccessor endTime,

		Duration timeSpent) {

}
