/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.dto.impl;

import org.geoserver.metadata.data.dto.FieldTypeEnum;
import org.geoserver.metadata.data.dto.MetadataAttributeConfiguration;
import org.geoserver.metadata.data.dto.OccurenceEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Object that matches yaml structure.
 *
 * The configuration descibes one field for the gui.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class MetadataAttributeConfigurationImpl implements MetadataAttributeConfiguration {

    private static final long serialVersionUID = 3130368513874060531L;

    String key;

    String label;

    FieldTypeEnum fieldType;

    OccurenceEnum occurrence = OccurenceEnum.SINGLE;

    List<String> values = new ArrayList<>();

    String typename;

    public MetadataAttributeConfigurationImpl() {
    }

    public MetadataAttributeConfigurationImpl(String key, FieldTypeEnum fieldType) {
        this.key = key;
        this.label = key;
        this.fieldType = fieldType;
    }

    public MetadataAttributeConfigurationImpl(MetadataAttributeConfigurationImpl other) {
        if (other != null) {
            key = other.getKey();
            label = other.getLabel();
            fieldType = other.getFieldType();
            occurrence = other.getOccurrence();
            typename = other.getTypename();
            for (String values : other.getValues()) {
                this.values.add(values);
            }
        }
    }

    @Override
    public String getKey() {
        return key;
    }
    @Override
    public void setKey(String key) {
        this.key = key;
    }
    @Override
    public String getLabel() {
        return label;
    }
    @Override
    public void setLabel(String label) {
        this.label = label;
    }
    @Override
    public FieldTypeEnum getFieldType() {
        return fieldType;
    }
    @Override
    public void setFieldType(FieldTypeEnum fieldType) {
        this.fieldType = fieldType;
    }
    @Override
    public List<String> getValues() {
        return values;
    }
    @Override
    public void setValues(List<String> values) {
        this.values = values;
    }
    @Override
    public String getTypename() {
        return typename;
    }
    @Override
    public void setTypename(String typename) {
        this.typename = typename;
    }
    @Override
    public OccurenceEnum getOccurrence() {
        return occurrence;
    }
    @Override
    public void setOccurrence(OccurenceEnum occurrence) {
        this.occurrence = occurrence;
    }
}
