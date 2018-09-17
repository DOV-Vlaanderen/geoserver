/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.dto;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AttributeInput implements Serializable{

    MetadataAttributeConfiguration attributeConfiguration;

    Object inputValue;

    public AttributeInput(MetadataAttributeConfiguration attributeConfiguration) {
        this.attributeConfiguration = attributeConfiguration;
    }

    public MetadataAttributeConfiguration getAttributeConfiguration() {
        return attributeConfiguration;
    }

    public Object getInputValue() {
        return inputValue;
    }

    public void setInputValue(Object inputValue) {
        this.inputValue = inputValue;
    }
}
