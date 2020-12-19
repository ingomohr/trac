package org.ingomohr.trac.util;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.ingomohr.trac.model.TracProtocol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TracProtocolMerger}.
 */
public class TestTracProtocolMerger {

    private TracProtocolMerger objUT;

    private TracProtocol protocol;

    private TracProtocol inputProtocol;

    @BeforeEach
    void prep() {
        objUT = new TracProtocolMerger();
        protocol = mock(TracProtocol.class);
        inputProtocol = mock(TracProtocol.class);
    }

    @Test
    void constructor_createWithProtocol_canAccessProtocol() {
        assertSame(protocol, new TracProtocolMerger(protocol).getProtocol());
    }

    @Test
    void merge_inputIsEmptyProtocol_nothingChanged() {
        when(inputProtocol.getItems()).thenReturn(Collections.emptyList());
        when(inputProtocol.getTopics()).thenReturn(Collections.emptyList());

        objUT.merge(inputProtocol);

        verifyNoInteractions(protocol);
    }

}