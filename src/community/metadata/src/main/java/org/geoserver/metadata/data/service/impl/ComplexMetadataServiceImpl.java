/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service.impl;


import org.geoserver.metadata.data.dto.MetadataAttributeTypeConfiguration;
import org.geoserver.metadata.data.dto.MetadataAttributeConfiguration;
import org.geoserver.metadata.data.dto.MetadataEditorConfiguration;
import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.geoserver.metadata.data.service.ComplexMetadataService;
import org.geoserver.metadata.data.service.YamlService;
import org.geotools.util.logging.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

@Repository
public class ComplexMetadataServiceImpl implements ComplexMetadataService {


    private static final Logger LOGGER = Logging.getLogger(ComplexMetadataServiceImpl.class);

    @Autowired
    YamlService yamlService;

    @Override
    public void merge(ComplexMetadataMap destination, List<ComplexMetadataMap> sources) {
        MetadataEditorConfiguration config;
        try {
            config = yamlService.readConfiguration();
        } catch (IOException e) {
            //TODO welke fout gooien?
            throw new IllegalStateException("Metadata could not be merge." +
                    "The corresponding gui configuration cannot be read.");
        }

        //TODO first clear stuff
        ArrayList<ComplexMetadataMap> reversed = new ArrayList<ComplexMetadataMap>(sources);
        Collections.reverse(reversed);
        for (ComplexMetadataMap source : reversed) {
            mergeAttribute(destination, source, config.getAttributes(), config);
        }

    }
    
    @Override
    public void merge(ComplexMetadataMap destination, ComplexMetadataMap source,
            String typeName) {
        MetadataEditorConfiguration config;
        try {
            config = yamlService.readConfiguration();
        } catch (IOException e) {
            // TODO welke fout gooien?
            throw new IllegalStateException("Metadata could not be merge."
                    + "The corresponding gui configuration cannot be read.");
        }
        mergeAttribute(destination, source, 
                typeName == null ? config.getAttributes()
                        : config.findType(typeName).getAttributes(), 
                config);
    }

    private void mergeAttribute(ComplexMetadataMap parent, ComplexMetadataMap child,
                                List<MetadataAttributeConfiguration> attributes,
                                MetadataEditorConfiguration config) {
        for (MetadataAttributeConfiguration attribute : attributes) {
            switch (attribute.getFieldType()) {
                case TEXT:
                    mergeSimpleField(attribute, parent, child);
                    break;
                case NUMBER:
                    mergeSimpleField(attribute, parent, child);
                    break;
                case DROPDOWN:
                    mergeSimpleField(attribute, parent, child);
                    break;
                case COMPLEX:
                    mergeComplexField(attribute, config.findType(attribute.getTypename()), 
                            config, parent, child);
                    break;
            }
        }
    }

    private void mergeSimpleField(MetadataAttributeConfiguration attribute,
                                  ComplexMetadataMap parent,
                                  ComplexMetadataMap child) {
        switch (attribute.getOccurrence()) {
            case SINGLE:
                String childValue = child.get(String.class, attribute.getKey()).getValue();
                if (childValue != null) {
                    parent.get(String.class, attribute.getKey()).setValue(childValue);
                }
                break;
            case REPEAT:
                for (int i = 0; i < child.size(attribute.getKey()); i++) {
                    childValue = child.get(String.class, attribute.getKey(), i).getValue();
                    if (childValue != null) {
                        int index = parent.size(attribute.getKey());
                        parent.get(String.class, attribute.getKey(), index).setValue(childValue);
                    }
                }
                break;
        }
    }

    private void mergeComplexField(MetadataAttributeConfiguration attribute,
                                   MetadataAttributeTypeConfiguration type,
                                   MetadataEditorConfiguration config,
                                   ComplexMetadataMap parent, ComplexMetadataMap child) {
        switch (attribute.getOccurrence()) {
            case SINGLE:
                if (child.size(attribute.getKey()) > 0) {
                    ComplexMetadataMap childMap = child.subMap(attribute.getKey());
                    ComplexMetadataMap parentMap = parent.subMap(attribute.getKey());
                    mergeAttribute(parentMap, childMap, type.getAttributes(), config);
                }
                break;
            case REPEAT:
                for (int i = 0; i < child.size(attribute.getKey()); i++) {
                    ComplexMetadataMap childMap = child.subMap(attribute.getKey(), i);
                    int index = parent.size(attribute.getKey());
                    ComplexMetadataMap parentMap = parent.subMap(attribute.getKey(), index);

                    mergeAttribute(parentMap, childMap, type.getAttributes(), config);
                }
                break;
        }

    }

}
