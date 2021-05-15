package org.ingomohr.trac.model;

/**
 * An item in an {@link ITracProtocol}.
 */
public interface ITracItem {

    /**
     * Returns the item text.
     */
    String getText();

    /**
     * Sets the item text.
     */
    void setText(String text);

    /**
     * Returns the protocol which the item belongs to.
     * 
     * @return protocol. <code>null</code> if not set.
     */
    ITracProtocol getProtocol();

    /**
     * Set the protocol which the item belongs to.
     * 
     * @param protocol the protocol to set.
     */
    void setProtocol(ITracProtocol protocol);

}
