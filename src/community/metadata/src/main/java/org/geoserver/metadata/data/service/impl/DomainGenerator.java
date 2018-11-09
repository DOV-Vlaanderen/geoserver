/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.geoserver.metadata.data.service.ComplexAttributeGenerator;
import org.geoserver.metadata.web.layer.MetadataTabPanel;
import org.geotools.data.Query;
import org.geotools.feature.visitor.UniqueVisitor;
import org.geotools.util.Converters;
import org.geotools.util.logging.Logging;
import org.opengis.filter.Filter;
import org.springframework.stereotype.Component;

@Component
public class DomainGenerator implements ComplexAttributeGenerator {

    private static final long serialVersionUID = 3179273148205046941L;

    private static final Logger LOGGER = Logging.getLogger(MetadataTabPanel.class);

    @Override
    public String getType() {
        return MetadataConstants.DOMAIN_TYPENAME;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void generate(ComplexMetadataMap metadata, LayerInfo layerInfo) {        
        String attName = metadata.get(String.class, 
                MetadataConstants.FEATURE_CATALOG_ATT_NAME).getValue();

        FeatureTypeInfo fti = (FeatureTypeInfo) layerInfo.getResource();
                
        //clear everything and build again
        metadata.delete(MetadataConstants.FEATURE_CATALOG);
        try {
            Query query = new Query(fti.getName());
            query.setPropertyNames(Arrays.asList(attName));
            final UniqueVisitor visitor = new UniqueVisitor(attName);
            
            fti.getFeatureSource(null, null)
                    .getFeatures(Filter.INCLUDE)
                    .accepts(visitor, null);

            int index = 0;
            for (Object value : new TreeSet<Object>(visitor.getUnique())) {
                ComplexMetadataMap domainMap = metadata.subMap(MetadataConstants.FEATURE_CATALOG_ATT_DOMAIN, index++);
                domainMap.get(String.class, MetadataConstants.DOMAIN_ATT_VALUE)
                    .setValue(Converters.convert(value, String.class));
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to retrieve domain for " + fti.getName(), e);
        }        
    }
    
    @Override
    public boolean supports(ComplexMetadataMap metadata, LayerInfo layerInfo) { 
        return layerInfo.getResource() instanceof FeatureTypeInfo;
    }

}
