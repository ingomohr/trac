package org.ingomohr.trac;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.ingomohr.trac.in.TracMultiProtocolParser;
import org.ingomohr.trac.model.TracProtocol;
import org.ingomohr.trac.out.TracProtocolWriterByTopic;
import org.ingomohr.trac.util.FileReader;
import org.ingomohr.trac.util.TracProtocolMerger;

/**
 * App class for use with terminal.
 */
public class Trac {

    public static void main(String[] args) {

        TracConfig config = TracConfig.fromArgs(args);

        logInfo("Trac");
        logInfo("--------------------");
        logInfo("Path           : " + config.getPath());
        logInfo("Merge protocols: " + config.isMergeProtocols());
        logInfo("--------------------");

        Path path = Paths.get(config.getPath());
        try {
            List<String> lines = new FileReader().readAllLines(path);
            String allLines = String.join(System.lineSeparator(), lines);

            TracMultiProtocolParser parser = new TracMultiProtocolParser();
            List<TracProtocol> protocols = parser.parse(allLines);
            logInfo("Found protocols: " + protocols.size());

            if (config.isMergeProtocols() && protocols.size() > 1) {
                logInfo("Merging protocols...");

                TracProtocolMerger merger = new TracProtocolMerger(protocols.get(0));

                for (int i = 1; i < protocols.size(); i++) {
                    merger.merge(protocols.get(i));
                }

                TracProtocol mergedProtocol = merger.getProtocol();

                TracProtocolWriterByTopic writer = new TracProtocolWriterByTopic();
                writer.write(mergedProtocol, System.out);
            } else {

                int i = 1;
                for (TracProtocol tracProtocol : protocols) {
                    System.out.println();
                    logInfo("Protocol #" + i++);
                    TracProtocolWriterByTopic writer = new TracProtocolWriterByTopic();
                    writer.write(tracProtocol, System.out);
                }
            }

        } catch (IOException e) {
            logError("Cannot read path", e);
        }

    }

    private static void logInfo(String message) {
        System.out.println("INFO: " + message);
    }

    private static void logError(String message, Throwable cause) {
        System.err.println("ERROR: " + message);
        if (cause != null) {
            cause.printStackTrace(System.err);
        }
    }

}
