/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service;

import org.geoserver.metadata.AbstractMetadataTest;
import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


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
        Assert.assertEquals("99", parent.get(String.class, "number-field").getValue());
        Assert.assertEquals("Select me", parent.get(String.class, "dropdown-field").getValue());

        HashMap<String, List<Integer>> descriptionMap = createDescriptionMap();
        service.merge(parent, children, descriptionMap);

        //Should be updated simple fields
        Assert.assertEquals("template-identifier", parent.get(String.class, "indentifier-single").getValue());
        Assert.assertEquals("77", parent.get(String.class, "number-field").getValue());
        Assert.assertEquals("Or select this row", parent.get(String.class, "dropdown-field").getValue());

        Assert.assertEquals(0, descriptionMap.get("indentifier-single").get(0).intValue());
        Assert.assertEquals(0, descriptionMap.get("number-field").get(0).intValue());
        Assert.assertEquals(0, descriptionMap.get("dropdown-field").get(0).intValue());
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
        HashMap<String, List<Integer>> descriptionMap = createDescriptionMap();
        service.merge(parent, children, descriptionMap);

        //simple fields
        Assert.assertEquals("the-indentifier-single", parent.get(String.class, "indentifier-single").getValue());
        Assert.assertEquals("99", parent.get(String.class, "number-field").getValue());
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

        Assert.assertEquals(0, descriptionMap.get("referencesystem-object").get(0).intValue());

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
        HashMap<String, List<Integer>> descriptionMap = createDescriptionMap();
        service.merge(parent, children, descriptionMap);

        //simple fields
        Assert.assertEquals("the-indentifier-single", parent.get(String.class, "indentifier-single").getValue());
        Assert.assertEquals("99", parent.get(String.class, "number-field").getValue());
        Assert.assertEquals("Select me", parent.get(String.class, "dropdown-field").getValue());
        //Should be updated list simple fields
        Assert.assertEquals(5, parent.size("refsystem-as-list"));
        Assert.assertEquals("template-value01", parent.get(String.class, "refsystem-as-list", 0).getValue());
        Assert.assertEquals("template--value02", parent.get(String.class, "refsystem-as-list", 1).getValue());
        Assert.assertEquals("list-refsystem-01", parent.get(String.class, "refsystem-as-list", 2).getValue());
        Assert.assertEquals("list-refsystem-02", parent.get(String.class, "refsystem-as-list", 3).getValue());
        Assert.assertEquals("list-refsystem-03", parent.get(String.class, "refsystem-as-list", 4).getValue());

        Assert.assertEquals(0, descriptionMap.get("refsystem-as-list").get(0).intValue());
        Assert.assertEquals(1, descriptionMap.get("refsystem-as-list").get(1).intValue());
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
        HashMap<String, List<Integer>> descriptionMap = createDescriptionMap();
        service.merge(parent, children, descriptionMap);

        //simple fields
        Assert.assertEquals("the-indentifier-single", parent.get(String.class, "indentifier-single").getValue());
        Assert.assertEquals("99", parent.get(String.class, "number-field").getValue());
        Assert.assertEquals("Select me", parent.get(String.class, "dropdown-field").getValue());
        //list simple fields
        Assert.assertEquals(3, parent.size("refsystem-as-list"));
        Assert.assertEquals("list-refsystem-01", parent.get(String.class, "refsystem-as-list", 0).getValue());
        Assert.assertEquals("list-refsystem-02", parent.get(String.class, "refsystem-as-list", 1).getValue());
        Assert.assertEquals("list-refsystem-03", parent.get(String.class, "refsystem-as-list", 2).getValue());
        //Object fields
        Assert.assertEquals("object-code", parent.get(String.class, "referencesystem-object_code").getValue());
        Assert.assertEquals("object-codeSpace", parent.get(String.class, "referencesystem-object_code-space").getValue());
        //Should be updated list of objects
        Assert.assertEquals(4, parent.size("referencesystem-object-list"));
        ComplexMetadataMap submapTemplateOne = parent.subMap("referencesystem-object-list", 0);
        ComplexMetadataMap submapTemplateTwo = parent.subMap("referencesystem-object-list", 1);
        ComplexMetadataMap submapUserInputOne = parent.subMap("referencesystem-object-list", 2);
        ComplexMetadataMap submapUserInputTwo = parent.subMap("referencesystem-object-list", 3);
        Assert.assertEquals("list-objectcode01", submapUserInputOne.get(String.class, "code").getValue());
        Assert.assertEquals("list-objectcodeSpace01", submapUserInputOne.get(String.class, "code-space").getValue());
        Assert.assertEquals("list-objectcode02", submapUserInputTwo.get(String.class, "code").getValue());
        Assert.assertEquals("list-objectcodeSpace02", submapUserInputTwo.get(String.class, "code-space").getValue());
        Assert.assertEquals("template-code01", submapTemplateOne.get(String.class, "code").getValue());
        Assert.assertEquals("template-codespace01", submapTemplateOne.get(String.class, "code-space").getValue());
        Assert.assertEquals("template-code02", submapTemplateTwo.get(String.class, "code").getValue());
        Assert.assertEquals("template-codespace02", submapTemplateTwo.get(String.class, "code-space").getValue());

        Assert.assertEquals(0, descriptionMap.get("referencesystem-object-list").get(0).intValue());
        Assert.assertEquals(1, descriptionMap.get("referencesystem-object-list").get(1).intValue());


    }


    @Test
    public void testMergeListNestedObjects() throws IOException {
        ComplexMetadataMap parent = templateService.load("allData").getMetadata();
        ComplexMetadataMap child = templateService.load("template-nested-object").getMetadata();

        ArrayList<ComplexMetadataMap> children = new ArrayList<>();
        children.add(child);
        service.merge(parent, children, createDescriptionMap());

        //list of objects
        Assert.assertEquals(2, parent.size("referencesystem-object-list"));
        ComplexMetadataMap submap01 = parent.subMap("referencesystem-object-list", 0);
        ComplexMetadataMap submap02 = parent.subMap("referencesystem-object-list", 1);
        Assert.assertEquals("list-objectcode01", submap01.get(String.class, "code").getValue());
        Assert.assertEquals("list-objectcodeSpace01", submap01.get(String.class, "code-space").getValue());
        Assert.assertEquals("list-objectcode02", submap02.get(String.class, "code").getValue());
        Assert.assertEquals("list-objectcodeSpace02", submap02.get(String.class, "code-space").getValue());
        //Should be updated list of nested objects
        Assert.assertEquals(3, parent.size("object-catalog"));
        ComplexMetadataMap submapTemplate = parent.subMap("object-catalog", 0);
        Assert.assertEquals("template-identifier", submapTemplate.get(String.class, "name").getValue());
        Assert.assertEquals("Geometry", submapTemplate.get(String.class, "type").getValue());
        Assert.assertEquals(2, submapTemplate.size("domain"));
        ComplexMetadataMap submapdomain01 = submapTemplate.subMap("domain", 0);
        ComplexMetadataMap submapdomain02 = submapTemplate.subMap("domain", 1);
        Assert.assertEquals("template-domain-code01", submapdomain01.get(String.class, "code").getValue());
        Assert.assertEquals("template-domain-code01", submapdomain01.get(String.class, "value").getValue());
        Assert.assertEquals("template-domain-code02", submapdomain02.get(String.class, "code").getValue());
        Assert.assertEquals("template-domain-code02", submapdomain02.get(String.class, "value").getValue());
    }

    @Test
    public void testUnlink() throws IOException {

        ComplexMetadataMap parent = templateService.load("allData").getMetadata();
        ComplexMetadataMap child = templateService.load("template-list-simple").getMetadata();
        ComplexMetadataMap child01 = templateService.load("simple fields").getMetadata();

        ArrayList<ComplexMetadataMap> children = new ArrayList<>();
        children.add(child);
        children.add(child01);
        HashMap<String, List<Integer>> descriptionMap = createDescriptionMap();

        //LINK
        service.merge(parent, children, descriptionMap);

        Assert.assertEquals("template-identifier", parent.get(String.class, "indentifier-single").getValue());
        Assert.assertEquals("77", parent.get(String.class, "number-field").getValue());
        Assert.assertEquals("Or select this row", parent.get(String.class, "dropdown-field").getValue());
        Assert.assertEquals(0, descriptionMap.get("indentifier-single").get(0).intValue());
        Assert.assertEquals(0, descriptionMap.get("number-field").get(0).intValue());
        Assert.assertEquals(0, descriptionMap.get("dropdown-field").get(0).intValue());

        Assert.assertEquals(5, parent.size("refsystem-as-list"));
        Assert.assertEquals("template-value01", parent.get(String.class, "refsystem-as-list", 0).getValue());
        Assert.assertEquals("template--value02", parent.get(String.class, "refsystem-as-list", 1).getValue());
        Assert.assertEquals("list-refsystem-01", parent.get(String.class, "refsystem-as-list", 2).getValue());
        Assert.assertEquals("list-refsystem-02", parent.get(String.class, "refsystem-as-list", 3).getValue());
        Assert.assertEquals("list-refsystem-03", parent.get(String.class, "refsystem-as-list", 4).getValue());
        Assert.assertEquals(0, descriptionMap.get("refsystem-as-list").get(0).intValue());
        Assert.assertEquals(1, descriptionMap.get("refsystem-as-list").get(1).intValue());

        //UNLINK
        service.merge(parent, new ArrayList<ComplexMetadataMap>(), descriptionMap);

        Assert.assertEquals("template-identifier", parent.get(String.class, "indentifier-single").getValue());
        Assert.assertEquals("77", parent.get(String.class, "number-field").getValue());
        Assert.assertEquals("Or select this row", parent.get(String.class, "dropdown-field").getValue());
        Assert.assertNull(descriptionMap.get("indentifier-single"));
        Assert.assertNull(descriptionMap.get("number-field"));
        Assert.assertNull(descriptionMap.get("dropdown-field"));

        Assert.assertEquals(3, parent.size("refsystem-as-list"));
        Assert.assertEquals("list-refsystem-01", parent.get(String.class, "refsystem-as-list", 0).getValue());
        Assert.assertEquals("list-refsystem-02", parent.get(String.class, "refsystem-as-list", 1).getValue());
        Assert.assertEquals("list-refsystem-03", parent.get(String.class, "refsystem-as-list", 2).getValue());

        Assert.assertNull(descriptionMap.get("refsystem-as-list"));

    }


    private HashMap<String, List<Integer>> createDescriptionMap() {
        return new HashMap<>();
    }
}
