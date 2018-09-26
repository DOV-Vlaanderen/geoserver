/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.dto;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MetadataAttributeConfiguration implements Serializable{

    String label;

    FieldTypeEnum fieldType;

    OccurenceEnum occurrence = OccurenceEnum.SINGLE;

    List<String> values = new ArrayList<>();

    String typename;

    public MetadataAttributeConfiguration() {
    }

    public MetadataAttributeConfiguration(String label, FieldTypeEnum fieldType) {
        this.label = label;
        this.fieldType = fieldType;
    }

    public MetadataAttributeConfiguration(MetadataAttributeConfiguration other) {
        if (other != null) {
            label = other.getLabel();
            fieldType = other.getFieldType();
            occurrence = other.getOccurrence();
            typename = other.getTypename();
            for (String values : other.getValues()) {
                this.values.add(values);
            }
        }
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
