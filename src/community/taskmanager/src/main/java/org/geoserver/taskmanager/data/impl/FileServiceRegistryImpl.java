/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.data.impl;

import org.geoserver.taskmanager.data.FileService;
import org.geoserver.taskmanager.data.FileServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Find all @FileService instance from the spring context.
 * The Registry will also define S3 fileServices for each configuration in the S3-geotiff module.
 */
@Component
public class FileServiceRegistryImpl implements FileServiceRegistry {

    private final static Logger LOGGER = Logger.getLogger("FileServiceRegistryImpl");

    /**
     * Read the properties of the s3-geotiff module since it will be needed most of the times.
     */
    public static final String S3_GEOTIFF_CONFIG_PATH = "s3.properties.location";

    @Autowired(required = false)
    private List<FileService> fileServices;


    @Override
    public List<String> getFileServiceNames() {
        ArrayList<String> names = new ArrayList<>();
        for (FileService fileService : fileServices) {
            names.add(fileService.getName());
        }
        return names;
    }

    @Override
    public FileService getService(String name) {
        if (name != null) {
            for (FileService fileService : fileServices) {
                if (fileService.getName().equals(name)) {
                    return fileService;
                }
            }
        }
        return null;
    }

    @PostConstruct
    public void initializeS3GeotiffFileServices() {
        Properties prop = readProperties();
        prop.stringPropertyNames().stream()
                .filter(key -> key.endsWith(".s3.user"))
                .forEach(key -> addS3FileService(prop, key.replace(".s3.user", "")));
    }

    private void addS3FileService(Properties properties, String prefix) {
        if(fileServices == null){
            fileServices = new ArrayList<>();
        }
        S3FileServiceImpl fileService = new S3FileServiceImpl(
                properties.getProperty(prefix + ".s3.endpoint"),
                properties.getProperty(prefix + ".s3.user"),
                properties.getProperty(prefix + ".s3.password"));
        fileServices.add(fileService);
    }

    private Properties readProperties() {
        Properties prop;
        try {
            prop = new Properties();
            String property = System.getProperty(S3_GEOTIFF_CONFIG_PATH);
            if (property != null) {
                InputStream resourceAsStream = new FileInputStream(property);
                prop.load(resourceAsStream);
            }
        } catch (IOException ex) {
            LOGGER.severe(ex.getMessage());
            throw new IllegalArgumentException("The properties could not be found.", ex);
        }
        return prop;
    }
}
