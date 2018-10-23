/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service;

import org.geoserver.metadata.data.model.ComplexMetadataMap;

import java.util.List;

/**
 * @author Timothy De Bock
 */
public interface ComplexMetadataService {

    /**
     * The values in the template are applied in reverse order, i.e. the first child has the highest priority.
     * @param parent
     * @param children
     */
    void merge(ComplexMetadataMap destination, List<ComplexMetadataMap> sources);
    
    /**
     * The values in the template are applied in reverse order, i.e. the first child has the highest priority.
     * @param destination
     * @param source
     */
    void merge(ComplexMetadataMap destination, ComplexMetadataMap source,
            String typeName);

}
