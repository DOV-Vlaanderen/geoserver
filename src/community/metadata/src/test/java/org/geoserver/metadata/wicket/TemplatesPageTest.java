/* (c) 2017-2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.wicket;

import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.geoserver.metadata.AbstractWicketMetadataTest;
import org.geoserver.metadata.web.MetadataTemplatePage;
import org.geoserver.metadata.web.MetadataTemplatesPage;
import org.geoserver.metadata.web.panel.MetadataPanel;
import org.geoserver.web.wicket.GeoServerTablePanel;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Test templates page.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class TemplatesPageTest extends AbstractWicketMetadataTest {

    @Before
    public void before() throws IOException {
        restoreTemplates();
        //Load the page
        MetadataTemplatesPage page = new MetadataTemplatesPage();
        tester.startPage(page);
        tester.assertRenderedPage(MetadataTemplatesPage.class);
    }


    @Test
    public void testPage() {

        print(tester.getLastRenderedPage(), true, true);

        tester.assertComponent("addNew", AjaxLink.class);
        tester.assertComponent("removeSelected", AjaxLink.class);
        tester.assertComponent("templatesPanel", GeoServerTablePanel.class);
        //Check content of the table
        //first row
        tester.assertLabel("templatesPanel:listContainer:items:1:itemProperties:0:component:link:label", "simple fields");
        tester.assertLabel("templatesPanel:listContainer:items:1:itemProperties:1:component", "Only basic fields");
        tester.assertLabel("templatesPanel:listContainer:items:1:itemProperties:2:component", "10");
        //other rows
        tester.assertLabel("templatesPanel:listContainer:items:2:itemProperties:0:component:link:label", "template-list-simple");
        tester.assertLabel("templatesPanel:listContainer:items:3:itemProperties:0:component:link:label", "template-object list");
        tester.assertLabel("templatesPanel:listContainer:items:4:itemProperties:0:component:link:label", "object-field");
        tester.assertLabel("templatesPanel:listContainer:items:5:itemProperties:0:component:link:label", "template-nested-object");
        //lastrow
        tester.assertLabel("templatesPanel:listContainer:items:6:itemProperties:0:component:link:label", "allData");
        tester.assertLabel("templatesPanel:listContainer:items:6:itemProperties:1:component", "All fields");
        tester.assertLabel("templatesPanel:listContainer:items:6:itemProperties:2:component", "0");

    }


    @Test
    public void testNewNavigation() throws Exception {


        tester.clickLink("addNew");

        tester.assertRenderedPage(MetadataTemplatePage.class);
        print(tester.getLastRenderedPage(), true, true);

        tester.assertComponent("form:name", TextField.class);
        tester.assertComponent("form:description", TextField.class);
        tester.assertComponent("form:priority", NumberTextField.class);

        tester.assertModelValue("form:priority", 0);

        tester.assertComponent("form:metadataTemplatePanel", MetadataPanel.class);
    }

    @Test
    public void testEditNavigation() throws Exception {

        //Navigate to first template
        tester.clickLink("templatesPanel:listContainer:items:1:itemProperties:0:component:link");

        tester.assertRenderedPage(MetadataTemplatePage.class);
        print(tester.getLastRenderedPage(), true, true);

        tester.assertComponent("form:name", TextField.class);
        tester.assertComponent("form:description", TextField.class);
        tester.assertComponent("form:priority", NumberTextField.class);

        tester.assertModelValue("form:name", "simple fields");
        tester.assertModelValue("form:description", "Only basic fields");
        tester.assertModelValue("form:priority", 10);

        tester.assertComponent("form:metadataTemplatePanel", MetadataPanel.class);
    }

    @Test
    public void testDelete() throws Exception {
        //select first template
        ((IModel<Boolean>) tester.getComponentFromLastRenderedPage("templatesPanel:listContainer:items:1:selectItemContainer:selectItem").getDefaultModel()).setObject(true);
        //select third templete
        ((IModel<Boolean>) tester.getComponentFromLastRenderedPage("templatesPanel:listContainer:items:3:selectItemContainer:selectItem").getDefaultModel()).setObject(true);
        //delete
        tester.getComponentFromLastRenderedPage("removeSelected").setEnabled(true);
        tester.clickLink("removeSelected");

        print(tester.getLastRenderedPage(), true, true);

        //Check update content of the table
        tester.assertLabel("templatesPanel:listContainer:items:7:itemProperties:0:component:link:label", "template-list-simple");
        tester.assertLabel("templatesPanel:listContainer:items:8:itemProperties:0:component:link:label", "object-field");
        tester.assertLabel("templatesPanel:listContainer:items:9:itemProperties:0:component:link:label", "template-nested-object");
        tester.assertLabel("templatesPanel:listContainer:items:10:itemProperties:0:component:link:label", "allData");

    }


}
