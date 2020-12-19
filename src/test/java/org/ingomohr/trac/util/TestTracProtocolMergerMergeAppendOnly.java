package org.ingomohr.trac.util;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TracProtocolMerger}.
 * <p>
 * Scenarios where the protocols don't share any topics.
 * </p>
 */
public class TestTracProtocolMergerMergeAppendOnly extends TestTracProtocolMergerBase {

    @Test
    void merge_NoTopicToMergeAndProtocolAIsEmpty_topicsAndItemsHaveBeenAppended() {
        givenProtocolAHasItems(NONE);
        givenProtocolBHasItems("d.1", "e.2");
        whenProtocolBIsMergedOntoProtocolA();
        thenProtocolAShouldHaveTopics("d", "e");
        andProtocolAShouldHaveItems("d.1", "e.2");
    }

    @Test
    void merge_NoTopicToMergeAndProtocolBIsEmpty_topicsAndItemsHaveBeenAppended() {
        givenProtocolAHasItems("a.1", "b.2");
        givenProtocolBHasItems(NONE);
        whenProtocolBIsMergedOntoProtocolA();
        thenProtocolAShouldHaveTopics("a", "b");
        andProtocolAShouldHaveItems("a.1", "b.2");
    }

    @Test
    void merge_NoTopicToMerge_topicsAndItemsHaveBeenAppended() {
        givenProtocolAHasItems("a.1", "b.2");
        givenProtocolBHasItems("d.1", "e.2");
        whenProtocolBIsMergedOntoProtocolA();
        thenProtocolAShouldHaveTopics("a", "b", "d", "e");
        andProtocolAShouldHaveItems("a.1", "b.2", "d.1", "e.2");
    }

}
