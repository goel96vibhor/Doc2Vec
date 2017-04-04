package com.media.net.Utils;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: jigar.p
 * Date: Jul 15, 2010
 * Time: 12:51:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class ApplicationProperties {
    private static Logger log = Logger.getLogger(ApplicationProperties.class.getName());
    private static Properties properties = new Properties();

    public static void loadProperties(InputStream file, boolean override) throws IOException {
        log.debug("Inside loadProperties");
        Properties tempProperties = new Properties();
        tempProperties.load(file);
        for (Object a : tempProperties.keySet()) {
            if (properties.containsKey(a)) {
                if (override) {
                    log.info("DuplicateProperty : " + a.toString() + " while loading from file stream");
                } else {
                    throw new RuntimeException("DuplicateProperty : ");
                }
            } else {
                log.info("Loaded property : " + a.toString() + " while loading from file stream");
            }
        }
        properties.putAll(tempProperties);
        log.info(tempProperties.size());

    }

    public static void loadProperties(File file, boolean override) throws IOException {
        log.debug("Inside loadProperties");

        Properties tempProperties = new Properties();
        tempProperties.load(new FileInputStream(file));
        for (Object a : tempProperties.keySet()) {
            if (properties.containsKey(a)) {
                if (override) {
                    log.info("DuplicateProperty : " + a.toString() + " overriden from " + file.getAbsolutePath());
                } else {
                    throw new RuntimeException("DuplicateProperty : " + a.toString() + " in " + file.getAbsolutePath());
                }
            } else
                log.info("Property : " + a.toString() + " obtained from " + file.getAbsolutePath());
        }
        properties.putAll(tempProperties);
        log.info(tempProperties.size() + " Application Properties Loaded from " + file.getAbsolutePath());
    }

    public static String getProperty(String key) {
        return (String) properties.get(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static Properties getPropertiesObject() {
        return properties;
    }
}