/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service;

import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Test the ComplexMetadataService. We use the data from the template service
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class ComplexMetadataServiceTest extends AbstractMetadataTest {


    @Autowired
    private MetadataTemplateService templateService;

    @Autowired
    private ComplexMetadataService service;

    @Test
    public void testMergeSimpleFields() throws IOException {
        ComplexMetadataMap parent = templateService.load("allData").getMetadata();
        ComplexMetadataMap child = templateService.load("simple fields").getMetadata();

        ArrayList<ComplexMetadataMap> children = new ArrayList<>();
        children.add(child);

        //simple fields orgiginal values
        Assert.assertEquals("the-indentifier-single", parent.get(String.class, "indentifier-single").getValue());
        Assert.assertEquals("88", parent.get(String.class, "number-field").getValue());
        Assert.assertEquals("Select me", parent.get(String.class, "dropdown-field").getValue());

        service.merge(parent, children);

        //Should be updated simple fields
        Assert.assertEquals("template-identifier", parent.get(String.class, "indentifier-single").getValue());
        Assert.assertEquals("template-88", parent.get(String.class, "number-field").getValue());
        Assert.assertEquals("Don't select this one", parent.get(String.class, "dropdown-field").getValue());
        //list simple fields
        Assert.assertEquals(3, parent.size("refsystem-as-list"));
        Assert.assertEquals("list-refsystem-01", parent.get(String.class, "refsystem-as-list", 0).getValue());
        Assert.assertEquals("list-refsystem-02", parent.get(String.class, "refsystem-as-list", 1).getValue());
        Assert.assertEquals("list-refsystem-03", parent.get(String.class, "refsystem-as-list", 2).getValue());
        //Object fields
        ComplexMetadataMap submap = parent.subMap("referencesystem-object");
        Assert.assertEquals("object-code", submap.get(String.class, "code").getValue());
        Assert.assertEquals("object-codeSpace", submap.get(String.class, "code-space").getValue());
        //list of objects
        Assert.assertEquals(2, parent.size("referencesystem-object-list"));
        ComplexMetadataMap submap01 = parent.subMap("referencesystem-object-list", 0);
        ComplexMetadataMap submap02 = parent.subMap("referencesystem-object-list", 1);
        Assert.assertEquals("list-objectcode01", submap01.get(String.class, "code").getValue());
        Assert.assertEquals("list-objectcodeSpace01", submap01.get(String.class, "code-space").getValue());
        Assert.assertEquals("list-objectcode02", submap02.get(String.class, "code").getValue());
        Assert.assertEquals("list-objectcodeSpace02", submap02.get(String.class, "code-space").getValue());
        //list of nested objects
        Assert.assertEquals(2, parent.size("object-catalog"));
        ComplexMetadataMap submapNested01 = parent.subMap("object-catalog", 0);
        Assert.assertEquals("First object catalog object", submapNested01.get(String.class, "name").getValue());
        Assert.assertEquals("String", submapNested01.get(String.class, "type").getValue());
        Assert.assertEquals(1, submapNested01.size("domain"));
        ComplexMetadataMap submapdomain = submapNested01.subMap("domain", 0);
        Assert.assertEquals("a domain for first catalog object", submapdomain.get(String.class, "code").getValue());
        Assert.assertEquals("15", submapdomain.get(String.class, "value").getValue());

    }


    @Test
    public void testMergeObject() throws IOException {
        ComplexMetadataMap parent = templateService.load("allData").getMetadata();
        ComplexMetadataMap child = templateService.load("object-field").getMetadata();

        ArrayList<ComplexMetadataMap> children = new ArrayList<>();
        children.add(child);
        service.merge(parent, children);

        //simple fields
        Assert.assertEquals("the-indentifier-single", parent.get(String.class, "indentifier-single").getValue());
        Assert.assertEquals("88", parent.get(String.class, "number-field").getValue());
        Assert.assertEquals("Select me", parent.get(String.class, "dropdown-field").getValue());
        //list simple fields
        Assert.assertEquals(3, parent.size("refsystem-as-list"));
        Assert.assertEquals("list-refsystem-01", parent.get(String.class, "refsystem-as-list", 0).getValue());
        Assert.assertEquals("list-refsystem-02", parent.get(String.class, "refsystem-as-list", 1).getValue());
        Assert.assertEquals("list-refsystem-03", parent.get(String.class, "refsystem-as-list", 2).getValue());
        //Should be updated Object fields
        ComplexMetadataMap submap = parent.subMap("referencesystem-object");
        Assert.assertEquals("templateValue-code", submap.get(String.class, "code").getValue());
        Assert.assertEquals("templateValue-codeSpace", submap.get(String.class, "code-space").getValue());

        //list of objects
        Assert.assertEquals(2, parent.size("referencesystem-object-list"));
        ComplexMetadataMap submap01 = parent.subMap("referencesystem-object-list", 0);
        ComplexMetadataMap submap02 = parent.subMap("referencesystem-object-list", 1);
        Assert.assertEquals("list-objectcode01", submap01.get(String.class, "code").getValue());
        Assert.assertEquals("list-objectcodeSpace01", submap01.get(String.class, "code-space").getValue());
        Assert.assertEquals("list-objectcode02", submap02.get(String.class, "code").getValue());
        Assert.assertEquals("list-objectcodeSpace02", submap02.get(String.class, "code-space").getValue());

    }



    @Test
    public void testMergeListFields() throws IOException {
        ComplexMetadataMap parent = templateService.load("allData").getMetadata();
        ComplexMetadataMap child = templateService.load("template-list-simple").getMetadata();

        ArrayList<ComplexMetadataMap> children = new ArrayList<>();
        children.add(child);
        service.merge(parent, children);

        //simple fields
        Assert.assertEquals("the-indentifier-single", parent.get(String.class, "indentifier-single").getValue());
        Assert.assertEquals("88", parent.get(String.class, "number-field").getValue());
        Assert.assertEquals("Select me", parent.get(String.class, "dropdown-field").getValue());
        //Should be updated list simple fields
        Assert.assertEquals(5, parent.size("refsystem-as-list"));
        Assert.assertEquals("list-refsystem-01", parent.get(String.class, "refsystem-as-list", 0).getValue());
        Assert.assertEquals("list-refsystem-02", parent.get(String.class, "refsystem-as-list", 1).getValue());
        Assert.assertEquals("list-refsystem-03", parent.get(String.class, "refsystem-as-list", 2).getValue());
        Assert.assertEquals("template-value01", parent.get(String.class, "refsystem-as-list", 3).getValue());
        Assert.assertEquals("template--value02", parent.get(String.class, "refsystem-as-list", 4).getValue());
        //Object fields
        Assert.assertEquals("object-code", parent.get(String.class, "referencesystem-object_code").getValue());
        Assert.assertEquals("object-codeSpace", parent.get(String.class, "referencesystem-object_code-space").getValue());
        //list of objects
        Assert.assertEquals(2, parent.size("referencesystem-object-list"));
        ComplexMetadataMap submap01 = parent.subMap("referencesystem-object-list", 0);
        ComplexMetadataMap submap02 = parent.subMap("referencesystem-object-list", 1);
        Assert.assertEquals("list-objectcode01", submap01.get(String.class, "code").getValue());
        Assert.assertEquals("list-objectcodeSpace01", submap01.get(String.class, "code-space").getValue());
        Assert.assertEquals("list-objectcode02", submap02.get(String.class, "code").getValue());
        Assert.assertEquals("list-objectcodeSpace02", submap02.get(String.class, "code-space").getValue());

    }


    @Test
    public void testMergeListObjects() throws IOException {
        ComplexMetadataMap parent = templateService.load("allData").getMetadata();
        ComplexMetadataMap child = templateService.load("template-object list").getMetadata();

        ArrayList<ComplexMetadataMap> children = new ArrayList<>();
        children.add(child);
        service.merge(parent, children);

        //simple fields
        Assert.assertEquals("the-indentifier-single", parent.get(String.class, "indentifier-single").getValue());
        Assert.assertEquals("88", parent.get(String.class, "number-field").getValue());
        Assert.assertEquals("Select me", parent.get(String.class, "dropdown-field").getValue());
        //Should be updated list simple fields
        Assert.assertEquals(3, parent.size("refsystem-as-list"));
        Assert.assertEquals("list-refsystem-01", parent.get(String.class, "refsystem-as-list", 0).getValue());
        Assert.assertEquals("list-refsystem-02", parent.get(String.class, "refsystem-as-list", 1).getValue());
        Assert.assertEquals("list-refsystem-03", parent.get(String.class, "refsystem-as-list", 2).getValue());
        //Object fields
        Assert.assertEquals("object-code", parent.get(String.class, "referencesystem-object_code").getValue());
        Assert.assertEquals("object-codeSpace", parent.get(String.class, "referencesystem-object_code-space").getValue());
        //list of objects
        Assert.assertEquals(4, parent.size("referencesystem-object-list"));
        ComplexMetadataMap submap01 = parent.subMap("referencesystem-object-list", 0);
        ComplexMetadataMap submap02 = parent.subMap("referencesystem-object-list", 1);
        ComplexMetadataMap submap03 = parent.subMap("referencesystem-object-list", 2);
        ComplexMetadataMap submap04 = parent.subMap("referencesystem-object-list", 3);
        Assert.assertEquals("list-objectcode01", submap01.get(String.class, "code").getValue());
        Assert.assertEquals("list-objectcodeSpace01", submap01.get(String.class, "code-space").getValue());
        Assert.assertEquals("list-objectcode02", submap02.get(String.class, "code").getValue());
        Assert.assertEquals("list-objectcodeSpace02", submap02.get(String.class, "code-space").getValue());
        Assert.assertEquals("template-code01", submap03.get(String.class, "code").getValue());
        Assert.assertEquals("template-codespace01", submap03.get(String.class, "code-space").getValue());
        Assert.assertEquals("template-code02", submap04.get(String.class, "code").getValue());
        Assert.assertEquals("template-codespace02", submap04.get(String.class, "code-space").getValue());


    }


    @Test
    public void testMergeListNestedObjects() throws IOException {
        ComplexMetadataMap parent = templateService.load("allData").getMetadata();
        ComplexMetadataMap child = templateService.load("template-nested-object").getMetadata();

        ArrayList<ComplexMetadataMap> children = new ArrayList<>();
        children.add(child);
        service.merge(parent, children);

        //list of objects
        Assert.assertEquals(2, parent.size("referencesystem-object-list"));
        ComplexMetadataMap submap01 = parent.subMap("referencesystem-object-list", 0);
        ComplexMetadataMap submap02 = parent.subMap("referencesystem-object-list", 1);
        Assert.assertEquals("list-objectcode01", submap01.get(String.class, "code").getValue());
        Assert.assertEquals("list-objectcodeSpace01", submap01.get(String.class, "code-space").getValue());
        Assert.assertEquals("list-objectcode02", submap02.get(String.class, "code").getValue());
        Assert.assertEquals("list-objectcodeSpace02", submap02.get(String.class, "code-space").getValue());
        //list of nested objects
        Assert.assertEquals(3, parent.size("object-catalog"));
        ComplexMetadataMap submapNested03 = parent.subMap("object-catalog", 2);
        Assert.assertEquals("template-identifier", submapNested03.get(String.class, "name").getValue());
        Assert.assertEquals("Geometry", submapNested03.get(String.class, "type").getValue());
        Assert.assertEquals(2, submapNested03.size("domain"));
        ComplexMetadataMap submapdomain01 = submapNested03.subMap("domain", 0);
        ComplexMetadataMap submapdomain02 = submapNested03.subMap("domain", 1);
        Assert.assertEquals("template-domain-code01", submapdomain01.get(String.class, "code").getValue());
        Assert.assertEquals("template-domain-code01", submapdomain01.get(String.class, "value").getValue());
        Assert.assertEquals("template-domain-code02", submapdomain02.get(String.class, "code").getValue());
        Assert.assertEquals("template-domain-code02", submapdomain02.get(String.class, "value").getValue());
    }


    @Test
    public void testMergeOrder() throws IOException {
        //TODO
        /*ComplexMetadataMap child = templateService.load("template-list-simple").getMetadata();
        ComplexMetadataMap parent = templateService.load("allData").getMetadata();

        ArrayList<ComplexMetadataMap> children = new ArrayList<>();
        children.add(child);
        service.merge(parent, children);
        Assert.assertEquals(3, 4);*/
    }
}
