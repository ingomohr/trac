package org.ingomohr.trac.model.impl;

import java.util.Objects;

import org.ingomohr.trac.model.ITracItem;
import org.ingomohr.trac.model.ITracProtocol;

/**
 * Standard impl of {@link ITracItem}
 */
public class TracItem implements ITracItem {

    private String text;

    private ITracProtocol protocol;

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public ITracProtocol getProtocol() {
        return protocol;
    }

    @Override
    public void setProtocol(ITracProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(protocol, text);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TracItem)) {
            return false;
        }
        TracItem other = (TracItem) obj;
        return Objects.equals(protocol, other.protocol) && Objects.equals(text, other.text);
    }

    @Override
    public String toString() {
        return "TracItem [text=" + text + ", protocol=" + (protocol != null ? protocol.getTitle() : "null") + "]";
    }

}