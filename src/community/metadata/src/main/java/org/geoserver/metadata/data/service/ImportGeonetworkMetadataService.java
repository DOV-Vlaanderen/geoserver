/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service;

import org.geoserver.catalog.MetadataMap;
import org.geoserver.metadata.data.ComplexMetadataMap;

import java.io.IOException;

/**
 * Import the metadata from a geonetwork server.
 *
 * @author Timothy De Bock
 */
public interface ImportGeonetworkMetadataService {

    void importMetadata(String url, ComplexMetadataMap metadataMap) throws IOException;


}
