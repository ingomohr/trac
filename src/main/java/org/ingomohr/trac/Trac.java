package org.ingomohr.trac;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.temporal.TemporalAccessor;
import java.util.List;

import org.ingomohr.trac.in.ITracReader;
import org.ingomohr.trac.in.impl.TracReader;
import org.ingomohr.trac.model.ITracProtocol;
import org.ingomohr.trac.util.FileReader;
import org.ingomohr.trac.util.TimeConverter;
import org.ingomohr.trac.util.TimeDiffCalculator;
import org.ingomohr.trac.util.TracProtocolInspector;

/**
 * App class for use with terminal.
 */
public class Trac {

    private static final String VERSION = "1.0.0";

    public static void main(String[] args) {
        System.out.println("Trac " + VERSION);

        TracConfig cfg = TracConfig.fromArgs(args);

        if (cfg.getPath() == null) {
            System.out.println("Please specify the file to read.");
            System.out.println("- e.g. -path=my-protocols.txt");
        } else {

            try {
                List<ITracProtocol> protocols = readProtocols(cfg);
                inspect(protocols, cfg);
            } catch (IOException e) {
                System.err.println("Cannot read protocols");
                e.printStackTrace();
            }
        }
    }

    private static List<ITracProtocol> readProtocols(TracConfig cfg) throws IOException {
        String path = cfg.getPath();
        Path actualPath = Paths.get(path);

        List<String> lines = new FileReader().readAllLines(actualPath);
        String doc = String.join(System.lineSeparator(), lines);

        ITracReader reader = new TracReader();
        List<ITracProtocol> protocols = reader.read(doc);
        return protocols;
    }

    private static void inspect(List<ITracProtocol> protocols, TracConfig cfg) {
        if (cfg.isCountProtocols()) {
            System.out.println("Number of protocols: " + protocols.size());
        }

        if (cfg.isPrintProtocolTitles()) {
            int i = 1;
            for (ITracProtocol protocol : protocols) {
                System.out.print(i++ + ": " + protocol.getTitle());

                TracProtocolInspector inspector = new TracProtocolInspector();
                TemporalAccessor start = inspector.getStartTime(protocol);
                TemporalAccessor end = inspector.getEndTime(protocol);

                if (start != null && end != null) {
                    TimeConverter converter = new TimeConverter();

                    String startHHmm = converter.toHHmm(start);
                    String endHHmm = converter.toHHmm(end);

                    int minutes = new TimeDiffCalculator().getDiffInMinutes(start, end);
                    String hhmm = converter.toHHmm(minutes);

                    System.out.println("  (" + startHHmm + "-" + endHHmm + " => " + hhmm);
                } else {
                    System.out.println("  (Start or End time missing)");

                }

            }
        }
    }

}
