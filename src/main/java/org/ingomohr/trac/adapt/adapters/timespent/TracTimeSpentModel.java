package org.ingomohr.trac.adapt.adapters.timespent;

import java.util.List;

/**
 * Model to contain one {@link TracTimeSpentModelEntry} for each
 * {@link org.ingomohr.trac.model.TracProtocol}.
 */
public record TracTimeSpentModel(

		List<TracTimeSpentModelEntry> entries) {

}
