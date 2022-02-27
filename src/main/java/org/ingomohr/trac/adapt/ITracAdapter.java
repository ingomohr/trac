package org.ingomohr.trac.adapt;

import java.util.List;

import org.ingomohr.trac.model.TracProtocol;

/**
 * Adapter for {@link TracProtocol}s.
 * <p>
 * Adapters can used to adapt protocols to find, combine and/or compute any
 * information that might be of interest for some cases.
 * </p>
 * 
 * @param <T> the target type.
 */
@FunctionalInterface
public interface ITracAdapter<T> {

    /**
     * Adapts the given protocols.
     * 
     * @param protocols the protocols to adapt. Cannot be <code>null</code>.
     * @return adapted model. Never <code>null</code>.
     */
    T adapt(List<TracProtocol> protocols);

}
