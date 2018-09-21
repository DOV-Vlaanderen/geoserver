/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service;

import org.geoserver.metadata.data.dto.MetadataEditorConfiguration;
import org.geotools.util.logging.Logging;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.logging.Logger;


/**
 * Test data methods.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class MetadataServiceTest extends AbstractMetadataTest{

    private final static Logger LOGGER = Logging.getLogger(MetadataServiceTest.class);

    @Autowired
    MetadataEditorConfigurationService metadataService;

    @Test
    public void testFileRegistry() {
        MetadataEditorConfiguration configuration = metadataService.readConfiguration();
        Assert.assertNotNull(configuration);
        Assert.assertEquals(4, configuration.getAttributes().size());
        Assert.assertEquals(2, configuration.getGeonetworks().size());
        Assert.assertEquals(1, configuration.getComplextypes().size());

    }
}
