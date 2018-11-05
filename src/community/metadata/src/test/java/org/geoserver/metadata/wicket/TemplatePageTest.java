/* (c) 2017-2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.wicket;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.geoserver.metadata.AbstractWicketMetadataTest;
import org.geoserver.metadata.data.model.MetadataTemplate;
import org.geoserver.metadata.data.service.MetadataTemplateService;
import org.geoserver.metadata.web.MetadataTemplatePage;
import org.geoserver.metadata.web.MetadataTemplatesPage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * Test template page.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class TemplatePageTest extends AbstractWicketMetadataTest {

    @Autowired

    private MetadataTemplateService service;


    @Before
    public void before() throws IOException {
        restoreTemplates();
        //Load the page
        MetadataTemplate allData = service.load("allData");
        MetadataTemplatePage page = new MetadataTemplatePage(new Model<>(allData));
        tester.startPage(page);
        tester.assertRenderedPage(MetadataTemplatePage.class);
    }


    @Test
    public void testPage() {
        print(tester.getLastRenderedPage(), true, true);

        Assert.assertEquals(false, tester.getComponentFromLastRenderedPage("form:name").isEnabled());
        tester.assertModelValue("form:name", "allData");
        tester.assertModelValue("form:description", "All fields");
        tester.assertModelValue("form:priority", 0);

    }


    @Test
    public void tesPageSave() throws IOException {
        tester.assertModelValue("form:description", "All fields");
        tester.assertModelValue("form:priority", 0);

        ((IModel<String>) tester.getComponentFromLastRenderedPage("form:description").getDefaultModel()).setObject("description update");
        ((IModel<Integer>) tester.getComponentFromLastRenderedPage("form:priority").getDefaultModel()).setObject(5);

        tester.assertModelValue("form:description", "description update");
        tester.assertModelValue("form:priority", 5);

        tester.clickLink("form:save");

        MetadataTemplate template = service.load("allData");
        Assert.assertEquals("description update", template.getDescription());
        Assert.assertEquals(new Integer(5), template.getPriority());

        tester.assertRenderedPage(MetadataTemplatesPage.class);

    }

    @Test
    public void tesPageCancel() throws IOException {
        tester.assertModelValue("form:description", "All fields");
        tester.assertModelValue("form:priority", 0);

        ((IModel<String>) tester.getComponentFromLastRenderedPage("form:description").getDefaultModel()).setObject("description update");
        ((IModel<Integer>) tester.getComponentFromLastRenderedPage("form:priority").getDefaultModel()).setObject(5);

        tester.assertModelValue("form:description", "description update");
        tester.assertModelValue("form:priority", 5);

        tester.clickLink("form:cancel");

        MetadataTemplate template = service.load("allData");
        Assert.assertEquals("All fields", template.getDescription());
        Assert.assertEquals(new Integer(0), template.getPriority());

        tester.assertRenderedPage(MetadataTemplatesPage.class);
    }

    @Test
    public void tesPageValidation() throws IOException {
        //Load the page
        MetadataTemplatePage page = new MetadataTemplatePage();
        tester.startPage(page);
        tester.assertRenderedPage(MetadataTemplatePage.class);

        tester.assertModelValue("form:name", null);

        tester.clickLink("form:save");

        Assert.assertEquals(1, tester.getMessages(FeedbackMessage.ERROR).size());
        Assert.assertEquals("Field 'Name' is required.", tester.getMessages(FeedbackMessage.ERROR).get(0).toString());
        tester.assertLabel("feedback:feedbackul:messages:0:message", "Field &#039;Name&#039; is required.");

        tester.assertRenderedPage(MetadataTemplatePage.class);

    }


}
