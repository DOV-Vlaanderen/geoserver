/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.fileservice.impl;

import org.geoserver.taskmanager.fileservice.FileService;
import org.geoserver.taskmanager.util.LookupServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Find all @FileService instance from the spring context.
 * The Lookup service will also define S3 fileServices for each configuration in the S3-geotiff module.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
@Component()
public class LookupFileServiceImpl extends LookupServiceImpl<FileService> {

    private final static Logger LOGGER = Logger.getLogger("FileServiceRegistryImpl");

    /**
     * Read the properties of the s3-geotiff module since it will be needed most of the times.
     */
    public static final String S3_GEOTIFF_CONFIG_PATH = "s3.properties.location";


    @Autowired(required = false)
    public void setFileServices(List<FileService> fileServices) {
        setNamed(fileServices);
    }

    @PostConstruct
    public void initializeS3GeotiffFileServices() {
        Properties prop = readProperties();
        prop.stringPropertyNames().stream()
                .filter(key -> key.endsWith(".s3.user"))
                .forEach(key -> addS3FileService(prop, key.replace(".s3.user", "")));
    }

    private void addS3FileService(Properties properties, String prefix) {
        ArrayList<FileService> fileServices = new ArrayList<>();
        S3FileServiceImpl fileService = new S3FileServiceImpl(
                properties.getProperty(prefix + ".s3.endpoint"),
                properties.getProperty(prefix + ".s3.user"),
                properties.getProperty(prefix + ".s3.password"), prefix);
        fileServices.add(fileService);

        setNamed(fileServices);
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
