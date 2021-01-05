package org.ingomohr.trac;

/**
 * Configuration used to start Trac.
 */
public class TracConfig {

    private String path;

    private boolean mergeProtocols;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isMergeProtocols() {
        return mergeProtocols;
    }

    public void setMergeProtocols(boolean mergeProtocols) {
        this.mergeProtocols = mergeProtocols;
    }

    public static TracConfig fromArgs(String[] args) {
        TracConfig config = new TracConfig();

        for (String arg : args) {
            if (arg.startsWith("-path=")) {
                config.setPath(arg.substring("-path=".length()));
            }
            if (arg.equals("-merge")) {
                config.setMergeProtocols(true);
            }
        }

        return config;
    }

}
