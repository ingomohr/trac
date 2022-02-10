package org.ingomohr.trac;

import java.util.Objects;

/**
 * Configuration used to start Trac.
 * <p>
 * Can be created via {@link #fromArgs(String[])}.
 * </p>
 */
public class TracConfig {

    private String path;

    private boolean countProtocols;

    private boolean printProtocolTitles;

    public static TracConfig fromArgs(String[] args) {
        TracConfig config = new TracConfig();

        for (String arg : args) {
            if (arg.startsWith("-path=")) {
                config.setPath(arg.substring("-path=".length()));
            }
            
            if (Objects.equals("-countProtocols", arg)) {
                config.setCountProtocols(true);
            }
            if (Objects.equals("-printProtocolTitles", arg)) {
                config.setPrintProtocolTitles(true);
            }
        }

        return config;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isCountProtocols() {
        return countProtocols;
    }

    public void setCountProtocols(boolean countProtocols) {
        this.countProtocols = countProtocols;
    }

    public boolean isPrintProtocolTitles() {
        return printProtocolTitles;
    }

    public void setPrintProtocolTitles(boolean printProtocolTitles) {
        this.printProtocolTitles = printProtocolTitles;
    }

}
