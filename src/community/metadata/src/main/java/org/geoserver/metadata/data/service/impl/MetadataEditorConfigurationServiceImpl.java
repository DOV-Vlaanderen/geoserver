/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service.impl;


import org.geoserver.metadata.data.dto.MetadataEditorConfiguration;
import org.geoserver.metadata.data.service.MetadataEditorConfigurationService;
import org.geoserver.metadata.data.service.YamlService;
import org.geotools.util.logging.Logging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.logging.Logger;

@Repository
public class MetadataEditorConfigurationServiceImpl implements MetadataEditorConfigurationService {


    private static final Logger LOGGER = Logging.getLogger(MetadataEditorConfigurationServiceImpl.class);

    @Value("${metadata.folder:./src/test/resources}")
    private String folder;

    private YamlService yamlService = new YamlServiceImpl();

    @Override
    public MetadataEditorConfiguration readConfiguration() {

        //process all the configurations
        MetadataEditorConfiguration configuration = new MetadataEditorConfiguration();
        try {
            configuration = yamlService.readValue(folder);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }

        return configuration;
    }

}
