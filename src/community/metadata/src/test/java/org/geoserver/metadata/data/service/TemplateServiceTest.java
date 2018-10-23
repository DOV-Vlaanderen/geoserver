/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service;


import org.geoserver.metadata.AbstractMetadataTest;
import org.geoserver.metadata.data.model.MetadataTemplate;
import org.geoserver.metadata.data.model.impl.ComplexMetadataMapImpl;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;


/**
 * Test the template service.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class TemplateServiceTest extends AbstractMetadataTest {

    @Autowired
    private MetadataTemplateService service;

    @Test
    public void testList() throws IOException {
        List<MetadataTemplate> actual = service.list();
        Assert.assertEquals(6, actual.size());
        Assert.assertEquals("allData", actual.get(0).getName());
        Assert.assertNotNull(actual.get(0).getMetadata());
    }

    @Test
    public void testLoad() throws IOException {
        MetadataTemplate actual = service.load("allData");

        Assert.assertNotNull(actual.getName());
        Assert.assertEquals("allData", actual.getName());
        Assert.assertNotNull(actual.getMetadata());
    }

    @Test
    public void testSave() throws IOException {

        MetadataTemplate metadataTemplate = new MetadataTemplate();
        metadataTemplate.setName("new-record");
        metadataTemplate.setDescription("new-record-description");
        metadataTemplate.setMetadata(new ComplexMetadataMapImpl(new HashMap<>()));

        service.save(metadataTemplate);

        MetadataTemplate actual = service.load("new-record");
        Assert.assertEquals("new-record", actual.getName());
        Assert.assertEquals("new-record-description", actual.getDescription());
        Assert.assertNotNull(actual.getMetadata());

    }

    @Test
    public void testSaveErrorFlow() throws IOException {

        MetadataTemplate metadataTemplate = new MetadataTemplate();
        //name required
        try {
            service.save(metadataTemplate);
            Assert.fail("Should trow error");
        } catch (IOException ignored) {

        }
        //no duplicate names
        metadataTemplate.setName("allData");
        try {
            service.save(metadataTemplate);
            Assert.fail("Should trow error");
        } catch (IOException ignored) {
        }
    }


    @Test
    public void testDelete() throws IOException {
        int initial = service.list().size();

        MetadataTemplate actual = service.load("allData");
        service.delete(actual);

        Assert.assertEquals(initial - 1, service.list().size());
    }

}
