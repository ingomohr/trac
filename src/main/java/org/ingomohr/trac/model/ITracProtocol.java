package org.ingomohr.trac.model;

import java.util.List;

/**
 * A protocol that consists of {@link ITracItem}s.
 */
public interface ITracProtocol {

    /**
     * Returns the items that belong to the protocol.
     * 
     * @return items. Never <code>null</code>, possibly empty.
     */
    List<ITracItem> getItems();

    /**
     * Returns the title of the protocol.
     * 
     * @return title. <code>null</code> if not set.
     */
    String getTitle();

    /**
     * Sets the title of the protocol.
     * 
     * @param title the title to set.
     */
    void setTitle(String title);

}
