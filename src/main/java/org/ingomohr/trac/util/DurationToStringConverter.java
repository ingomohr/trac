package org.ingomohr.trac.util;

import java.time.Duration;
import java.util.Objects;

/**
 * Converts {@link java.time.Duration} to a string representation.
 */
public class DurationToStringConverter {

	/**
	 * Returns a string representation for the given Duration.
	 * 
	 * @param duration the duration. Cannot be <code>null</code>.
	 * @return string representation with info on days, hours and minutes. Never
	 *         <code>null</code>.
	 */
	public String toString(Duration duration) {
		Objects.requireNonNull(duration);

		long minutes = duration.toMinutes();

		long hours = minutes / 60;
		minutes = minutes % 60;

		long days = hours / 24;
		hours = hours % 24;

		StringBuilder builder = new StringBuilder();
		if (days > 0) {
			builder.append(String.format("%,d", days)).append("d ");
		}
		if (hours > 0 || days > 0) {
			if (hours < 10) {
				builder.append(" ");
			}
			builder.append(hours).append("h ");
		}
		if (minutes < 10) {
			builder.append(" ");
		}
		builder.append(minutes).append("m");

		return builder.toString();
	}

}
