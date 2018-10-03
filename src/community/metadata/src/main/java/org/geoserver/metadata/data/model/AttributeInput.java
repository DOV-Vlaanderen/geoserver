/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.model;


import org.geoserver.metadata.data.dto.MetadataAttributeConfiguration;

import java.io.Serializable;

public class AttributeInput implements Serializable{

    private static final long serialVersionUID = -8928656843453377656L;

    MetadataAttributeConfiguration attributeConfiguration;

    Object inputValue;

    public AttributeInput(MetadataAttributeConfiguration attributeConfiguration) {
        if (attributeConfiguration != null) {
            this.attributeConfiguration = new MetadataAttributeConfiguration(attributeConfiguration);
        }
    }

    public AttributeInput(MetadataAttributeConfiguration attributeConfiguration, Object inputValue) {
        if (attributeConfiguration != null) {
            this.attributeConfiguration = new MetadataAttributeConfiguration(attributeConfiguration);
        }
        this.inputValue = inputValue;
    }

    public MetadataAttributeConfiguration getAttributeConfiguration() {
        return attributeConfiguration;
    }

    public void setAttributeConfiguration(MetadataAttributeConfiguration attributeConfiguration) {
        this.attributeConfiguration = attributeConfiguration;
    }

    public Object getInputValue() {
        return inputValue;
    }

    public void setInputValue(Object inputValue) {
        this.inputValue = inputValue;
    }
}
