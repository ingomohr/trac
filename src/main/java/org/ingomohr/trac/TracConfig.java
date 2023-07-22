package org.ingomohr.trac;

/**
 * Configuration used to start Trac.
 * <p>
 * Can be created via {@link #fromArgs(String[])}.
 * </p>
 */
public class TracConfig {

	private String path;

	private String[] adapterIDs;

	public static TracConfig fromArgs(String[] args) {
		TracConfig config = new TracConfig();

		boolean recordingPath = false;
		boolean recordingAdapterIDs = false;

		for (String arg : args) {

			if ("-path".equals(arg)) {
				recordingPath = true;
				recordingAdapterIDs = false;
			} else if ("-p".equals(arg)) {
				recordingPath = false;
				recordingAdapterIDs = true;
			} else {
				if (recordingPath) {
					config.setPath(arg);
				} else if (recordingAdapterIDs) {
					config.setAdapterIDs(arg.split(","));
				}

				recordingPath = false;
				recordingAdapterIDs = false;
			}
		}

		return config;
	}

	public String[] getAdapterIDs() {
		return adapterIDs;
	}

	public void setAdapterIDs(String[] adapterIDs) {
		this.adapterIDs = adapterIDs;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
