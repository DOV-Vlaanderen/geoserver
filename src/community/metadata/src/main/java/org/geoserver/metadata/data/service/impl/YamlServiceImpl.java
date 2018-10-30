/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.geoserver.config.GeoServerDataDirectory;
import org.geoserver.metadata.data.dto.AttributeComplexTypeMapping;
import org.geoserver.metadata.data.dto.AttributeMapping;
import org.geoserver.metadata.data.dto.AttributeMappingConfiguration;
import org.geoserver.metadata.data.dto.MetadataAttributeConfiguration;
import org.geoserver.metadata.data.dto.MetadataAttributeTypeConfiguration;
import org.geoserver.metadata.data.dto.MetadataEditorConfiguration;
import org.geoserver.metadata.data.dto.MetadataGeonetworkConfiguration;
import org.geoserver.metadata.data.service.YamlService;
import org.geoserver.platform.resource.Resource;
import org.geoserver.platform.resource.Resources;
import org.geotools.util.logging.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

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
        return dataDirectory.get(MetadataConstants.DIRECTORY);
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
                    readConfiguration(in, configuration, mapper);
                } 
            }            
            //add feature catalog
            try (InputStream in  = getClass().getResourceAsStream(MetadataConstants.FEATURE_CATALOG_CONFIG_FILE)) {
                readConfiguration(in, configuration, mapper);
            }
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
        }

        return configuration;
    }
    
    private void readConfiguration(InputStream in, MetadataEditorConfiguration configuration, ObjectMapper mapper) {
        try {
            MetadataEditorConfiguration config = mapper.readValue(in,
                    MetadataEditorConfiguration.class);
            // Merge attribute configuration and remove duplicates
            Set<String> attributeKeys = new HashSet<>();
            for (MetadataAttributeConfiguration attribute : config.getAttributes()) {
                if (attribute.getKey() == null) {
                    throw new IOException(
                            "The key of an attribute may not be null. " + attribute.getLabel());
                }
                if (attribute.getLabel() == null) {
                    attribute.setLabel(attribute.getKey());

                }
                if (!attributeKeys.contains(attribute.getKey())) {
                    configuration.getAttributes().add(attribute);
                    attributeKeys.add(attribute.getKey());
                }
            }
            // Merge geonetwork configuration and remove duplicates
            Set<String> geonetworkKeys = new HashSet<>();
            for (MetadataGeonetworkConfiguration geonetwork : config.getGeonetworks()) {
                if (!geonetworkKeys.contains(geonetwork.getName())) {
                    configuration.getGeonetworks().add(geonetwork);
                    geonetworkKeys.add(geonetwork.getName());
                }
            }
            // Merge Types configuration and remove duplicates
            Set<String> typesKeys = new HashSet<>();
            for (MetadataAttributeTypeConfiguration type : config.getTypes()) {
                if (!typesKeys.contains(type.getTypename())) {
                    configuration.getTypes().add(type);
                    typesKeys.add(type.getTypename());
                }
            }
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
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
                    Set<String> attKeys = new HashSet<>();
                    for (AttributeMapping mapping : config.getGeonetworkmapping()) {
                        if (!attKeys.contains(mapping.getGeoserver())) {
                            configuration.getGeonetworkmapping().add(mapping);
                            attKeys.add(mapping.getGeoserver());
                        }
                    }

                    Set<String> objectKay = new HashSet<>();
                    for (AttributeComplexTypeMapping mapping : config.getObjectmapping()) {
                        if (!objectKay.contains(mapping.getTypename())) {
                            configuration.getObjectmapping().add(mapping);
                            objectKay.add(mapping.getTypename());
                        }
                    }
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

