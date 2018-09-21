/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.dto;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MetadataAttributeComplexTypeConfiguration implements Serializable{

    String typename;

    List<MetadataAttributeConfiguration> attributes = new ArrayList<>();

    public List<MetadataAttributeConfiguration> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<MetadataAttributeConfiguration> attributes) {
        this.attributes = attributes;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }
}
