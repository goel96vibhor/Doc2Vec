package com.media.net.Utils;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.LinkedHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: kushagra.s
 * Date: 8/28/12
 * Time: 4:05 PM
 */
public class ApplicationPropertiesInitializer {

    private static Logger log = Logger.getLogger(ApplicationPropertiesInitializer.class);
    public static boolean initCalled = false;
    public static boolean initDone = false;
    private static final Object INITIALIZER_LOCK = new Object();

    public LinkedHashMap<String, Boolean> propertiesFileList = null;
    public String absolutePath = null;

    private static ApplicationPropertiesInitializer propertiesInitializer = null;
    public static final Object SingleTonLock = new Object();

    private ApplicationPropertiesInitializer() {

    }


    public static ApplicationPropertiesInitializer getInstance(LinkedHashMap<String, Boolean> propertiesFileList, String absolutePath) {
        synchronized (SingleTonLock) {
            if (propertiesInitializer == null) {
                propertiesInitializer = new ApplicationPropertiesInitializer();
                propertiesInitializer.propertiesFileList = propertiesFileList;
                propertiesInitializer.absolutePath = absolutePath;
            }
        }
        return propertiesInitializer;
    }

    public static ApplicationPropertiesInitializer getInstance(LinkedHashMap<String, Boolean> propertiesFileList) {
        synchronized (SingleTonLock) {
            if (propertiesInitializer == null) {
                propertiesInitializer = new ApplicationPropertiesInitializer();
                propertiesInitializer.propertiesFileList = propertiesFileList;
                propertiesInitializer.absolutePath = null;
            } else {
                propertiesInitializer.propertiesFileList = propertiesFileList;
            }
        }
        return propertiesInitializer;
    }

    public static ApplicationPropertiesInitializer getInstance(String absolutePath) {
        synchronized (SingleTonLock) {
            if (propertiesInitializer == null) {
                log.info("Setting Application Properties Initializer with absolute path as :  " + absolutePath);
                propertiesInitializer = new ApplicationPropertiesInitializer();
                propertiesInitializer.absolutePath = absolutePath;
            } else {
                log.info("Returning Application Properties Initializer instance as it is already initialized, n absolutePath is : " + absolutePath);
            }
        }
        return propertiesInitializer;
    }

    public static ApplicationPropertiesInitializer getInstance() {
        synchronized (SingleTonLock) {
            if (propertiesInitializer == null) {
                log.info("Setting Application Properties Initializer without any absolute path");
                propertiesInitializer = new ApplicationPropertiesInitializer();
            } else {
                log.info("Returning Application Properties Initializer instance as it is already initialized, n absolutePath is : " + propertiesInitializer.absolutePath);
            }
        }
        return propertiesInitializer;
    }

    public void initProperties() {
        if (Util.parseData(absolutePath) == null)
            initJarProperties();
        else
            initWarProperties();
    }

    public void initJarProperties() {
        if (initDone)
            return;
        synchronized (INITIALIZER_LOCK) {
            if (!initCalled) {
                initCalled = true;
                try {
                    String projectPath = ApplicationPropertiesInitializer.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                    String parentProjectPath = new File(projectPath).getParent();
                    for (String propertiesFile : propertiesFileList.keySet())
                        ApplicationProperties.loadProperties(new File(parentProjectPath + "/" + propertiesFile + ".properties"), propertiesFileList.get(propertiesFile));
                } catch (Exception e) {
                    log.error("Error Loading Application Properties:" + e.getMessage(), e);
                }
                log.info("Properties Loading Successful");
                initDone = true;
            }
        }
    }

    public void initWarProperties() {
        if (initDone)
            return;
        synchronized (INITIALIZER_LOCK) {
            if (!initCalled) {
                initCalled = true;
                log.info("AbsolutePath " + absolutePath);
                try {
                    for (String propertiesFile : propertiesFileList.keySet())
                        ApplicationProperties.loadProperties(new File(absolutePath + "/" + propertiesFile + ".properties"), propertiesFileList.get(propertiesFile));
                } catch (Exception e) {
                    log.error("Error Loading Application Properties:" + e.getMessage(), e);
                }
                log.info("WAR Properties Loaded Successful");
                initDone = true;
            }
        }
    }
}
