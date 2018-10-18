/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service;

import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.geoserver.metadata.data.model.MetadataTemplate;

import java.io.IOException;
import java.util.List;

/**
 * @author Timothy De Bock
 */
public interface ComplexMetadataService {


    void merge(ComplexMetadataMap parent, ComplexMetadataMap child);

}