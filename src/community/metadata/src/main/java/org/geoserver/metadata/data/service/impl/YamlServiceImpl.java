// Copyright (C) 2010-2011 DOV, http://dov.vlaanderen.be/
// All rights reserved
package org.geoserver.metadata.data.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.geoserver.metadata.data.dto.MetadataEditorConfiguration;
import org.geoserver.metadata.data.service.YamlService;
import org.geotools.util.logging.Logging;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Service responsible for interaction with yaml files. It will search for all *.yaml files in a given directory and try
 * to parse the files. Yaml files that cannot do parsed will be ignored.
 *
 * @author Timothy De Bock
 */
public class YamlServiceImpl implements YamlService {


    private static final java.util.logging.Logger LOGGER = Logging.getLogger(YamlServiceImpl.class);

    @Override
    public MetadataEditorConfiguration readValue(String folder) throws IOException {
        LOGGER.info("Searching for yamls in: " + folder);
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        MetadataEditorConfiguration configuration = new MetadataEditorConfiguration();
        try {
            ArrayList<File> files = new ArrayList<File>(
                    FileUtils.listFiles(new File(folder), new RegexFileFilter("^(.*?).yaml"), DirectoryFileFilter.DIRECTORY));

            for (File file : files) {
                try {
                    MetadataEditorConfiguration config = mapper.readValue(file, MetadataEditorConfiguration.class);
                    //Merge configuration
                    configuration.getAttributes().addAll(config.getAttributes());
                    configuration.getGeonetworks().addAll(config.getGeonetworks());
                    configuration.getComplextypes().addAll(config.getComplextypes());
                } catch (IOException e) {
                    LOGGER.severe(e.getMessage());
                }
            }
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
        }

        return configuration;
    }
}

