/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service;

import org.geoserver.catalog.MetadataMap;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;


/**
 * Test Import geonetwork.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class ImportGeonetworkMetadataServiceTest extends AbstractMetadataTest{

    @Autowired
    ImportGeonetworkMetadataService importService;

    @Test
    public void testImport() throws IOException {
        MetadataMap metadataMap = importService.importMetadata("https://oefen.dov.vlaanderen.be/geonetwork/srv/api/records/1a2c6739-3c62-432b-b2a0-aaa589a9e3a1/formatters/xml", new MetadataMap());
        Assert.assertEquals("1a2c6739-3c62-432b-b2a0-aaa589a9e3a1",metadataMap.get("indentifier-single"));

    }
}
