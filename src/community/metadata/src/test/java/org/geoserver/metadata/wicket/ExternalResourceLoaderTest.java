/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.wicket;

import org.apache.wicket.Session;
import org.apache.wicket.util.file.File;
import org.geoserver.metadata.AbstractWicketMetadataTest;
import org.geoserver.metadata.web.MetadataTemplatesPage;
import org.geoserver.metadata.web.resource.WicketFileResourceLoader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Locale;


/**
 * Test the ExternalResourceLoader.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class ExternalResourceLoaderTest  extends AbstractWicketMetadataTest {

    @Before
    public void before() throws IOException {
        //Load the page
        MetadataTemplatesPage page = new MetadataTemplatesPage();
        tester.startPage(page);
        tester.assertRenderedPage(MetadataTemplatesPage.class);
    }

    @Test
    public void testExternalResourceLoader() throws IOException {
        File metadata = new File(DATA_DIRECTORY.getDataDirectoryRoot(), "metadata");
        WicketFileResourceLoader loader = new WicketFileResourceLoader(metadata.toString(), "metadata.properties");

        String actual = loader.loadStringResource(tester.getLastRenderedPage(), "metadata.generated.form.indentifier-single");
        Assert.assertEquals("Indentifier single field", actual);

        Session.get().setLocale(new Locale("nl"));
        actual = loader.loadStringResource(tester.getLastRenderedPage(), "metadata.generated.form.indentifier-single");
        Assert.assertEquals("Indentifier single field", actual);
    }

    @Test
    public void testExternalResourceLoaderDutch() throws IOException {
        Session.get().setLocale(new Locale("nl"));
        File metadata = new File(DATA_DIRECTORY.getDataDirectoryRoot(), "metadata");
        WicketFileResourceLoader loader = new WicketFileResourceLoader(metadata.toString(), "metadata.properties");

        String actual = loader.loadStringResource(tester.getLastRenderedPage(), "metadata.generated.form.number-field");
        Assert.assertEquals("Getal veld", actual);
    }
}
