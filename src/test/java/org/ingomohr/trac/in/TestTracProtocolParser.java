package org.ingomohr.trac.in;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.hamcrest.CustomMatcher;
import org.hamcrest.Matcher;
import org.ingomohr.trac.model.TracItem;
import org.ingomohr.trac.model.TracProtocol;
import org.ingomohr.trac.model.TracTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestTracProtocolParser {

    private TracProtocolParser objUT;
    private TracProtocol protocol;

    @BeforeEach
    void prep() {
        objUT = null;
    }

    @Test
    void parse_EmptyDocument_ProtocolIsEmpty() {
        givenObjectUnderTestExists();
        whenParseIsCalledFor("");
        thenProtocolHasItemCount(0);
        thenProtocolHasTitle(null);
        thenProtocolHasTopicsCount(0);
    }

    @Test
    void parse_StartTimeOnly_ProtocolHasSingleEntryWithTime() {
        givenObjectUnderTestExists();
        whenParseIsCalledFor("08:33");
        thenProtocolHasTitle(null);
        thenProtocolHasItemCount(1);
        thenProtocolHasTopicsCount(0);
        thenProtocolItemMatches(0, rawText("08:33"));
        thenProtocolItemMatches(0, isAll(startTime("08:33"), endTime(null)));
        thenProtocolItemMatches(0, isAll(text(null), timeSpent(-1), topic(null)));
    }

    @Test
    void parse_EndTimeOnNextDay_TimeWasCalculatedCorrectly() {
        givenObjectUnderTestExists();
        whenParseIsCalledFor("22:33-02:44");
        thenProtocolHasTitle(null);
        thenProtocolHasItemCount(1);
        thenProtocolHasTopicsCount(0);
        thenProtocolItemMatches(0, rawText("22:33-02:44"));
        thenProtocolItemMatches(0, isAll(startTime("22:33"), endTime("02:44")));
        thenProtocolItemMatches(0, isAll(text(null), timeSpent(251), topic(null)));
    }

    @Test
    void parse_SimpleEntry_TopicWasAdded() {
        givenObjectUnderTestExists();
        whenParseIsCalledFor("08:33 Dev");
        thenProtocolHasTitle(null);
        thenProtocolHasItemCount(1);
        thenProtocolItemMatches(0, rawText("08:33 Dev"));
        thenProtocolItemMatches(0, isAll(startTime("08:33"), endTime(null)));
        thenProtocolItemMatches(0, isAll(text(null), timeSpent(-1), topic("Dev")));
        thenProtocolHasTopicsCount(1);
        thenProtocolHasTopics("Dev");
    }

    @Test
    void parse_StartTimeAndEndTime_BothTimesWerePlaced() {
        givenObjectUnderTestExists();
        whenParseIsCalledFor("08:33-10:12 Dev");
        thenProtocolHasTitle(null);
        thenProtocolHasItemCount(1);
        thenProtocolItemMatches(0, rawText("08:33-10:12 Dev"));
        thenProtocolItemMatches(0, isAll(startTime("08:33"), endTime("10:12")));
        thenProtocolItemMatches(0, isAll(text(null), timeSpent(99), topic("Dev")));
        thenProtocolHasTopicsCount(1);
        thenProtocolHasTopics("Dev");
    }

    @Test
    void parse_EndTimeInNextLine_TimeSpentWasCalculated() {
        givenObjectUnderTestExists();
        whenParseIsCalledFor("08:33 Dev1\n10:13 Dev2");
        thenProtocolHasTitle(null);
        thenProtocolHasItemCount(2);
        thenProtocolHasTopicsCount(2);
        thenProtocolHasTopics("Dev1", "Dev2");

        thenProtocolItemMatches(0, rawText("08:33 Dev1"));
        thenProtocolItemMatches(0, isAll(startTime("08:33"), endTime("10:13")));
        thenProtocolItemMatches(0, isAll(text(null), timeSpent(100), topic("Dev1")));

        thenProtocolItemMatches(1, rawText("10:13 Dev2"));
        thenProtocolItemMatches(1, isAll(startTime("10:13"), endTime(null)));
        thenProtocolItemMatches(1, isAll(text(null), timeSpent(-1), topic("Dev2")));
    }

    @Test
    void parse_SameTopic_OnlyOneTopicWasCreated() {
        givenObjectUnderTestExists();
        whenParseIsCalledFor("08:33 Dev\n10:13 Dev\n11:04 Dev");
        thenProtocolHasTitle(null);
        thenProtocolHasItemCount(3);
        thenProtocolHasTopicsCount(1);
        thenProtocolHasTopics("Dev");

        thenProtocolItemMatches(0, rawText("08:33 Dev"));
        thenProtocolItemMatches(0, isAll(startTime("08:33"), endTime("10:13")));
        thenProtocolItemMatches(0, isAll(text(null), timeSpent(100), topic("Dev")));

        thenProtocolItemMatches(1, rawText("10:13 Dev"));
        thenProtocolItemMatches(1, isAll(startTime("10:13"), endTime("11:04")));
        thenProtocolItemMatches(1, isAll(text(null), timeSpent(51), topic("Dev")));

        thenProtocolItemMatches(2, rawText("11:04 Dev"));
        thenProtocolItemMatches(2, isAll(startTime("11:04"), endTime(null)));
        thenProtocolItemMatches(2, isAll(text(null), timeSpent(-1), topic("Dev")));
    }

    @Test
    void parse_ProtocolWithTitle_TitleAndItemsWereParsed() {
        givenObjectUnderTestExists();

        final String line1 = "Tue (Mar 10 2020)";
        final String line2 = "----";
        final String line3 = "01:00 One";
        final String line4 = "02:20 Two";

        String lines = String.join("\n", line1, line2, line3, line4);
        whenParseIsCalledFor(lines);

        thenProtocolHasTitle("Tue (Mar 10 2020)");
        thenProtocolHasItemCount(2);
        thenProtocolHasTopicsCount(2);
        thenProtocolHasTopics("One", "Two");

        thenProtocolItemMatches(0, isAll(startTime("01:00"), endTime("02:20")));
        thenProtocolItemMatches(0, isAll(text("Tue (Mar 10 2020)"), timeSpent(80), topic("One")));

        thenProtocolItemMatches(1, isAll(startTime("02:20"), endTime(null)));
        thenProtocolItemMatches(1, isAll(text("Tue (Mar 10 2020)"), timeSpent(-1), topic("Two")));
    }

    @Test
    void parse_ItemWithComment_CommentWasIgnored() {
        final String line1 = "01:00 A # : B: C";
        final String line2 = "02:00 B # : B: D: E";

        String lines = line1 + "\n" + line2;

        givenObjectUnderTestExists();
        whenParseIsCalledFor(lines);

        thenProtocolHasItemCount(2);
        thenProtocolHasTopics("A", "B");

        thenProtocolItemMatches(0, isAll(startTime("01:00"), endTime("02:00")));
        thenProtocolItemMatches(0, isAll(text(null), timeSpent(60), topic("A")));

        thenProtocolItemMatches(1, isAll(startTime("02:00"), endTime(null)));
        thenProtocolItemMatches(1, isAll(text(null), timeSpent(-1), topic("B")));
    }

    @Test
    void subTopics() {
        givenObjectUnderTestExists();

        final String line1 = "01:00 A: B : C";
        final String line2 = "02:00 A: B: D   :E";

        TracProtocol protocol = objUT.parse(line1 + "\n" + line2);

        assertEquals(1, protocol.getTopics().size());

        TracTopic topicA = protocol.getTopics().get(0);
        assertIsTopic("A", 1, topicA);

        TracTopic topicB = topicA.getChildren().get(0);
        assertIsTopic("B", 2, topicB);

        TracTopic topicC = topicB.getChildren().get(0);
        assertIsTopic("C", 0, topicC);

        TracTopic topicD = topicB.getChildren().get(1);
        assertIsTopic("D", 1, topicD);

        TracTopic topicE = topicD.getChildren().get(0);
        assertIsTopic("E", 0, topicE);

        assertSame(topicD, topicE.getParent());
        assertSame(topicB, topicD.getParent());
        assertSame(topicB, topicC.getParent());
        assertSame(topicA, topicB.getParent());
        assertSame(null, topicA.getParent());

    }

    private void givenObjectUnderTestExists() {
        objUT = new TracProtocolParser();
    }

    private void whenParseIsCalledFor(String input) {
        protocol = objUT.parse(input);
    }

    private void thenProtocolItemMatches(int pItemIndex, Matcher<TracItem> matcher) {
        TracItem item = protocol.getItems().get(pItemIndex);
        assertThat(item, matcher);
    }

    private void thenProtocolHasTopicsCount(int count) {
        assertEquals(count, protocol.getTopics().size());
    }

    private void thenProtocolHasTitle(String title) {
        assertEquals(title, protocol.getTitle());
    }

    private void thenProtocolHasItemCount(int count) {
        assertEquals(count, protocol.getItems().size());
    }

    private void thenProtocolHasTopics(String... topics) {
        assertEquals(topics.length, protocol.getTopics().size());

        List<String> names = protocol.getTopics().stream().map(t -> t.getName()).collect(Collectors.toList());

        assertThat(names, contains(topics));
    }

    @SafeVarargs
    private Matcher<TracItem> isAll(Matcher<TracItem>... matchers) {
        return new CustomMatcher<TracItem>("all of") {

            @Override
            public boolean matches(Object actual) {
                for (Matcher<TracItem> matcher : matchers) {
                    if (!matcher.matches(actual)) {
                        return false;
                    }
                }
                return true;
            }

        };
    }

    private Matcher<TracItem> timeSpent(int minutes) {
        return new CustomMatcher<TracItem>("minutes spent") {

            @Override
            public boolean matches(Object actual) {
                if (actual instanceof TracItem) {
                    TracItem item = (TracItem) actual;
                    return minutes == item.getTimeSpentInMinutes();
                }
                return false;
            }
        };
    }

    private Matcher<TracItem> text(String text) {
        return new CustomMatcher<TracItem>("text") {

            @Override
            public boolean matches(Object actual) {
                if (actual instanceof TracItem) {
                    TracItem item = (TracItem) actual;
                    return Objects.equals(text, item.getSectionTitle());
                }
                return false;
            }
        };
    }

    private Matcher<TracItem> rawText(String rawText) {
        return new CustomMatcher<TracItem>("raw text") {

            @Override
            public boolean matches(Object actual) {
                if (actual instanceof TracItem) {
                    TracItem item = (TracItem) actual;
                    return Objects.equals(rawText, item.getRawText());
                }
                return false;
            }
        };
    }

    private Matcher<TracItem> endTime(String endTime) {
        return new CustomMatcher<TracItem>("end time") {

            @Override
            public boolean matches(Object actual) {
                if (actual instanceof TracItem) {
                    TracItem item = (TracItem) actual;
                    return Objects.equals(endTime, item.getEndTime());
                }
                return false;
            }
        };
    }

    private Matcher<TracItem> startTime(String startTime) {
        return new CustomMatcher<TracItem>("start time") {

            @Override
            public boolean matches(Object actual) {
                if (actual instanceof TracItem) {
                    TracItem item = (TracItem) actual;
                    return Objects.equals(startTime, item.getStartTime());
                }
                return false;
            }
        };
    }

    private Matcher<TracItem> topic(String topic) {
        return new CustomMatcher<TracItem>("topic with name") {

            @Override
            public boolean matches(Object actual) {
                if (actual instanceof TracItem) {
                    TracItem item = (TracItem) actual;

                    if (topic == null) {
                        return item.getTopic() == null;
                    }

                    if (item.getTopic() != null) {
                        String actualTopicName = item.getTopic().getName();
                        return Objects.equals(actualTopicName, topic);
                    }
                }
                return false;
            }
        };
    }

    private void assertIsItem(String raw, String start, String end, int timeSpentInMin, TracItem actualItem) {
        assertEquals(raw, actualItem.getRawText());
        assertEquals(start, actualItem.getStartTime());
        assertEquals(end, actualItem.getEndTime());
        assertEquals(timeSpentInMin, actualItem.getTimeSpentInMinutes());
    }

    private void assertIsTopic(String name, int numChildren, TracTopic topic) {
        assertEquals(name, topic.getName());
        assertEquals(numChildren, topic.getChildren().size());
    }

}