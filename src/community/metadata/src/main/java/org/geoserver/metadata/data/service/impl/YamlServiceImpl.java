/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.geoserver.config.GeoServerDataDirectory;
import org.geoserver.metadata.data.dto.AttributeMappingConfiguration;
import org.geoserver.metadata.data.dto.MetadataAttributeConfiguration;
import org.geoserver.metadata.data.dto.MetadataEditorConfiguration;
import org.geoserver.metadata.data.service.YamlService;
import org.geoserver.platform.resource.Resource;
import org.geoserver.platform.resource.Resources;
import org.geotools.util.logging.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

/**
 * Service responsible for interaction with yaml files. It will search for all *.yaml files in a given directory and try
 * to parse the files. Yaml files that cannot do parsed will be ignored.
 *
 * @author Timothy De Bock
 */
@Component
public class YamlServiceImpl implements YamlService {

    @Autowired
    private GeoServerDataDirectory dataDirectory;

    private static final java.util.logging.Logger LOGGER = Logging.getLogger(YamlServiceImpl.class);

    private Resource getFolder() {
        return dataDirectory.get("metadata");
    }

    @Override
    public MetadataEditorConfiguration readConfiguration() throws IOException {
        Resource folder = getFolder();
        LOGGER.info("Searching for yamls in: " + folder.path());
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        MetadataEditorConfiguration configuration = new MetadataEditorConfiguration();
        try {
            for (Resource file : Resources.list(folder, new Resources.ExtensionFilter("YAML"))) {
                try (InputStream in = file.in()) {
                    MetadataEditorConfiguration config = mapper.readValue(in, MetadataEditorConfiguration.class);
                    //Merge configuration
                    for (MetadataAttributeConfiguration attribute : config.getAttributes()) {
                        if (attribute.getKey() == null) {
                            throw new IOException("The key of an attribute may not be null. " + attribute.getLabel());
                        }
                        if (attribute.getLabel() == null) {
                            attribute.setLabel(attribute.getKey());

                        }
                        configuration.getAttributes().add(attribute);
                    }
                    configuration.getGeonetworks().addAll(config.getGeonetworks());
                    configuration.getTypes().addAll(config.getTypes());
                } catch (IOException e) {
                    LOGGER.severe(e.getMessage());
                }
            }
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
        }

        return configuration;
    }

    @Override
    public AttributeMappingConfiguration readMapping() throws IOException {
        Resource folder = getFolder();
        LOGGER.info("Searching for yamls in: " + folder.path());
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        AttributeMappingConfiguration configuration = new AttributeMappingConfiguration();
        try {
            for (Resource file : Resources.list(folder, new Resources.ExtensionFilter("YAML"))) {
                try (InputStream in = file.in()) {
                    AttributeMappingConfiguration config = mapper.readValue(in, AttributeMappingConfiguration.class);
                    //Merge configuration
                    configuration.getGeonetworkmapping().addAll(config.getGeonetworkmapping());
                    configuration.getObjectmapping().addAll(config.getObjectmapping());
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

