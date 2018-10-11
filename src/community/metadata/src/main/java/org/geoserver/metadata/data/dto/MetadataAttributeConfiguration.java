/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MetadataAttributeConfiguration implements Serializable{

    private static final long serialVersionUID = 3130368513874060531L;

    String key;

    String label;

    FieldTypeEnum fieldType;

    OccurenceEnum occurrence = OccurenceEnum.SINGLE;

    List<String> values = new ArrayList<>();

    String typename;

    public MetadataAttributeConfiguration() {
    }

    public MetadataAttributeConfiguration(String key, FieldTypeEnum fieldType) {
        this.key = key;
        this.label = key;
        this.fieldType = fieldType;
    }

    public MetadataAttributeConfiguration(MetadataAttributeConfiguration other) {
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public FieldTypeEnum getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldTypeEnum fieldType) {
        this.fieldType = fieldType;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public OccurenceEnum getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(OccurenceEnum occurrence) {
        this.occurrence = occurrence;
    }
}
