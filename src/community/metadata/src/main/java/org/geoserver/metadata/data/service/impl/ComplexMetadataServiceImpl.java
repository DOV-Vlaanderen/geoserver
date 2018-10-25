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
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * Implementation.
 * TODO insert templates values before user values in the list!(this way indexes are constant)
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
@Repository
public class ComplexMetadataServiceImpl implements ComplexMetadataService {


    private static final Logger LOGGER = Logging.getLogger(ComplexMetadataServiceImpl.class);

    @Autowired
    YamlService yamlService;

    @Override
    public void merge(ComplexMetadataMap destination,
                      List<ComplexMetadataMap> sources,
                      HashMap<String, List<Integer>> descriptionMap) {

        MetadataEditorConfiguration config;
        try {
            config = yamlService.readConfiguration();
        } catch (IOException e) {
            //TODO welke fout gooien?
            throw new IllegalStateException("Metadata could not be merge." +
                    "The corresponding gui configuration cannot be read.");
        }

        clearTemplateData(destination, descriptionMap);

        ArrayList<ComplexMetadataMap> reversed = new ArrayList<ComplexMetadataMap>(sources);
        Collections.reverse(reversed);
        for (ComplexMetadataMap source : reversed) {
            mergeAttribute(destination, source, config.getAttributes(), config, descriptionMap);
        }

    }


    @Override
    public void merge(ComplexMetadataMap destination,
                      ComplexMetadataMap source,
                      String typeName,
                      HashMap<String, List<Integer>> descriptionMap) {

        MetadataEditorConfiguration config;
        try {
            config = yamlService.readConfiguration();
        } catch (IOException e) {
            // TODO welke fout gooien?
            throw new IllegalStateException("Metadata could not be merge."
                    + "The corresponding gui configuration cannot be read.");
        }

        clearTemplateData(destination, descriptionMap);

        mergeAttribute(destination, source,
                typeName == null ? config.getAttributes()
                        : config.findType(typeName).getAttributes(),
                config, new HashMap<>());
    }

    private void mergeAttribute(ComplexMetadataMap parent, ComplexMetadataMap child,
                                List<MetadataAttributeConfiguration> attributes,
                                MetadataEditorConfiguration config, HashMap<String, List<Integer>> descriptionMap) {
        for (MetadataAttributeConfiguration attribute : attributes) {
            List<Integer> indexes = new ArrayList<>();
            switch (attribute.getFieldType()) {
                case TEXT:
                    indexes = mergeSimpleField(attribute, parent, child);
                    break;
                case NUMBER:
                    indexes = mergeSimpleField(attribute, parent, child);
                    break;
                case DROPDOWN:
                    indexes = mergeSimpleField(attribute, parent, child);
                    break;
                case COMPLEX:
                    indexes = mergeComplexField(attribute, config.findType(attribute.getTypename()),
                            config, parent, child);
                    break;
            }
            //keep track of the values that are from the template
            if (descriptionMap != null) {
                if (!descriptionMap.containsKey(attribute.getKey())) {
                    descriptionMap.put(attribute.getKey(), new ArrayList<>());
                }
                descriptionMap.get(attribute.getKey()).addAll(indexes);
            }
        }
    }

    private  List<Integer> mergeSimpleField(MetadataAttributeConfiguration attribute,
                                  ComplexMetadataMap parent,
                                  ComplexMetadataMap child) {
        switch (attribute.getOccurrence()) {
            case SINGLE:
                String childValue = child.get(String.class, attribute.getKey()).getValue();
                if (childValue != null) {
                    parent.get(String.class, attribute.getKey()).setValue(childValue);
                    ArrayList<Integer> indexes = new ArrayList<>();
                    indexes.add(0);
                    return indexes;
                }
                break;
            case REPEAT:
                ArrayList<Integer> indexes = new ArrayList<>();
                for (int i = 0; i < child.size(attribute.getKey()); i++) {
                    childValue = child.get(String.class, attribute.getKey(), i).getValue();
                    if (childValue != null) {
                        int index = parent.size(attribute.getKey());
                        indexes.add(index);
                        parent.get(String.class, attribute.getKey(), index).setValue(childValue);
                    }
                }
                return indexes;
        }
        return new ArrayList<>();
    }

    private  List<Integer> mergeComplexField(MetadataAttributeConfiguration attribute,
                                   MetadataAttributeTypeConfiguration type,
                                   MetadataEditorConfiguration config,
                                   ComplexMetadataMap parent, ComplexMetadataMap child) {

        switch (attribute.getOccurrence()) {
            case SINGLE:
                if (child.size(attribute.getKey()) > 0) {
                    ComplexMetadataMap childMap = child.subMap(attribute.getKey());
                    ComplexMetadataMap parentMap = parent.subMap(attribute.getKey());
                    mergeAttribute(parentMap, childMap, type.getAttributes(), config, null);
                    ArrayList<Integer> indexes = new ArrayList<>();
                    indexes.add(0);
                    return indexes;
                }
                break;
            case REPEAT:
                ArrayList<Integer> indexes = new ArrayList<>();
                for (int i = 0; i < child.size(attribute.getKey()); i++) {
                    ComplexMetadataMap childMap = child.subMap(attribute.getKey(), i);
                    int index = parent.size(attribute.getKey());
                    ComplexMetadataMap parentMap = parent.subMap(attribute.getKey(), index);
                    indexes.add(index);
                    mergeAttribute(parentMap, childMap, type.getAttributes(), config, null);
                }
                return indexes;
        }
        return new ArrayList<>();
    }


    private void clearTemplateData(ComplexMetadataMap destination, HashMap<String, List<Integer>> descriptionMap) {
        for (String key : descriptionMap.keySet()) {
            List<Integer> indexes = descriptionMap.get(key);
            ArrayList<Integer> reversed = new ArrayList<Integer>(indexes);
            Collections.reverse(reversed);
            for (Integer index : reversed) {
                destination.delete(key, index);
            }
        }
        descriptionMap.clear();
    }
}
