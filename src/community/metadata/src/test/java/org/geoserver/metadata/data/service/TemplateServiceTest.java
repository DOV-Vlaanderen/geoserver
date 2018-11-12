/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service;


import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.metadata.AbstractMetadataTest;
import org.geoserver.metadata.data.model.ComplexMetadataMap;
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
        Assert.assertEquals("simple fields", actual.get(0).getName());
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
        //Should not result in nullpointers because we read it from the xml
        //Empty sets en list are not stored in the xml file. this could result in nullpointers.
        actual.getMetadata().size("indentifier-single");

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
    public void testUpdate() throws IOException {
        MetadataTemplate initial = service.load("simple fields");
        Assert.assertEquals("template-identifier", initial.getMetadata().get(String.class,"indentifier-single").getValue());
        Assert.assertTrue(initial.getLinkedLayers().contains("topp:mylayer"));

        initial.getMetadata().get(String.class, "indentifier-single").setValue("updated value");

        //check if the linked metadata is updated.
        LayerInfo initialMyLayer = geoServer.getCatalog().getLayerByName("mylayer");
        Serializable initialCustom = initialMyLayer.getResource().getMetadata().get("custom");
        @SuppressWarnings("unchecked")
        IModel<ComplexMetadataMap> initialMetadataModel = new Model<ComplexMetadataMap>(
                new ComplexMetadataMapImpl((HashMap<String, Serializable>) initialCustom));
        Assert.assertEquals(1, initialMetadataModel.getObject().size("object-catalog/type"));

        service.update(initial);

        MetadataTemplate actual= service.load("simple fields");
        Assert.assertEquals("updated value", actual.getMetadata().get(String.class,"indentifier-single").getValue());

        //check if the linked metadata is updated.
        LayerInfo myLayer = geoServer.getCatalog().getLayerByName("mylayer");
        Serializable custom = myLayer.getResource().getMetadata().get("custom");
        @SuppressWarnings("unchecked")
        IModel<ComplexMetadataMap> metadataModel = new Model<ComplexMetadataMap>(
                new ComplexMetadataMapImpl((HashMap<String, Serializable>) custom));


        Assert.assertEquals("updated value", metadataModel.getObject().get(String.class, "indentifier-single").getValue());
        //only linked data from the linked template should change
        Assert.assertEquals(1, metadataModel.getObject().size("object-catalog/type"));
    }


    @Test
    public void testDelete() throws IOException {
        int initial = service.list().size();

        MetadataTemplate actual = service.load("allData");
        service.delete(actual);

        Assert.assertEquals(initial - 1, service.list().size());
    }

}
