package org.ingomohr.trac.model;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A protocol with a title and a number of items.
 * <p>
 * Note that protocols are immutable - i.e. you can only modify the list of
 * items, and you can't replace it - nor can you modify the protocol title.
 * </p>
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

    /**
     * Replaces (exactly) the given item (object) with the given other item.
     * <p>
     * Does nothing if the given item is not contained in the protocol.
     * </p>
     * 
     * @param item    the item to replace in the protocol. Cannot be
     *                <code>null</code>.
     * @param newItem the new item with replace the item with. Cannot be
     *                <code>null</code>.
     */
    public void replaceItem(TracItem item, TracItem newItem) {
        requireNonNull(item);
        requireNonNull(newItem);

        items.replaceAll((a) -> {
            if (a == item) {
                return newItem;
            }
            return a;
        });
    }

}
