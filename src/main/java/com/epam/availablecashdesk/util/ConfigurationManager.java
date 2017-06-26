package com.epam.availablecashdesk.util;

import java.util.ResourceBundle;

public class ConfigurationManager {
    private final static ResourceBundle resourceBundle = ResourceBundle.getBundle("configuration");

    public static int getProperty(String key) {
        return Integer.parseInt(resourceBundle.getString(key));
    }
}
