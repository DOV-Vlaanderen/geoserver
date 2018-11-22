/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.geoserver.metadata.data.dto.FieldTypeEnum;
import org.geoserver.metadata.data.dto.MetadataAttributeConfiguration;
import org.geoserver.metadata.data.dto.MetadataAttributeTypeConfiguration;
import org.geoserver.metadata.data.dto.MetadataEditorConfiguration;
import org.geoserver.metadata.data.model.ComplexMetadataAttribute;
import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.geoserver.metadata.data.service.ComplexMetadataService;
import org.geoserver.metadata.data.service.YamlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Implementation.
 *
 * <p>Node: values for templates that are lists are added in front of the user defined values in
 * order to keep the indexes in the description map constant even when the user modifies the list.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
@Repository
public class ComplexMetadataServiceImpl implements ComplexMetadataService {

    @Autowired YamlService yamlService;

    @Override
    public void merge(
            ComplexMetadataMap destination,
            List<ComplexMetadataMap> sources,
            HashMap<String, List<Integer>> derivedAtts) {

        MetadataEditorConfiguration config;
        try {
            config = yamlService.readConfiguration();
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Metadata could not be merge."
                            + "The corresponding gui configuration cannot be read.");
        }

        clearTemplateData(destination, derivedAtts);

        ArrayList<ComplexMetadataMap> reversed = new ArrayList<ComplexMetadataMap>(sources);
        Collections.reverse(reversed);
        for (ComplexMetadataMap source : reversed) {
            mergeAttribute(destination, source, config.getAttributes(), config, derivedAtts);
        }
    }

    @Override
    public void merge(
            ComplexMetadataMap destination,
            ComplexMetadataMap source,
            String typeName,
            HashMap<String, List<Integer>> derivedAtts) {

        MetadataEditorConfiguration config;
        try {
            config = yamlService.readConfiguration();
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Metadata could not be merge."
                            + "The corresponding gui configuration cannot be read.");
        }

        clearTemplateData(destination, derivedAtts);

        mergeAttribute(
                destination,
                source,
                typeName == null
                        ? config.getAttributes()
                        : config.findType(typeName).getAttributes(),
                config,
                new HashMap<>());
    }

    private void mergeAttribute(
            ComplexMetadataMap destination,
            ComplexMetadataMap source,
            List<MetadataAttributeConfiguration> attributes,
            MetadataEditorConfiguration config,
            HashMap<String, List<Integer>> derivedAtts) {
        for (MetadataAttributeConfiguration attribute : attributes) {
            if (attribute.getFieldType() == FieldTypeEnum.COMPLEX) {
                mergeComplexField(
                        attribute,
                        config.findType(attribute.getTypename()),
                        config,
                        destination,
                        source,
                        derivedAtts);
            } else {
                mergeSimpleField(attribute, destination, source, derivedAtts);
            }
        }
    }

    private void mergeSimpleField(
            MetadataAttributeConfiguration attribute,
            ComplexMetadataMap destination,
            ComplexMetadataMap source,
            HashMap<String, List<Integer>> derivedAtts) {

        if (derivedAtts != null && !derivedAtts.containsKey(attribute.getKey())) {
            derivedAtts.put(attribute.getKey(), new ArrayList<>());
        }
        ArrayList<Integer> indexes = new ArrayList<>();

        switch (attribute.getOccurrence()) {
            case SINGLE:
                String sourceValue = source.get(String.class, attribute.getKey()).getValue();
                if (sourceValue != null) {
                    destination.get(String.class, attribute.getKey()).setValue(sourceValue);
                    indexes.add(0);
                }
                break;
            case REPEAT:
                int startIndex = 0;
                int sourceSize = source.size(attribute.getKey());
                if (derivedAtts != null) {
                    startIndex = derivedAtts.get(attribute.getKey()).size();
                    // SHIFT user content
                    for (int i = destination.size(attribute.getKey()) - 1; i >= 0; i--) {
                        if (i >= startIndex) {
                            String value =
                                    destination.get(String.class, attribute.getKey(), i).getValue();
                            destination
                                    .get(String.class, attribute.getKey(), i + sourceSize)
                                    .setValue(value);
                        }
                    }
                }
                // insert template content
                for (int i = 0; i < sourceSize; i++) {
                    sourceValue = source.get(String.class, attribute.getKey(), i).getValue();
                    int index = startIndex + i;
                    indexes.add(index);
                    destination.get(String.class, attribute.getKey(), index).setValue(sourceValue);
                }
        }
        // keep track of the values that are from the template
        if (derivedAtts != null) {
            derivedAtts.get(attribute.getKey()).addAll(indexes);
        }
    }

    private void mergeComplexField(
            MetadataAttributeConfiguration attribute,
            MetadataAttributeTypeConfiguration type,
            MetadataEditorConfiguration config,
            ComplexMetadataMap destination,
            ComplexMetadataMap source,
            HashMap<String, List<Integer>> derivedAtts) {

        ArrayList<Integer> indexes = new ArrayList<>();
        if (derivedAtts != null) {
            if (!derivedAtts.containsKey(attribute.getKey())) {
                derivedAtts.put(attribute.getKey(), new ArrayList<>());
            }
        }

        switch (attribute.getOccurrence()) {
            case SINGLE:
                if (source.size(attribute.getKey()) > 0) {
                    ComplexMetadataMap sourceMap = source.subMap(attribute.getKey());
                    ComplexMetadataMap destinationMap = destination.subMap(attribute.getKey());
                    mergeAttribute(destinationMap, sourceMap, type.getAttributes(), config, null);
                    indexes.add(0);
                }
                break;
            case REPEAT:
                int startIndex = 0;
                int sourceSize = source.size(attribute.getKey());
                if (derivedAtts != null) {
                    startIndex = derivedAtts.get(attribute.getKey()).size();
                    // SHIFT user content
                    for (int i = destination.size(attribute.getKey()) - 1; i >= 0; i--) {
                        if (i >= startIndex) {
                            ComplexMetadataMap orig = destination.subMap(attribute.getKey(), i);
                            ComplexMetadataMap shifted =
                                    destination.subMap(attribute.getKey(), i + sourceSize);
                            mergeAttribute(shifted, orig, type.getAttributes(), config, null);
                        }
                    }
                }
                // insert template content
                for (int i = 0; i < source.size(attribute.getKey()); i++) {
                    ComplexMetadataMap sourceMap = source.subMap(attribute.getKey(), i);
                    int index = startIndex + i;
                    ComplexMetadataMap destinationMap =
                            destination.subMap(attribute.getKey(), index);
                    indexes.add(index);
                    mergeAttribute(destinationMap, sourceMap, type.getAttributes(), config, null);
                }
        }
        // keep track of the values that are from the template
        if (derivedAtts != null) {
            derivedAtts.get(attribute.getKey()).addAll(indexes);
        }
    }

    private void clearTemplateData(
            ComplexMetadataMap destination, HashMap<String, List<Integer>> derivedAtts) {
        for (String key : derivedAtts.keySet()) {
            List<Integer> indexes = derivedAtts.get(key);
            ArrayList<Integer> reversed = new ArrayList<Integer>(indexes);
            Collections.reverse(reversed);
            for (Integer index : reversed) {
                ComplexMetadataAttribute<String> attribute = destination.get(String.class, key);
                if (attribute != null
                        && attribute.getValue() != null
                        && !attribute.getValue().startsWith("[")
                        && !attribute.getValue().startsWith("]")) {
                    attribute.setValue("");
                }
                destination.delete(key, index);
            }
        }
        derivedAtts.clear();
    }
}
