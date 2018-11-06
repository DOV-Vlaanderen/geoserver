/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.mock;


import org.geoserver.config.GeoServerDataDirectory;
import org.geoserver.metadata.data.service.RemoteDocumentReader;
import org.geoserver.platform.resource.Resource;
import org.geotools.util.logging.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Primary
@Repository
public class LocalDocumentReaderMock implements RemoteDocumentReader {

    private static final java.util.logging.Logger LOGGER = Logging.getLogger(LocalDocumentReaderMock.class);

    @Autowired
    private GeoServerDataDirectory dataDirectory;

    @Override
    public Document readDocument(URL url) throws IOException {
        String uuid = url.toString().split("uuid=")[1];
        for (Resource resource : dataDirectory.get("metadata").list()) {
            if (resource.name().contains(uuid)) {
                try {
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    dbf.setNamespaceAware(true);
                    InputStream stream = resource.in();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document doc = db.parse(stream);
                    doc.getDocumentElement().normalize();
                    return doc;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }

            }
        }
        throw new IOException("Resource not found: " + url);
    }



}
