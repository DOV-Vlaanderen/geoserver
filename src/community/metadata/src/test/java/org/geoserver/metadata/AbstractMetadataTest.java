/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata;

import org.apache.wicket.util.file.File;
import org.geoserver.config.GeoServer;
import org.geoserver.data.test.MockData;
import org.geoserver.util.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract test class.
 * 
 * @author Niels Charlier
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/applicationContext.xml", "classpath*:/applicationSecurityContext.xml"})
@WebAppConfiguration //we need web app context to have data directory set.
public abstract class AbstractMetadataTest {
    
    protected static MockData DATA_DIRECTORY;

    private static File metadata;

    @Autowired
    protected GeoServer geoServer;

    @BeforeClass
    public static void init() throws Exception {
        if (DATA_DIRECTORY == null) {
            //set data directory
            DATA_DIRECTORY = new MockData();
            System.setProperty("GEOSERVER_DATA_DIR", 
                    DATA_DIRECTORY.getDataDirectoryRoot().toString());
            
            //copy test files to data directory
            metadata = new File(DATA_DIRECTORY.getDataDirectoryRoot(), "metadata");
            metadata.mkdirs();
            IOUtils.copy(AbstractMetadataTest.class.getResourceAsStream("fouteinhoud.yaml"),
                    new File(metadata, "fouteinhoud.yaml"));
            IOUtils.copy(AbstractMetadataTest.class.getResourceAsStream("metadata-geonetwork.yaml"), 
                    new File(metadata, "metadata-geonetwork.yaml"));
            IOUtils.copy(AbstractMetadataTest.class.getResourceAsStream("metadata-mapping.yaml"), 
                    new File(metadata, "metadata-mapping.yaml"));
            IOUtils.copy(AbstractMetadataTest.class.getResourceAsStream("metadata-ui.yaml"), 
                    new File(metadata, "metadata-ui.yaml"));
            IOUtils.copy(AbstractMetadataTest.class.getResourceAsStream("metadata-ui.properties"),
                    new File(metadata, "metadata-ui.properties"));
            IOUtils.copy(AbstractMetadataTest.class.getResourceAsStream("metadata-ui-extra.properties"),
                    new File(metadata, "metadata-ui-extra.properties"));

            IOUtils.copy(AbstractMetadataTest.class.getResourceAsStream("templates.xml"),
                    new File(metadata, "templates.xml"));
            IOUtils.copy(AbstractMetadataTest.class.getResourceAsStream("geonetwork-1a2c6739-3c62-432b-b2a0-aaa589a9e3a1.xml"),
                    new File(metadata, "geonetwork-1a2c6739-3c62-432b-b2a0-aaa589a9e3a1.xml"));


            File target = new File(DATA_DIRECTORY.getDataDirectoryRoot());
            URL sourceUrl = AbstractMetadataTest.class.getClassLoader().getResource("datadir_layers");
            File sourceDir = new File(sourceUrl.toURI());

            IOUtils.deepCopy(sourceDir, target);
        }
    }
    
    protected boolean setupDataDirectory() throws Exception {
        return false;
    }
    
    @Before
    public final void setupAndLoadDataDirectory() throws Exception {        
        if (setupDataDirectory()) {
            DATA_DIRECTORY.setUp();
            geoServer.reload();
        }
    }
    
    /**
     * Sets up the authentication context for the test.
     * <p>
     * This context lasts only for a single test case, it is cleared after every test has completed. 
     * </p>
     * @param username The username.
     * @param password The password.
     * @param roles Roles to assign.
     */
    protected void login(String username, String password, String... roles) {
        SecurityContextHolder.setContext(new SecurityContextImpl());
        List<GrantedAuthority> l= new ArrayList<GrantedAuthority>();
        for (String role : roles) {
            l.add(new SimpleGrantedAuthority(role));
        }

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(username,password,l));
    }

    protected void restoreTemplates() throws IOException {
        IOUtils.copy(AbstractMetadataTest.class.getResourceAsStream("templates.xml"),
                new File(metadata, "templates.xml"));
    }

    protected void restoreLayers() throws IOException {
        File target = new File(DATA_DIRECTORY.getDataDirectoryRoot());
        URL sourceUrl = AbstractMetadataTest.class.getClassLoader().getResource("datadir_layers");
        File sourceDir = null;
        try {
            sourceDir = new File(sourceUrl.toURI());
            IOUtils.deepCopy(sourceDir, target);
            geoServer.reload();
        } catch (URISyntaxException e) {
            throw new IOException(e);
        } catch (Exception e) {
            throw new IOException(e);
        }

    }
}
