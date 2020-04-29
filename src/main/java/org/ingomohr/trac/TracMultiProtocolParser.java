package org.ingomohr.trac;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.ingomohr.trac.model.TracProtocol;

/**
 * Parser to read multiple protocols at once.
 * <p>
 * Each protocol starts with a header line, followed by a line of dashes,
 * followed by the actual protocol entries.
 * </p>
 * <p>
 * Between every two protocols, there is at least one empty line.
 * </p>
 * 
 * <pre>
Di
---
08:00 Topic A
08:30-10:30 Topic B

Mo
---
08:27 Topic A
08:44-08:57 Topic B
09:25-45 Topic C: Topic C1
 * 
 * </pre>
 */
public class TracMultiProtocolParser {

    /**
     * Reads the protocols from the given document.
     * 
     * @param document the document to read from. Cannot be <code>null</code>.
     * @return all protocols. Never <code>null</code>, possibly empty.
     */
    public List<TracProtocol> parse(String document) {
        Objects.requireNonNull(document, "Document cannot be null.");

        final List<TracProtocol> protocols = new ArrayList<TracProtocol>();

        if (!document.isEmpty()) {
            String[] lines = document.split(System.lineSeparator());

            List<String> protocolChunks = toProtocolChunks(lines);

            protocolChunks.forEach(chunk -> {
                final TracProtocol protocol = new TracProtocolParser().parse(chunk);
                protocols.add(protocol);
            });

        }

        return protocols;
    }

    private List<String> toProtocolChunks(String[] lines) {
        final List<String> chunks = new ArrayList<String>();

        StringBuilder builder = new StringBuilder();

        for (String line : lines) {

            if (line.trim().isEmpty()) {
                if (builder.length() > 0) {
                    chunks.add(builder.toString());
                    builder = new StringBuilder();
                }
            } else {
                builder.append(line).append(System.lineSeparator());
            }
        }

        if (builder.length() > 0) {
            chunks.add(builder.toString());
        }

        return chunks;
    }

}
