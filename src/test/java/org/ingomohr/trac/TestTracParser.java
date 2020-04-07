package org.ingomohr.trac;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.ingomohr.trac.model.TracItem;
import org.ingomohr.trac.model.TracProtocol;
import org.ingomohr.trac.model.TracTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestTracParser {

	private TracParser objUT;

	@BeforeEach
	void prep() {
		objUT = new TracParser();
	}

	@Test
	void emptyDocument() {
		TracProtocol protocol = objUT.parse("");
		assertEquals(0, protocol.getItems().size());
		assertEquals(null, protocol.getTitle());
		assertEquals(0, protocol.getTopics().size());
	}

	@Test
	void startTimeOnly() {
		TracProtocol protocol = objUT.parse("08:33");
		assertEquals(1, protocol.getItems().size());

		TracItem item = protocol.getItems().get(0);
		assertIsItem("08:33", "08:33", null, -1, item);

		assertNull(item.getTopic());

		assertEquals(null, protocol.getTitle());
		assertEquals(0, protocol.getTopics().size());
	}

	@Test
	void simpleEntry() {
		TracProtocol protocol = objUT.parse("08:33 Dev");
		assertEquals(1, protocol.getItems().size());

		TracItem item = protocol.getItems().get(0);
		assertIsItem("08:33 Dev", "08:33", null, -1, item);

		assertNotNull(item.getTopic());
		assertEquals(0, item.getTopic().getChildren().size());
		assertEquals(null, item.getTopic().getParent());

		assertEquals(null, protocol.getTitle());
		assertEquals(1, protocol.getTopics().size());
		assertSame(item.getTopic(), protocol.getTopics().get(0));
	}

	@Test
	void startTimeAndEndTime() {
		TracProtocol protocol = objUT.parse("08:33-10:12 Dev");
		assertEquals(1, protocol.getItems().size());

		TracItem item = protocol.getItems().get(0);
		assertIsItem("08:33-10:12 Dev", "08:33", "10:12", 99, item);
	}

	@Test
	void endTimeInNextLine() {
		TracProtocol protocol = objUT.parse("08:33 Dev\n10:13 Dev2");

		assertEquals(2, protocol.getItems().size());

		TracItem item = protocol.getItems().get(0);
		assertIsItem("08:33 Dev", "08:33", "10:13", 100, item);

		TracItem item2 = protocol.getItems().get(1);
		assertIsItem("10:13 Dev2", "10:13", null, -1, item2);

	}

	@Test
	void sameTopic() {
		TracProtocol protocol = objUT.parse("08:33 Dev\n10:13 XY\n11:04 Dev");

		assertEquals(3, protocol.getItems().size());

		assertIsItem("08:33 Dev", "08:33", "10:13", 100, protocol.getItems().get(0));
		assertIsItem("10:13 XY", "10:13", "11:04", 51, protocol.getItems().get(1));
		assertIsItem("11:04 Dev", "11:04", null, -1, protocol.getItems().get(2));

		assertEquals(2, protocol.getTopics().size());

		TracTopic topic0 = protocol.getItems().get(0).getTopic();
		TracTopic topic1 = protocol.getItems().get(1).getTopic();
		TracTopic topic2 = protocol.getItems().get(2).getTopic();

		assertSame(topic0, topic2);
		assertSame(topic0, protocol.getTopics().get(0));
		assertSame(topic1, protocol.getTopics().get(1));

		assertIsTopic("Dev", 0, topic0);
		assertIsTopic("XY", 0, topic1);
	}

	@Test
	void subTopics() {
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

	@Test
	void itemWithComment() {
		final String line1 = "01:00 A // : B: C";
		final String line2 = "02:00 B // : B: D: E";

		TracProtocol protocol = objUT.parse(line1 + "\n" + line2);

		assertEquals(2, protocol.getTopics().size());

		TracTopic topicA = protocol.getTopics().get(0);
		assertIsTopic("A", 0, topicA);

		TracTopic topicB = protocol.getTopics().get(1);
		assertIsTopic("B", 0, topicB);

		assertIsItem("01:00 A // : B: C", "01:00", "02:00", 60, protocol.getItems().get(0));
		assertIsItem("02:00 B // : B: D: E", "02:00", null, -1, protocol.getItems().get(1));

	}

	@Test
	void title() {
		final String line1 = "Tue (Mar 10 2020)";
		final String line2 = "----";
		final String line3 = "01:00 One";
		final String line4 = "02:20 Two";

		String lines = String.join("\n", line1, line2, line3, line4);
		TracProtocol protocol = objUT.parse(lines);

		assertEquals("Tue (Mar 10 2020)", protocol.getTitle());

		assertIsItem("01:00 One", "01:00", "02:20", 80, protocol.getItems().get(0));
		assertIsItem("02:20 Two", "02:20", null, -1, protocol.getItems().get(1));
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