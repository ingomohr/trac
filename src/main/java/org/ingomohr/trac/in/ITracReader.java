package org.ingomohr.trac.in;

import java.util.List;

import org.ingomohr.trac.model.ITracProtocol;

/**
 * Reads a trac input document into a list of {@link ITracProtocol}s.
 */
public interface ITracReader {

    /**
     * Reads the given document into {@link ITracProtocol}s.
     * 
     * @param document the document to read. Cannot be <code>null</code>.
     * @return list of protocols. Never <code>null</code>, possibly empty.
     * @throws TracReaderException if the given document cannot be read.
     */
    List<ITracProtocol> read(String document) throws TracReaderException;

}
