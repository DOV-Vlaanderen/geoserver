/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service;

import java.io.Serializable;

import org.geoserver.catalog.LayerInfo;
import org.geoserver.metadata.data.model.ComplexMetadataMap;

public interface ComplexAttributeGenerator extends Serializable {
    
    String getType();
    
    void generate(ComplexMetadataMap metadata, LayerInfo layerInfo);

    default boolean supports(ComplexMetadataMap metadata, LayerInfo layerInfo) {
        return true;
    }
    

}
