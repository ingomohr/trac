package org.ingomohr.trac.testutil;

import java.util.Objects;

import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Matcher;
import org.ingomohr.trac.model.TracItem;
import org.ingomohr.trac.util.TracItemInspector;

/**
 * Provides Hamcrest matchers for {@link TracItem}s.
 */
public final class TracItemMatchers {

    private TracItemMatchers() {
    }

    /**
     * Returns a matcher that matches whe a given {@link TracItem} has the given
     * start time, end time and text.
     * 
     * @param start the expected start time. Might be <code>null</code>.
     * @param end   the expected end time. Might be <code>null</code>.
     * @param text  the expected text. Might be <code>null</code>.
     * @return matcher to match {@link TracItem}s. Never <code>null</code>.
     */
    public static Matcher<TracItem> isItem(String start, String end, String message) {
        return new DiagnosingMatcher<TracItem>() {

            @Override
            public void describeTo(Description description) {
                describeItem(start, end, message, description);
            }

            @Override
            protected boolean matches(Object obj, Description mismatchDescription) {
                TracItem item = (TracItem) obj;

                TracItemInspector inspector = new TracItemInspector();
                String actualStart = inspector.getStartTimeAsString(item);
                String actualEnd = inspector.getEndTimeAsString(item);
                String actualText = item.text();

                boolean matchesStart = Objects.equals(start, actualStart);
                boolean matchesEnd = Objects.equals(end, actualEnd);
                boolean matchesText = Objects.equals(message, actualText);

                if (matchesStart && matchesEnd && matchesText) {
                    return true;
                } else {
                    mismatchDescription.appendText("was ");
                    describeItem(actualStart, actualEnd, actualText, mismatchDescription);
                    return false;
                }
            }

            private void describeItem(String actualStart, String actualEnd, String actualMessage,
                    Description description) {
                description.appendText(System.lineSeparator());
                description.appendText("start: " + actualStart + System.lineSeparator());
                description.appendText("end: " + actualEnd + System.lineSeparator());
                description.appendText("text: <" + actualMessage + ">");
            }
        };
    }

}
