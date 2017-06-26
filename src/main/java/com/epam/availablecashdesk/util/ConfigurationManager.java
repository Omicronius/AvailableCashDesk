package com.epam.availablecashdesk.util;

import java.util.ResourceBundle;

public class ConfigurationManager {
    private final static ResourceBundle resourceBundle = ResourceBundle.getBundle("configuration");

    public static String getProperty(String key) {return resourceBundle.getString(key);}
}
