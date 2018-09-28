// Copyright (C) 2010-2011 DOV, http://dov.vlaanderen.be/
// All rights reserved
package org.geoserver.metadata.data.service;

import org.geoserver.catalog.MetadataMap;
import org.geoserver.metadata.data.dto.MetadataEditorConfiguration;

import java.io.IOException;

/**
 * Import the metadata from a geonetwork server.
 *
 * @author Timothy De Bock
 */
@SuppressWarnings("UnusedDeclaration")
public interface ImportGeonetworkMetadataService {

    MetadataMap importMetadata(String url, MetadataMap metadataMap) throws IOException;


}
