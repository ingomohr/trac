package org.ingomohr.trac;

/**
 * Configuration used to start Trac.
 */
public class TracConfig {

    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public static TracConfig fromArgs(String[] args) {
        TracConfig config = new TracConfig();

        for (String arg : args) {
            if (arg.startsWith("-path=")) {
                config.setPath(arg.substring("-path=".length()));
            }
        }

        return config;
    }

}
