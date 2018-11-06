/* (c) 2017-2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.wicket;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.metadata.AbstractWicketMetadataTest;
import org.geoserver.metadata.data.model.MetadataTemplate;
import org.geoserver.metadata.web.panel.ImportGeonetworkPanel;
import org.geoserver.metadata.web.panel.ImportTemplatePanel;
import org.geoserver.metadata.web.panel.MetadataPanel;
import org.geoserver.web.data.resource.ResourceConfigurationPage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Test metadatatab in layer page.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class LayerMetadataTabTest extends AbstractWicketMetadataTest {

    @Before
    public void before() throws IOException, URISyntaxException {
        restoreTemplates();
        restoreLayers();

        LayerInfo layer = geoServer.getCatalog().getLayers().get(0);
        login();
        tester.startPage(new ResourceConfigurationPage(layer, false));
        tester.clickLink("publishedinfo:tabs:tabs-container:tabs:3:link");
    }


    @Test
    public void testMetadataTab() {
        //check we are on the correct page
        tester.assertComponent("publishedinfo:tabs:panel:metadataPanel", MetadataPanel.class);
        tester.assertComponent("publishedinfo:tabs:panel:importTemplatePanel", ImportTemplatePanel.class);
        tester.assertComponent("publishedinfo:tabs:panel:geonetworkPanel", ImportGeonetworkPanel.class);
    }

    @Test
    public void testReadMedataFields() {

        //Metadata field
        tester.assertModelValue("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:1:itemProperties:1:component:textfield", "f7de06ca-f93c-457b-b0ae-9c52f5b1ca5e");
        tester.clickLink("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:1:itemProperties:1:component:generateUUID");
        Component metadataTextField = tester.getComponentFromLastRenderedPage("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:1:itemProperties:1:component:textfield");
        Assert.assertNotEquals(metadataTextField.getDefaultModel().getObject(), "f7de06ca-f93c-457b-b0ae-9c52f5b1ca5e");

        tester.assertModelValue("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:3:itemProperties:1:component:textfield", 88);

        tester.assertModelValue("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:2:itemProperties:1:component:textfield", "template-identifier");
        tester.assertModelValue("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:4:itemProperties:1:component:dropdown", "Don't select this one");

        tester.assertModelValue("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:0:component:textfield", "reflist-first");
        tester.assertModelValue("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:attributesTablePanel:listContainer:items:2:itemProperties:0:component:textfield", "reflist-second");

        tester.assertModelValue("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:7:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:0:component:attributesTablePanel:listContainer:items:1:itemProperties:1:component:textfield", "First object :code");
        tester.assertModelValue("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:7:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:0:component:attributesTablePanel:listContainer:items:2:itemProperties:1:component:textfield", "First object :codeSpace");

        tester.assertModelValue("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:8:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:0:component:attributesTablePanel:listContainer:items:1:itemProperties:1:component:textfield", "theObject catalog");
        tester.assertModelValue("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:8:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:0:component:attributesTablePanel:listContainer:items:2:itemProperties:1:component:dropdown", "Date");
        tester.assertModelValue("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:8:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:0:component:attributesTablePanel:listContainer:items:4:itemProperties:1:component:textfield", "a date");
        tester.assertModelValue("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:8:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:0:component:attributesTablePanel:listContainer:items:7:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:0:component:attributesTablePanel:listContainer:items:1:itemProperties:1:component:textfield", "nestedobject");
    }

    @Test
    public void testRepeatFields() {
        //add row
        tester.clickLink("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:addNew");
        tester.assertComponent("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:attributesTablePanel:listContainer:items:3:itemProperties:0:component:textfield", TextField.class);

        //delete row
        tester.clickLink("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:attributesTablePanel:listContainer:items:3:itemProperties:1:component");
        tester.clickLink("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:attributesTablePanel:listContainer:items:2:itemProperties:1:component");
        tester.clickLink("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:1:component");
        tester.assertComponent("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:noData", Label.class);

        print(tester.getLastRenderedPage(), true, true);
    }

    @Test
    public void testLinkWithSimpleTemplate() {
        //link
        DropDownChoice selectTemplate = (DropDownChoice) tester.getComponentFromLastRenderedPage("publishedinfo:tabs:panel:importTemplatePanel:form:metadataTemplate");
        MetadataTemplate template = (MetadataTemplate) selectTemplate.getChoices().get(0);
        ((IModel<MetadataTemplate>) selectTemplate.getDefaultModel()).setObject(template);
        tester.clickLink("publishedinfo:tabs:panel:importTemplatePanel:form:link");
        //test list of linked templates
        tester.assertLabel("publishedinfo:tabs:panel:importTemplatePanel:form:templatesPanel:listContainer:items:1:itemProperties:0:component", "simple fields");
        //test values
        tester.assertModelValue("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:2:itemProperties:1:component:textfield", "template-identifier");
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:2:itemProperties:1:component").isEnabled());

        tester.assertModelValue("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:3:itemProperties:1:component:textfield", 77);
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:3:itemProperties:1:component").isEnabled());

        tester.assertModelValue("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:4:itemProperties:1:component:dropdown", "Select me");
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:4:itemProperties:1:component").isEnabled());

        Assert.assertTrue(tester.getComponentFromLastRenderedPage("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:1:itemProperties:1:component").isEnabled());
        Assert.assertTrue(tester.getComponentFromLastRenderedPage("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component").isEnabled());
        Assert.assertTrue(tester.getComponentFromLastRenderedPage("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:6:itemProperties:1:component").isEnabled());

    }


    @Test
    public void testImportFromGeonetwork() {

    }
}
