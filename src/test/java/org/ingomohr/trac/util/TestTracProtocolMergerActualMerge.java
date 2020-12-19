package org.ingomohr.trac.util;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link TracProtocolMerger} with scenario that actually merges topics.
 */
public class TestTracProtocolMergerActualMerge extends TestTracProtocolMergerBase {

    @Test
    void merge_AAndBShareTopicInTheMiddle_MiddleTopicIsMerged() {
        givenProtocolAHasItems("a.1", "b.2", "c.3");
        givenProtocolBHasItems("d.4", "b.5", "e.6");
        whenProtocolBIsMergedOntoProtocolA();
        thenProtocolAShouldHaveTopics("a", "b", "c", "d", "e");
        andProtocolAShouldHaveItems("a.1", "b.2", "c.3", "d.4", "b.5", "e.6");
    }

    @Test
    void merge_AAndBShareAllTopicsInOrder_AllTopicsAreMerged() {
        givenProtocolAHasItems("a.1", "b.2", "c.3");
        givenProtocolBHasItems("a.4", "b.5", "c.6");
        whenProtocolBIsMergedOntoProtocolA();
        thenProtocolAShouldHaveTopics("a", "b", "c");
        andProtocolAShouldHaveItems("a.1", "b.2", "c.3", "a.4", "b.5", "c.6");
    }

    @Test
    void merge_AAndBShareAllTopicsNotInOrder_AllTopicsAreMergedAndProtocolAOrderRemains() {
        givenProtocolAHasItems("a.1", "b.2", "c.3");
        givenProtocolBHasItems("c.4", "a.5", "b.6");
        whenProtocolBIsMergedOntoProtocolA();
        thenProtocolAShouldHaveTopics("a", "b", "c");
        andProtocolAShouldHaveItems("a.1", "b.2", "c.3", "c.4", "a.5", "b.6");
    }

    @Test
    void merge_MultiLevelTopics_AllLevelsHaveBeenMerged() {
        givenProtocolAHasItems("a.1.x", "a.1.y", "a.1.y.z", "b");
        givenProtocolBHasItems("c.4", "a.1.y.m", "a.1.y.m2");
        whenProtocolBIsMergedOntoProtocolA();
        thenProtocolAShouldHaveTopics("a", "b", "c");
        andProtocolAShouldHaveItems("a.1.x", "a.1.y", "a.1.y.z", "b", "c.4", "a.1.y.m", "a.1.y.m2");
    }

}
