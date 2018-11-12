/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geoserver.catalog.AttributeTypeInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.geoserver.metadata.data.service.ComplexAttributeGenerator;
import org.geoserver.metadata.data.service.ComplexMetadataService;
import org.geoserver.metadata.web.layer.MetadataTabPanel;
import org.geoserver.web.GeoServerApplication;
import org.geotools.util.logging.Logging;
import org.springframework.stereotype.Component;

@Component
public class FeatureCatalogGenerator implements ComplexAttributeGenerator {

    private static final long serialVersionUID = 3179273148205046941L;

    private static final Logger LOGGER = Logging.getLogger(MetadataTabPanel.class);

    @Override
    public String getType() {
        return MetadataConstants.FEATURE_CATALOG_TYPENAME;
    }

    @Override
    public void generate(ComplexMetadataMap metadata, LayerInfo layerInfo) {
        ComplexMetadataService service =
                GeoServerApplication.get().getApplicationContext().getBean(ComplexMetadataService.class);
        
        FeatureTypeInfo fti = (FeatureTypeInfo) layerInfo.getResource();
        
        @SuppressWarnings("unchecked")
        HashMap<String, List<Integer>> derivedAtts = 
                (HashMap<String, List<Integer>>) fti.getMetadata().get(MetadataConstants.DERIVED_KEY);
        
        //we will save the old details for attributes that still exist
        Map<String, ComplexMetadataMap> old = new HashMap<>();
        for(int i = 0; i < metadata.size(MetadataConstants.FEATURE_CATALOG); i++) {
            ComplexMetadataMap attMap 
                = metadata.subMap(MetadataConstants.FEATURE_CATALOG, i);            
            old.put(attMap.get(String.class, MetadataConstants.FEATURE_CATALOG_ATT_NAME).getValue(),
                    attMap.clone());
        }
        
        //clear everything and build again
        metadata.delete(MetadataConstants.FEATURE_CATALOG);
        int index = 0;
        try {
            for (AttributeTypeInfo att : fti.attributes()) {
                ComplexMetadataMap attMap 
                    = metadata.subMap(MetadataConstants.FEATURE_CATALOG, index++);
                
                ComplexMetadataMap oldMap = old.get(att.getName());
                if (oldMap != null) {
                    service.merge(attMap, oldMap, MetadataConstants.FEATURE_CATALOG_TYPENAME, derivedAtts);
                }
                
                attMap.get(String.class, MetadataConstants.FEATURE_CATALOG_ATT_NAME).setValue(
                        att.getName());
                if (att.getBinding() != null) {
                    String type = MetadataConstants.FEATURECATALOG_TYPE_UNKNOWN;
                    for (Class<?> clazz : MetadataConstants.FEATURE_CATALOG_KNOWN_TYPES) {
                        if (clazz.isAssignableFrom(att.getBinding())) {
                            type = clazz.getSimpleName();
                            break;
                        }
                    }
                    attMap.get(String.class, MetadataConstants.FEATURE_CATALOG_ATT_TYPE)
                        .setValue(type);
                }
                if (att.getLength() != null) {
                    attMap.get(Integer.class, MetadataConstants.FEATURE_CATALOG_ATT_TYPE).setValue(
                        att.getLength());      
                }
                attMap.get(Integer.class, MetadataConstants.FEATURE_CATALOG_ATT_MIN_OCCURENCE).setValue(
                        att.getMinOccurs());
                attMap.get(Integer.class, MetadataConstants.FEATURE_CATALOG_ATT_MAX_OCCURENCE).setValue(
                        att.getMaxOccurs());
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Could not read attributes for " + fti.getName(), 
                    e);
        }
    }
    
    @Override
    public boolean supports(ComplexMetadataMap metadata, LayerInfo layerInfo) { 
        return layerInfo.getResource() instanceof FeatureTypeInfo;
    }

}
