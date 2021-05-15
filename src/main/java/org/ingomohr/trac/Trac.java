package org.ingomohr.trac;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.ingomohr.trac.in.ITracReader;
import org.ingomohr.trac.in.impl.TracReader;
import org.ingomohr.trac.model.ITracProtocol;
import org.ingomohr.trac.util.FileReader;

/**
 * App class for use with terminal.
 */
public class Trac {

    public static void main(String[] args) {
        System.out.println("Trac");

        TracConfig cfg = TracConfig.fromArgs(args);

        try {
            List<ITracProtocol> protocols = readProtocols(cfg);
            inspect(protocols, cfg);
        } catch (IOException e) {
            System.err.println("Cannot read protocols");
            e.printStackTrace();
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
                System.out.println(i++ + ": " + protocol.getTitle());
            }
        }
    }

}
