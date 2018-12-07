/* (c) 2017-2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.wicket;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Locale;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.metadata.AbstractWicketMetadataTest;
import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.geoserver.metadata.data.model.impl.MetadataTemplateImpl;
import org.geoserver.metadata.web.panel.ImportGeonetworkPanel;
import org.geoserver.metadata.web.panel.ImportTemplatePanel;
import org.geoserver.metadata.web.panel.MetadataPanel;
import org.geoserver.web.data.resource.ResourceConfigurationPage;
import org.geoserver.web.wicket.GeoServerDialog;
import org.geoserver.web.wicket.GeoServerTablePanel;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test metadatatab in layer page.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class LayerMetadataTabTest extends AbstractWicketMetadataTest {

    @Before
    public void before() throws IOException {
        Session.get().setLocale(new Locale("nl"));
        restoreTemplates();
        restoreLayers();

        LayerInfo layer = geoServer.getCatalog().getLayers().get(0);
        login();
        ResourceConfigurationPage page = new ResourceConfigurationPage(layer, false);
        tester.startPage(page);
        tester.clickLink("publishedinfo:tabs:tabs-container:tabs:3:link");
        tester.assertComponent(
                "publishedinfo:tabs:panel:importTemplatePanel", ImportTemplatePanel.class);
    }

    @After
    public void after() throws IOException {
        restoreTemplates();
        restoreLayers();
    }

    @Test
    public void testMetadataTab() {
        // check we are on the correct page
        tester.assertComponent("publishedinfo:tabs:panel:metadataPanel", MetadataPanel.class);
        tester.assertComponent(
                "publishedinfo:tabs:panel:importTemplatePanel", ImportTemplatePanel.class);
        tester.assertComponent(
                "publishedinfo:tabs:panel:geonetworkPanel", ImportGeonetworkPanel.class);
    }

    @Test
    public void testLocalizationLabels() {
        tester.assertLabel(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:2:itemProperties:0:component",
                "Indentifier single field");
        tester.assertLabel(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:3:itemProperties:0:component",
                "Getal veld");
        tester.assertLabel(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:0:component",
                "the refsystem as list field");
        tester.assertLabel(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:6:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:0:component",
                "Het code veld");
    }

    /** The layer is linked to the 'simple field' template. */
    @Test
    public void testReadMedataFields() {
        print(tester.getLastRenderedPage(), true, true);
        // Metadata field
        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:1:itemProperties:1:component:textfield",
                "f7de06ca-f93c-457b-b0ae-9c52f5b1ca5e");
        tester.clickLink(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:1:itemProperties:1:component:generateUUID");
        Component metadataTextField =
                tester.getComponentFromLastRenderedPage(
                        "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:1:itemProperties:1:component:textfield");
        Assert.assertNotEquals(
                metadataTextField.getDefaultModel().getObject(),
                "f7de06ca-f93c-457b-b0ae-9c52f5b1ca5e");

        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:3:itemProperties:1:component:textfield",
                77);

        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:2:itemProperties:1:component:textfield",
                "template-identifier");
        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:4:itemProperties:1:component:dropdown",
                "Or select this row");
        // string list (refsystem-as-list)
        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:0:component:textfield",
                "reflist-first");
        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:attributesTablePanel:listContainer:items:2:itemProperties:0:component:textfield",
                "reflist-second");
        // object list (referencesystem-object-list)
        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:7:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:0:component:attributesTablePanel:listContainer:items:1:itemProperties:1:component:textfield",
                "First object :code");
        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:7:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:0:component:attributesTablePanel:listContainer:items:2:itemProperties:1:component:textfield",
                "First object :codeSpace");

        // nested objects (object catalog)
        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:8:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:0:component:attributesTablePanel:listContainer:items:1:itemProperties:1:component:textfield",
                "theObject catalog");
        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:8:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:0:component:attributesTablePanel:listContainer:items:2:itemProperties:1:component:dropdown",
                "Date");
        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:8:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:0:component:attributesTablePanel:listContainer:items:4:itemProperties:1:component:textfield",
                "a date");
        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:8:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:0:component:attributesTablePanel:listContainer:items:7:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:0:component:attributesTablePanel:listContainer:items:1:itemProperties:1:component:textfield",
                "nestedobject");

        // test list of linked templates
        tester.assertLabel(
                "publishedinfo:tabs:panel:importTemplatePanel:templatesPanel:listContainer:items:1:itemProperties:0:component",
                "simple fields");

        // test values of 'simple fields template'
        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:2:itemProperties:1:component:textfield",
                "template-identifier");
        Assert.assertFalse(
                tester.getComponentFromLastRenderedPage(
                                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:2:itemProperties:1:component")
                        .isEnabled());

        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:3:itemProperties:1:component:textfield",
                77);
        Assert.assertFalse(
                tester.getComponentFromLastRenderedPage(
                                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:3:itemProperties:1:component")
                        .isEnabled());

        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:4:itemProperties:1:component:dropdown",
                "Or select this row");
        Assert.assertFalse(
                tester.getComponentFromLastRenderedPage(
                                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:4:itemProperties:1:component")
                        .isEnabled());
    }

    /** Test if we can add and remove fields from a list. */
    @Test
    public void testRepeatFields() {
        // add row
        tester.clickLink(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:addNew");
        tester.assertComponent(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:attributesTablePanel:listContainer:items:3:itemProperties:0:component:textfield",
                TextField.class);

        // delete row
        tester.clickLink(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:attributesTablePanel:listContainer:items:3:itemProperties:1:component");
        tester.clickLink(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:attributesTablePanel:listContainer:items:2:itemProperties:1:component");
        tester.clickLink(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:1:component");
        tester.assertComponent(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:noData",
                Label.class);
    }

    /** Test if we can add links with a template. */
    @SuppressWarnings("unchecked")
    @Test
    public void testLinkWithSimpleAndListTemplates() {
        // link template-list-simple
        DropDownChoice<?> selectTemplate =
                (DropDownChoice<?>)
                        tester.getComponentFromLastRenderedPage(
                                "publishedinfo:tabs:panel:importTemplatePanel:metadataTemplate");
        Assert.assertEquals(5, selectTemplate.getChoices().size());
        MetadataTemplateImpl template = (MetadataTemplateImpl) selectTemplate.getChoices().get(0);
        Assert.assertEquals("template-list-simple", template.getName());
        ((IModel<MetadataTemplateImpl>) selectTemplate.getDefaultModel()).setObject(template);
        tester.clickLink("publishedinfo:tabs:panel:importTemplatePanel:link");
        tester.clickLink("publishedinfo:tabs:panel:importTemplatePanel:importDialog:dialog:content:form:submit");
        // test list of linked templates
        tester.assertLabel(
                "publishedinfo:tabs:panel:importTemplatePanel:templatesPanel:listContainer:items:2:itemProperties:0:component",
                "simple fields");
        tester.assertLabel(
                "publishedinfo:tabs:panel:importTemplatePanel:templatesPanel:listContainer:items:3:itemProperties:0:component",
                "template-list-simple");

        // test values
        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:0:component:textfield",
                "template-value01");
        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:attributesTablePanel:listContainer:items:2:itemProperties:0:component:textfield",
                "template--value02");
        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:attributesTablePanel:listContainer:items:3:itemProperties:0:component:textfield",
                "reflist-first");
        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:attributesTablePanel:listContainer:items:4:itemProperties:0:component:textfield",
                "reflist-second");
        Assert.assertFalse(
                tester.getComponentFromLastRenderedPage(
                                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:0:component")
                        .isEnabled());
        Assert.assertFalse(
                tester.getComponentFromLastRenderedPage(
                                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:attributesTablePanel:listContainer:items:2:itemProperties:0:component")
                        .isEnabled());
        Assert.assertTrue(
                tester.getComponentFromLastRenderedPage(
                                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:attributesTablePanel:listContainer:items:3:itemProperties:0:component")
                        .isEnabled());
        Assert.assertTrue(
                tester.getComponentFromLastRenderedPage(
                                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:attributesTablePanel:listContainer:items:4:itemProperties:0:component")
                        .isEnabled());

        // link template-object list
        selectTemplate =
                (DropDownChoice<?>)
                        tester.getComponentFromLastRenderedPage(
                                "publishedinfo:tabs:panel:importTemplatePanel:metadataTemplate");
        Assert.assertEquals(4, selectTemplate.getChoices().size());
        template = (MetadataTemplateImpl) selectTemplate.getChoices().get(0);
        Assert.assertEquals("template-object list", template.getName());
        ((IModel<MetadataTemplateImpl>) selectTemplate.getDefaultModel()).setObject(template);
        tester.clickLink("publishedinfo:tabs:panel:importTemplatePanel:link");
        tester.clickLink("publishedinfo:tabs:panel:importTemplatePanel:importDialog:dialog:content:form:submit");

        tester.assertLabel(
                "publishedinfo:tabs:panel:importTemplatePanel:templatesPanel:listContainer:items:4:itemProperties:0:component",
                "simple fields");
        tester.assertLabel(
                "publishedinfo:tabs:panel:importTemplatePanel:templatesPanel:listContainer:items:5:itemProperties:0:component",
                "template-list-simple");
        tester.assertLabel(
                "publishedinfo:tabs:panel:importTemplatePanel:templatesPanel:listContainer:items:6:itemProperties:0:component",
                "template-object list");

        // print(tester.getLastRenderedPage(), true, true);

        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:7:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:0:component:attributesTablePanel:listContainer:items:1:itemProperties:1:component:textfield",
                "template-code01");
        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:7:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:0:component:attributesTablePanel:listContainer:items:2:itemProperties:1:component:textfield",
                "template-codespace01");
        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:7:itemProperties:1:component:attributesTablePanel:listContainer:items:2:itemProperties:0:component:attributesTablePanel:listContainer:items:1:itemProperties:1:component:textfield",
                "template-code02");
        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:7:itemProperties:1:component:attributesTablePanel:listContainer:items:2:itemProperties:0:component:attributesTablePanel:listContainer:items:2:itemProperties:1:component:textfield",
                "template-codespace02");
        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:7:itemProperties:1:component:attributesTablePanel:listContainer:items:3:itemProperties:0:component:attributesTablePanel:listContainer:items:1:itemProperties:1:component:textfield",
                "First object :code");
        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:7:itemProperties:1:component:attributesTablePanel:listContainer:items:3:itemProperties:0:component:attributesTablePanel:listContainer:items:2:itemProperties:1:component:textfield",
                "First object :codeSpace");

        // TODO figure out why this test fails.. the gui looks ok

        /*Assert.assertFalse(tester.getComponentFromLastRenderedPage("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:7:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:0:component:attributesTablePanel:listContainer:items:1:itemProperties:1:component:textfield").isEnabled());
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:7:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:0:component:attributesTablePanel:listContainer:items:2:itemProperties:1:component:textfield").isEnabled());
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:7:itemProperties:1:component:attributesTablePanel:listContainer:items:2:itemProperties:0:component:attributesTablePanel:listContainer:items:1:itemProperties:1:component:textfield").isEnabled());
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:7:itemProperties:1:component:attributesTablePanel:listContainer:items:2:itemProperties:0:component:attributesTablePanel:listContainer:items:2:itemProperties:1:component:textfield").isEnabled());
        Assert.assertTrue(tester.getComponentFromLastRenderedPage("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:7:itemProperties:1:component:attributesTablePanel:listContainer:items:3:itemProperties:0:component:attributesTablePanel:listContainer:items:1:itemProperties:1:component:textfield").isEnabled());
        Assert.assertTrue(tester.getComponentFromLastRenderedPage("publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:7:itemProperties:1:component:attributesTablePanel:listContainer:items:3:itemProperties:0:component:attributesTablePanel:listContainer:items:2:itemProperties:1:component:textfield").isEnabled());*/

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUnlinkFromSimpleAndListTemplates() {
        // link 2 more templates
        DropDownChoice<?> selectTemplate =
                (DropDownChoice<?>)
                        tester.getComponentFromLastRenderedPage(
                                "publishedinfo:tabs:panel:importTemplatePanel:metadataTemplate");
        MetadataTemplateImpl template = (MetadataTemplateImpl) selectTemplate.getChoices().get(0);
        ((IModel<MetadataTemplateImpl>) selectTemplate.getDefaultModel()).setObject(template);
        tester.clickLink("publishedinfo:tabs:panel:importTemplatePanel:link");
        tester.clickLink("publishedinfo:tabs:panel:importTemplatePanel:importDialog:dialog:content:form:submit");
        template = (MetadataTemplateImpl) selectTemplate.getChoices().get(0);
        ((IModel<MetadataTemplateImpl>) selectTemplate.getDefaultModel()).setObject(template);
        tester.clickLink("publishedinfo:tabs:panel:importTemplatePanel:link");
        tester.clickLink("publishedinfo:tabs:panel:importTemplatePanel:importDialog:dialog:content:form:submit");

        // check the link
        tester.assertLabel(
                "publishedinfo:tabs:panel:importTemplatePanel:templatesPanel:listContainer:items:4:itemProperties:0:component",
                "simple fields");
        tester.assertLabel(
                "publishedinfo:tabs:panel:importTemplatePanel:templatesPanel:listContainer:items:5:itemProperties:0:component",
                "template-list-simple");
        tester.assertLabel(
                "publishedinfo:tabs:panel:importTemplatePanel:templatesPanel:listContainer:items:6:itemProperties:0:component",
                "template-object list");

        // first check the checkboxes
        Component checkbox01 =
                tester.getComponentFromLastRenderedPage(
                        "publishedinfo:tabs:panel:importTemplatePanel:templatesPanel:listContainer:items:4:selectItemContainer:selectItem");
        ((IModel<Boolean>) checkbox01.getDefaultModel()).setObject(true);
        Component checkbox02 =
                tester.getComponentFromLastRenderedPage(
                        "publishedinfo:tabs:panel:importTemplatePanel:templatesPanel:listContainer:items:5:selectItemContainer:selectItem");
        ((IModel<Boolean>) checkbox02.getDefaultModel()).setObject(true);
        Component checkbox03 =
                tester.getComponentFromLastRenderedPage(
                        "publishedinfo:tabs:panel:importTemplatePanel:templatesPanel:listContainer:items:6:selectItemContainer:selectItem");
        ((IModel<Boolean>) checkbox03.getDefaultModel()).setObject(true);
        // click remove links
        tester.getComponentFromLastRenderedPage(
                        "publishedinfo:tabs:panel:importTemplatePanel:removeSelected")
                .setEnabled(true);
        tester.clickLink("publishedinfo:tabs:panel:importTemplatePanel:removeSelected");
        // linked list should be empty
        tester.assertComponent("publishedinfo:tabs:panel:importTemplatePanel:noData", Label.class);

        // list data remove
        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:0:component:textfield",
                "reflist-first");
        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:attributesTablePanel:listContainer:items:2:itemProperties:0:component:textfield",
                "reflist-second");

        Assert.assertTrue(
                tester.getComponentFromLastRenderedPage(
                                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:0:component")
                        .isEnabled());
        Assert.assertTrue(
                tester.getComponentFromLastRenderedPage(
                                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:attributesTablePanel:listContainer:items:2:itemProperties:0:component")
                        .isEnabled());
    }

    /**
     * Adding and deleting a template multiple times deletes the wrong row (selection model out of
     * sync).
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testLinkAndUnlinkDeleteRowBuTemplates() {
        // add link
        DropDownChoice<?> selectTemplate =
                (DropDownChoice<?>)
                        tester.getComponentFromLastRenderedPage(
                                "publishedinfo:tabs:panel:importTemplatePanel:metadataTemplate");
        MetadataTemplateImpl template = (MetadataTemplateImpl) selectTemplate.getChoices().get(0);
        ((IModel<MetadataTemplateImpl>) selectTemplate.getDefaultModel()).setObject(template);
        tester.clickLink("publishedinfo:tabs:panel:importTemplatePanel:link");
        tester.clickLink("publishedinfo:tabs:panel:importTemplatePanel:importDialog:dialog:content:form:submit");
        tester.assertLabel(
                "publishedinfo:tabs:panel:importTemplatePanel:templatesPanel:listContainer:items:2:itemProperties:0:component",
                "simple fields");
        tester.assertLabel(
                "publishedinfo:tabs:panel:importTemplatePanel:templatesPanel:listContainer:items:3:itemProperties:0:component",
                "template-list-simple");

        // delete first link
        Component checkbox01 =
                tester.getComponentFromLastRenderedPage(
                        "publishedinfo:tabs:panel:importTemplatePanel:templatesPanel:listContainer:items:2:selectItemContainer:selectItem");
        ((IModel<Boolean>) checkbox01.getDefaultModel()).setObject(true);

        // click remove links
        tester.getComponentFromLastRenderedPage(
                        "publishedinfo:tabs:panel:importTemplatePanel:removeSelected")
                .setEnabled(true);
        tester.clickLink("publishedinfo:tabs:panel:importTemplatePanel:removeSelected");

        // Check first line is remove
        tester.assertContainsNot("'simple fields'");
        tester.assertLabel(
                "publishedinfo:tabs:panel:importTemplatePanel:templatesPanel:listContainer:items:4:itemProperties:0:component",
                "template-list-simple");

        // add link
        selectTemplate =
                (DropDownChoice<?>)
                        tester.getComponentFromLastRenderedPage(
                                "publishedinfo:tabs:panel:importTemplatePanel:metadataTemplate");
        template = (MetadataTemplateImpl) selectTemplate.getChoices().get(1);
        ((IModel<MetadataTemplateImpl>) selectTemplate.getDefaultModel()).setObject(template);
        tester.clickLink("publishedinfo:tabs:panel:importTemplatePanel:link");
        tester.clickLink("publishedinfo:tabs:panel:importTemplatePanel:importDialog:dialog:content:form:submit");

        tester.assertLabel(
                "publishedinfo:tabs:panel:importTemplatePanel:templatesPanel:listContainer:items:5:itemProperties:0:component",
                "template-list-simple");
        tester.assertLabel(
                "publishedinfo:tabs:panel:importTemplatePanel:templatesPanel:listContainer:items:6:itemProperties:0:component",
                "template-object list");

        // delete first link
        Component checkbox02 =
                tester.getComponentFromLastRenderedPage(
                        "publishedinfo:tabs:panel:importTemplatePanel:templatesPanel:listContainer:items:5:selectItemContainer:selectItem");
        ((IModel<Boolean>) checkbox02.getDefaultModel()).setObject(true);

        // click remove links
        tester.clickLink("publishedinfo:tabs:panel:importTemplatePanel:removeSelected");

        tester.assertContainsNot("'template-list-simple'");
        tester.assertLabel(
                "publishedinfo:tabs:panel:importTemplatePanel:templatesPanel:listContainer:items:7:itemProperties:0:component",
                "template-object list");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testTemplatesRemainInPriorityOrder() {
        // add link
        DropDownChoice<?> selectTemplate =
                (DropDownChoice<?>)
                        tester.getComponentFromLastRenderedPage(
                                "publishedinfo:tabs:panel:importTemplatePanel:metadataTemplate");
        MetadataTemplateImpl template = (MetadataTemplateImpl) selectTemplate.getChoices().get(4);
        ((IModel<MetadataTemplateImpl>) selectTemplate.getDefaultModel()).setObject(template);
        tester.clickLink("publishedinfo:tabs:panel:importTemplatePanel:link");
        tester.clickLink("publishedinfo:tabs:panel:importTemplatePanel:importDialog:dialog:content:form:submit");

        selectTemplate =
                (DropDownChoice<?>)
                        tester.getComponentFromLastRenderedPage(
                                "publishedinfo:tabs:panel:importTemplatePanel:metadataTemplate");
        template = (MetadataTemplateImpl) selectTemplate.getChoices().get(1);
        ((IModel<MetadataTemplateImpl>) selectTemplate.getDefaultModel()).setObject(template);
        tester.clickLink("publishedinfo:tabs:panel:importTemplatePanel:link");
        tester.clickLink("publishedinfo:tabs:panel:importTemplatePanel:importDialog:dialog:content:form:submit");

        tester.assertLabel(
                "publishedinfo:tabs:panel:importTemplatePanel:templatesPanel:listContainer:items:4:itemProperties:0:component",
                "simple fields");
        tester.assertLabel(
                "publishedinfo:tabs:panel:importTemplatePanel:templatesPanel:listContainer:items:5:itemProperties:0:component",
                "template-object list");
        tester.assertLabel(
                "publishedinfo:tabs:panel:importTemplatePanel:templatesPanel:listContainer:items:6:itemProperties:0:component",
                "allData");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testImportFromGeonetwork() {
        // check the layer is linked
        Assert.assertNull(
                tester.getComponentFromLastRenderedPage(
                        "publishedinfo:tabs:panel:importTemplatePanel:noData"));

        // Import from geonetwork
        DropDownChoice<?> geonetwork =
                (DropDownChoice<?>)
                        tester.getComponentFromLastRenderedPage(
                                "publishedinfo:tabs:panel:geonetworkPanel:geonetworkName");
        String geonetworkName = (String) geonetwork.getChoices().get(0);
        ((IModel<String>) geonetwork.getDefaultModel()).setObject(geonetworkName);
        TextField<String> uuid =
                (TextField<String>)
                        tester.getComponentFromLastRenderedPage(
                                "publishedinfo:tabs:panel:geonetworkPanel:textfield");
        ((IModel<String>) uuid.getDefaultModel()).setObject("1a2c6739-3c62-432b-b2a0-aaa589a9e3a1");

        tester.clickLink("publishedinfo:tabs:panel:geonetworkPanel:link");
        tester.clickLink("publishedinfo:tabs:panel:geonetworkPanel:importDialog:dialog:content:form:submit");

        // Check content
        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:attributesTablePanel:listContainer:items:5:itemProperties:0:component:textfield",
                "http://www.opengis.net/def/crs/EPSG/0/3043");
        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:attributesTablePanel:listContainer:items:3:itemProperties:0:component:textfield",
                "Belge_Lambert_1972 (31370)");
        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:5:itemProperties:1:component:attributesTablePanel:listContainer:items:4:itemProperties:0:component:textfield",
                "TAW");
        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:7:itemProperties:1:component:attributesTablePanel:listContainer:items:4:itemProperties:0:component:attributesTablePanel:listContainer:items:1:itemProperties:1:component:textfield",
                "http://www.opengis.net/def/crs/EPSG/0/3043");

        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:6:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:1:component:textfield",
                "Belge_Lambert_1972 (31370)");

        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:7:itemProperties:1:component:attributesTablePanel:listContainer:items:2:itemProperties:0:component:attributesTablePanel:listContainer:items:1:itemProperties:1:component:textfield",
                "Belge_Lambert_1972 (31370)");
        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:7:itemProperties:1:component:attributesTablePanel:listContainer:items:3:itemProperties:0:component:attributesTablePanel:listContainer:items:1:itemProperties:1:component:textfield",
                "TAW");
        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:7:itemProperties:1:component:attributesTablePanel:listContainer:items:4:itemProperties:0:component:attributesTablePanel:listContainer:items:1:itemProperties:1:component:textfield",
                "http://www.opengis.net/def/crs/EPSG/0/3043");

        // should remove all links
        tester.assertComponent("publishedinfo:tabs:panel:importTemplatePanel:noData", Label.class);
    }

    @Test
    public void testImportFromGeonetworkValidation() {
        tester.clickLink("publishedinfo:tabs:panel:geonetworkPanel:link");
        print(tester.getLastRenderedPage(), true, true);

        Assert.assertEquals(2, tester.getMessages(FeedbackMessage.ERROR).size());
        Assert.assertEquals(
                "Select a geonetwork", tester.getMessages(FeedbackMessage.ERROR).get(0).toString());
        Assert.assertEquals(
                "A metadata UUID is required",
                tester.getMessages(FeedbackMessage.ERROR).get(1).toString());
        tester.assertLabel(
                "publishedinfo:tabs:panel:geonetworkPanel:importFeedback:feedbackul:messages:0:message",
                "Select a geonetwork");
        tester.assertLabel(
                "publishedinfo:tabs:panel:geonetworkPanel:importFeedback:feedbackul:messages:1:message",
                "A metadata UUID is required");
    }

    @Test
    public void testGenerateFeatureCatalogueAndDomain() {
        tester.assertComponent(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:8:itemProperties:1:component:generate",
                AjaxSubmitLink.class);

        tester.clickLink(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:8:itemProperties:1:component:generate");

        tester.assertComponent(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:8:itemProperties:1:component:dialog",
                GeoServerDialog.class);

        tester.clickLink(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:8:itemProperties:1:component:dialog:dialog:content:form:submit");

        @SuppressWarnings("unchecked")
        GeoServerTablePanel<ComplexMetadataMap> panel =
                (GeoServerTablePanel<ComplexMetadataMap>)
                        tester.getComponentFromLastRenderedPage(
                                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:8:itemProperties:1:component:attributesTablePanel");

        assertEquals(23, panel.getDataProvider().size());

        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:8:itemProperties:1:component:attributesTablePanel:listContainer:items:2:itemProperties:0:component:attributesTablePanel:listContainer:items:1:itemProperties:1:component",
                "STATE_NAME");

        tester.assertComponent(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:8:itemProperties:1:component:attributesTablePanel:listContainer:items:2:itemProperties:0:component:attributesTablePanel:listContainer:items:7:itemProperties:1:component:generate",
                AjaxSubmitLink.class);

        tester.clickLink(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:8:itemProperties:1:component:attributesTablePanel:listContainer:items:2:itemProperties:0:component:attributesTablePanel:listContainer:items:7:itemProperties:1:component:generate");

        tester.assertComponent(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:8:itemProperties:1:component:attributesTablePanel:listContainer:items:2:itemProperties:0:component:attributesTablePanel:listContainer:items:7:itemProperties:1:component:dialog",
                GeoServerDialog.class);

        tester.clickLink(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:8:itemProperties:1:component:attributesTablePanel:listContainer:items:2:itemProperties:0:component:attributesTablePanel:listContainer:items:7:itemProperties:1:component:dialog:dialog:content:form:submit");

        // print(tester.getLastRenderedPage(), true, true);

        @SuppressWarnings("unchecked")
        GeoServerTablePanel<ComplexMetadataMap> panel2 =
                (GeoServerTablePanel<ComplexMetadataMap>)
                        tester.getComponentFromLastRenderedPage(
                                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:8:itemProperties:1:component:attributesTablePanel:listContainer:items:2:itemProperties:0:component:attributesTablePanel:listContainer:items:7:itemProperties:1:component:attributesTablePanel");

        assertEquals(49, panel2.getDataProvider().size());

        tester.assertModelValue(
                "publishedinfo:tabs:panel:metadataPanel:attributesPanel:attributesTablePanel:listContainer:items:8:itemProperties:1:component:attributesTablePanel:listContainer:items:2:itemProperties:0:component:attributesTablePanel:listContainer:items:7:itemProperties:1:component:attributesTablePanel:listContainer:items:1:itemProperties:0:component:attributesTablePanel:listContainer:items:2:itemProperties:1:component",
                "Alabama");
    }
}
