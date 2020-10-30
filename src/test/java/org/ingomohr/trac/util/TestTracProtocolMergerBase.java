package org.ingomohr.trac.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.ingomohr.trac.model.TracItem;
import org.ingomohr.trac.model.TracProtocol;
import org.ingomohr.trac.model.TracTopic;
import org.junit.jupiter.api.BeforeEach;

public class TestTracProtocolMergerBase {

    protected static final String[] NONE = new String[0];

    private TracProtocol protocolA;

    private TracProtocol protocolB;

    @BeforeEach
    void prep() {
        protocolA = new TracProtocol();
        protocolB = new TracProtocol();
    }

    protected void givenProtocolAHasItems(String... items) {
        mkItems(protocolA, items);
    }

    protected void givenProtocolBHasItems(String... items) {
        mkItems(protocolB, items);
    }

    protected void whenProtocolBIsMergedOntoProtocolA() {
        TracProtocolMerger objUT = new TracProtocolMerger();
        objUT.setProtocol(protocolA);
        objUT.merge(protocolB);

    }

    protected void thenProtocolAShouldHaveTopics(String... topics) {
        MatcherAssert.assertThat(protocolA.getTopics().stream().map(t -> t.getName()).collect(Collectors.toList()),
                Matchers.contains(topics));
    }

    protected void andProtocolAShouldHaveItems(String... items) {
        List<String> actualItems = toActualItems(protocolA);
        assertThat(actualItems, contains(items));
    }

    protected void mkItems(TracProtocol protocol, String... items) {

        for (String item : items) {
            String[] topics = item.split("\\.");

            TracTopic tt = null;

            for (int i = 0, n = topics.length; i < n; i++) {

                final String topic = topics[i];
                final boolean isLast = i == n - 1;

                if (tt == null) {
                    tt = protocol.getTopics().stream().filter(t -> Objects.equals(t.getName(), topic)).findFirst()
                            .orElse(null);

                    if (tt == null) {
                        tt = new TracTopic();
                        tt.setName(topic);
                        protocol.getTopics().add(tt);
                    }

                } else {
                    Objects.requireNonNull(tt);

                    TracTopic child = protocol.getTopics().stream().filter(t -> Objects.equals(t.getName(), topic))
                            .findFirst().orElse(null);
                    if (child == null) {
                        child = new TracTopic();
                        child.setName(topic);
                        tt.getChildren().add(child);
                        child.setParent(tt);
                        tt = child;
                    }
                }

                if (isLast) {
                    TracItem newItem = new TracItem();
                    newItem.setTopic(tt);
                    protocol.getItems().add(newItem);
                }
            }
        }
    }

    protected List<String> toActualItems(TracProtocol protocol) {
        List<String> items = new ArrayList<>();

        for (TracItem ti : protocol.getItems()) {
            items.add(toItemText(ti));
        }
        return items;
    }

    protected String toItemText(TracItem item) {

        String txt = null;

        TracTopic tt = item.getTopic();

        while (tt != null) {
            if (txt == null) {
                txt = tt.getName();
            } else {
                txt = tt.getName() + "." + txt;
            }
            tt = tt.getParent();
        }

        return txt;
    }

    protected TracProtocol getProtocolA() {
        return protocolA;
    }

    protected TracProtocol getProtocolB() {
        return protocolB;
    }

}
