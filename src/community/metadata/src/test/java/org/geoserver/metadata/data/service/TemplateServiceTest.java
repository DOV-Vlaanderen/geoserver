/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.metadata.AbstractMetadataTest;
import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.geoserver.metadata.data.model.MetadataTemplate;
import org.geoserver.metadata.data.model.impl.ComplexMetadataMapImpl;
import org.geoserver.metadata.data.model.impl.MetadataTemplateImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test the template service.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class TemplateServiceTest extends AbstractMetadataTest {

    @Autowired private MetadataTemplateService service;

    @After
    public void after() throws IOException {
        restoreTemplates();
    }

    @Test
    public void testList() throws IOException {
        List<MetadataTemplate> actual = service.list();
        Assert.assertEquals(6, actual.size());
        Assert.assertEquals("simple fields", actual.get(0).getName());
        Assert.assertNotNull(actual.get(0).getMetadata());
        Assert.assertEquals(0, actual.get(0).getOrder());
        Assert.assertEquals(1, actual.get(1).getOrder());
        Assert.assertEquals(2, actual.get(2).getOrder());
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

        MetadataTemplate metadataTemplate = new MetadataTemplateImpl();
        metadataTemplate.setName("new-record");
        metadataTemplate.setMetadata(new ComplexMetadataMapImpl(new HashMap<>()));

        service.save(metadataTemplate);

        MetadataTemplate actual = service.load("new-record");
        Assert.assertEquals("new-record", actual.getName());
        Assert.assertNotNull(actual.getMetadata());
        // Should not result in nullpointers because we read it from the xml
        // Empty sets en list are not stored in the xml file. this could result in nullpointers.
        actual.getMetadata().size("indentifier-single");
    }

    @Test
    public void testSaveErrorFlow() throws IOException {

        MetadataTemplate metadataTemplate = new MetadataTemplateImpl();
        // name required
        try {
            service.save(metadataTemplate);
            Assert.fail("Should trow error");
        } catch (IOException ignored) {

        }
        // no duplicate names
        metadataTemplate.setName("allData");
        try {
            service.save(metadataTemplate);
            Assert.fail("Should trow error");
        } catch (IOException ignored) {
        }
    }

    /**
     * Test if:
     * 1) the template data is updated
     * 2) the metadata for linked layers is updated.
     * @throws IOException
     */
    @Test
    public void testUpdate() throws IOException {
        MetadataTemplate initial = service.load("simple fields");
        Assert.assertEquals(
                "template-identifier",
                initial.getMetadata().get(String.class, "indentifier-single").getValue());
        Assert.assertTrue(initial.getLinkedLayers().contains("myLayerId"));

        initial.getMetadata().get(String.class, "indentifier-single").setValue("updated value");

        // check if the linked metadata is updated.
        LayerInfo initialMyLayer = geoServer.getCatalog().getLayer("myLayerId");
        Serializable initialCustom = initialMyLayer.getResource().getMetadata().get("custom");
        @SuppressWarnings("unchecked")
        IModel<ComplexMetadataMap> initialMetadataModel =
                new Model<ComplexMetadataMap>(
                        new ComplexMetadataMapImpl((HashMap<String, Serializable>) initialCustom));
        Assert.assertEquals(1, initialMetadataModel.getObject().size("object-catalog/type"));

        service.update(initial);

        MetadataTemplate actual = service.load("simple fields");
        Assert.assertEquals(
                "updated value",
                actual.getMetadata().get(String.class, "indentifier-single").getValue());

        // check if the linked metadata is updated.
        LayerInfo myLayer = geoServer.getCatalog().getLayer("myLayerId");
        Serializable custom = myLayer.getResource().getMetadata().get("custom");
        @SuppressWarnings("unchecked")
        IModel<ComplexMetadataMap> metadataModel =
                new Model<ComplexMetadataMap>(
                        new ComplexMetadataMapImpl((HashMap<String, Serializable>) custom));

        Assert.assertEquals(
                "updated value",
                metadataModel.getObject().get(String.class, "indentifier-single").getValue());
        // only linked data from the linked template should change
        Assert.assertEquals(1, metadataModel.getObject().size("object-catalog/type"));
    }

    @Test
    public void testDelete() throws IOException {
        int initial = service.list().size();

        MetadataTemplate actual = service.load("allData");
        service.delete(actual);

        Assert.assertEquals(initial - 1, service.list().size());
    }

    @Test
    public void testDeleteWarning() throws IOException {
        int initial = service.list().size();

        MetadataTemplate actual = service.load("simple fields");
        try {
            service.delete(actual);
            Assert.fail("should throw error for linked templates");
        } catch (IOException e) {

        }

        Assert.assertEquals(initial, service.list().size());
    }

    @Test
    public void testIncreasePriority() throws IOException {
        MetadataTemplate initial = service.load("allData");
        Assert.assertEquals("allData", service.list().get(5).getName());

        service.increasePriority(initial);
        service.increasePriority(initial);

        Assert.assertEquals("allData", service.list().get(3).getName());
    }

    @Test
    public void testDecreasePriority() throws IOException {
        MetadataTemplate initial = service.load("simple fields");
        Assert.assertEquals("simple fields", service.list().get(0).getName());

        service.decreasePriority(initial);
        service.decreasePriority(initial);

        Assert.assertEquals("simple fields", service.list().get(2).getName());
    }
}
