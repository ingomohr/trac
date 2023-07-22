package org.ingomohr.trac;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.ingomohr.trac.adapt.adapters.timespent.TracTimeSpentAdapter;
import org.ingomohr.trac.adapt.adapters.timespent.TracTimeSpentModel;
import org.ingomohr.trac.adapt.adapters.timespent.TracTimeSpentModelToStringAdapter;
import org.ingomohr.trac.in.DefaultTracReader;
import org.ingomohr.trac.in.TracReader;
import org.ingomohr.trac.model.TracProtocol;
import org.ingomohr.trac.util.FileReader;

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
			System.out.println("- e.g. -path my-protocols.txt");
		} else {

			String[] adapterIDs = cfg.getAdapterIDs();
			if (adapterIDs == null || adapterIDs.length == 0) {
				System.out.println("Please specify trac profiles to use to inspect your protocol.");
				System.out.println("- e.g. -p timeSpent");
			} else {
				try {
					List<TracProtocol> protocols = readProtocols(cfg);
					runAdapters(protocols, adapterIDs);

				} catch (IOException e) {
					System.err.println("Error: " + e.getMessage());
				}
			}
		}
	}

	private static void runAdapters(List<TracProtocol> protocols, String[] adapterIDs) {
		for (String adapterID : adapterIDs) {
			runAdapter(protocols, adapterID);
		}
	}

	private static void runAdapter(List<TracProtocol> protocols, String adapterID) {

		// TODO this is why adapter => (model => ) String have to be generalized

		if ("timeSpent".equals(adapterID)) {
			TracTimeSpentModel model = new TracTimeSpentAdapter().adapt(protocols);
			String str = new TracTimeSpentModelToStringAdapter().adapt(model);
			System.out.println(str);
		} else {
			throw new RuntimeException("Unknown profile: " + adapterID);
		}
	}

	private static List<TracProtocol> readProtocols(TracConfig cfg) throws IOException {
		String path = cfg.getPath();
		Path actualPath = Paths.get(path);

		List<String> lines = new FileReader().readAllLines(actualPath);
		String doc = String.join(System.lineSeparator(), lines);

		TracReader reader = new DefaultTracReader();
		List<TracProtocol> protocols = reader.read(doc);
		return protocols;
	}

}
