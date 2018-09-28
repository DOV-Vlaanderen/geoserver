/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service;

import org.geoserver.metadata.data.dto.AttributeMappingConfiguration;
import org.geoserver.metadata.data.dto.MetadataEditorConfiguration;
import org.geoserver.metadata.data.service.impl.YamlServiceImpl;
import org.geotools.util.logging.Logging;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.logging.Logger;


/**
 * Test data methods.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class YamlServiceTest extends AbstractMetadataTest{

    private final static Logger LOGGER = Logging.getLogger(YamlServiceTest.class);



    private YamlService yamlService = new YamlServiceImpl();

    @Test
    public void testFileRegistry() throws IOException {
        MetadataEditorConfiguration configuration = yamlService.readConfiguration("./src/test/resources");
        Assert.assertNotNull(configuration);
        Assert.assertEquals(5, configuration.getAttributes().size());
        Assert.assertEquals(2, configuration.getGeonetworks().size());
        Assert.assertEquals(1, configuration.getComplextypes().size());

    }

    @Test
    public void testGeonetworkMappingRegistry() throws IOException {
        AttributeMappingConfiguration configuration = yamlService.readMapping("./src/test/resources");
        Assert.assertNotNull(configuration);
        Assert.assertEquals(4, configuration.getGeonetworkmapping().size());
        Assert.assertEquals(1, configuration.getObjectmapping().size());


    }
}
