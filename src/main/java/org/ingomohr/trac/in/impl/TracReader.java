package org.ingomohr.trac.in.impl;

import static java.util.Objects.requireNonNull;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ingomohr.trac.in.ITracReader;
import org.ingomohr.trac.model.ITracItem;
import org.ingomohr.trac.model.ITracProtocol;
import org.ingomohr.trac.model.IWorklogItem;
import org.ingomohr.trac.model.impl.TracItem;
import org.ingomohr.trac.model.impl.TracProtocol;
import org.ingomohr.trac.model.impl.WorklogItem;

/**
 * Standard implementation for {@link ITracReader}.
 * <p>
 * <ul>
 * <li>Considers empty lines as separators between protocols</li>
 * <li>Considers lines starting with 'hh:mm-hh:mm' as worklog items</li>
 * <li>Considers the first line as document title (if it's no worklog item)</li>
 * <li>Considers all other lines as meta-information w/o specific semantics</li>
 * </ul>
 * </p>
 */
public class TracReader implements ITracReader {

    protected static final Pattern PATTERN_ITEM = Pattern
            .compile("([0-2][0-9]:[0-5][0-9])(-[0-2][0-9]:[0-5][0-9])?(.*)");

    @Override
    public List<ITracProtocol> read(String document) {
        requireNonNull(document, "Document cannot be null.");

        final List<ITracProtocol> protocols = new ArrayList<ITracProtocol>();

        if (!document.isEmpty()) {
            String[] lines = document.split(System.lineSeparator());

            List<String> protocolChunks = toProtocolChunks(lines);

            for (String chunk : protocolChunks) {
                ITracProtocol protocol = readProtocol(chunk);
                protocols.add(protocol);
            }
        }

        return protocols;
    }

    /**
     * Turns the given lines into protocol chunks. Each of the chunks contains all
     * lines of one protocol.
     * <p>
     * The standard implementation considers every empty line as separator between
     * protocols.
     * </p>
     * 
     * @param lines the lines to turn into protocol chunks. Cannot be
     *              <code>null</code>.
     * @return protocol chunks. Never <code>null</code>, possibly empty.
     */
    protected List<String> toProtocolChunks(String[] lines) {
        final List<String> chunks = new ArrayList<String>();

        StringBuilder builder = new StringBuilder();

        for (String line : requireNonNull(lines)) {

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

    protected ITracProtocol readProtocol(String document) {
        ITracProtocol protocol = createProtocol();

        IWorklogItem predecessorWorklogItem = null;

        if (!document.isEmpty()) {
            final String[] lines = document.split(System.lineSeparator());

            for (int i = 0; i < lines.length; i++) {
                final String line = lines[i].trim();

                if (isEmptyLine(line)) {
                    continue;
                }

                final Matcher workLogItemMatcher = PATTERN_ITEM.matcher(line);
                final boolean isWorkItem = workLogItemMatcher.matches();

                if (!isWorkItem) {
                    ITracItem item = createItem(protocol);
                    item.setText(line);

                    if (protocol.getTitle() == null) {
                        protocol.setTitle(line);
                    }
                } else {

                    if (isWorkItem) {
                        IWorklogItem item = createWorklogItem(protocol);
                        readWorklogItem(workLogItemMatcher, line, item);

                        if (predecessorWorklogItem != null && predecessorWorklogItem.getEndTime() == null) {
                            predecessorWorklogItem.setEndTime(item.getStartTime());
                        }

                        predecessorWorklogItem = item;
                    }
                }
            }
        }

        return protocol;
    }

    /**
     * Creates a new {@link IWorklogItem} added to the given protocol.
     * 
     * @param protocol the protocol. Cannot be <code>null</code>.
     * @return new item that is added to the given protocol. Never
     *         <code>null</code>.
     */
    protected IWorklogItem createWorklogItem(ITracProtocol protocol) {
        requireNonNull(protocol);

        IWorklogItem item = new WorklogItem();
        addItem(item, protocol);

        return item;
    }

    /**
     * Creates a new {@link ITracItem} added to the given protocol.
     * 
     * @param protocol the protocol. Cannot be <code>null</code>.
     * @return new item that is added to the given protocol. Never
     *         <code>null</code>.
     */
    protected ITracItem createItem(ITracProtocol protocol) {
        requireNonNull(protocol);

        ITracItem item = new TracItem();
        protocol.getItems().add(item);
        item.setProtocol(protocol);

        return item;
    }

    /**
     * Adds the given item to the given protocol.
     * 
     * @param item     the item to add.
     * @param protocol the protocol to add to.
     */
    protected void addItem(IWorklogItem item, ITracProtocol protocol) {
        protocol.getItems().add(item);
        item.setProtocol(protocol);
    }

    /**
     * Creates a new {@link ITracProtocol}.
     * 
     * @return new protocol. Never <code>null</code>.
     */
    protected ITracProtocol createProtocol() {
        return new TracProtocol();
    }

    /**
     * Returns <code>true</code> if the given line is empty.
     * 
     * @param line the line to check.
     * @return <code>true</code> if line is empty.
     */
    protected boolean isEmptyLine(String line) {
        return line.length() == 0;
    }

    /**
     * Reads the given line into the given {@link IWorklogItem}.
     * 
     * @param matcher the matcher for the given line. Cannot be <code>null</code>.
     * @param line    the line to read. Cannot be <code>null</code>.
     * @param item    the item to write to. Cannot be <code>null</code>.
     */
    protected void readWorklogItem(Matcher matcher, String line, IWorklogItem item) {

        int count = matcher.groupCount();

        switch (count) {
        case 3:

            String start = matcher.group(1);
            item.setStartTime(toTime(start));

            String end = matcher.group(2);
            if (end != null && end.length() > 0) {
                end = end.substring(1);
                item.setEndTime(toTime(end));
            }

            String message = matcher.group(3);
            item.setMessage(message.trim());
            break;

        default:
            throw new RuntimeException("Unsupported match: Cannot read line: '" + line + "'");
        }

    }

    private TemporalAccessor toTime(String start) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("HH:mm").toFormatter();
        return formatter.parse(start);
    }

}
