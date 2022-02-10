package org.ingomohr.trac.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link TracProtocol}.
 */
public class TestTracProtocol {

    @Test
    void replaceItem() {
        TracProtocol protocol = new TracProtocol("myTitle");

        TracItem item1 = mkItem();
        TracItem item2 = mkItem();
        TracItem item3 = mkItem();
        TracItem item4 = mkItem();

        protocol.items().add(item1);
        protocol.items().add(item2);
        protocol.items().add(item3);

        protocol.replaceItem(item2, item4);

        assertEquals(3, protocol.items().size());
        assertSame(item1, protocol.items().get(0));
        assertSame(item4, protocol.items().get(1));
        assertSame(item3, protocol.items().get(2));
    }

    private TracItem mkItem() {
        return new TracItem(null, null, null);
    }

}
