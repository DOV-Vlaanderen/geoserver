package org.geoserver.metadata.data.service;

import static org.junit.Assert.assertNotNull;

import org.geoserver.metadata.AbstractMetadataTest;
import org.geoserver.metadata.data.service.impl.MetadataConstants;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GeneratorServiceTest extends AbstractMetadataTest {

    @Autowired private GeneratorService generatorService;

    @Test
    public void testGeneratorService() {
        assertNotNull(
                generatorService.findGeneratorByType(MetadataConstants.FEATURE_CATALOG_TYPENAME));

        assertNotNull(generatorService.findGeneratorByType(MetadataConstants.DOMAIN_TYPENAME));
    }
}
