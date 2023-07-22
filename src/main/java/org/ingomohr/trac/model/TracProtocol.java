package org.ingomohr.trac.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A protocol with a title and a number of items.
 */
public record TracProtocol(

		String title,

		List<TracItem> items) {

	/**
	 * Creates a new protocol without a title.
	 */
	public TracProtocol() {
		this(null);
	}

	/**
	 * Creates a new protocol with the given title.
	 * 
	 * @param title the title to set.
	 */
	public TracProtocol(String title) {
		this(title, new ArrayList<>());
	}

}
