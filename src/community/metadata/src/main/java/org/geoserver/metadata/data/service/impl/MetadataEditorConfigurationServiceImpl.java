/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service.impl;


import org.geoserver.metadata.data.dto.MetadataEditorConfiguration;
import org.geoserver.metadata.data.dto.impl.MetadataEditorConfigurationImpl;
import org.geoserver.metadata.data.service.MetadataEditorConfigurationService;
import org.geoserver.metadata.data.service.YamlService;
import org.geotools.util.logging.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.logging.Logger;

@Repository
public class MetadataEditorConfigurationServiceImpl implements MetadataEditorConfigurationService {


    private static final Logger LOGGER = Logging.getLogger(MetadataEditorConfigurationServiceImpl.class);

    @Autowired
    private YamlService yamlService;

    @Override
    public MetadataEditorConfiguration readConfiguration() {

        //process all the configurations
        MetadataEditorConfiguration configuration = new MetadataEditorConfigurationImpl();
        try {
            configuration = yamlService.readConfiguration();
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }

        return configuration;
    }

}
