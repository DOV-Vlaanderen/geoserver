/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.model;


import org.geoserver.metadata.data.dto.MetadataAttributeConfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
//TODO this one is probaly not needed.
public class AttributeInput implements Serializable{

    MetadataAttributeConfiguration attributeConfiguration;

    Object inputValue;

    public AttributeInput(MetadataAttributeConfiguration attributeConfiguration) {
        if (attributeConfiguration != null) {
            this.attributeConfiguration = new MetadataAttributeConfiguration(attributeConfiguration);
        }
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
