/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service;

import org.geoserver.metadata.data.ComplexMetadataMap;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.Serializable;

public interface GeonetworkXmlParser extends Serializable {

    void parseMetadata(Document doc, ComplexMetadataMap metadataMap) throws IOException;

}
