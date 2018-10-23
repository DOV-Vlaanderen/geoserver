/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Object that matches yaml structure.
 *
 * The part describes a complex object. The complex object contains a list of mappings that make the object.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class MetadataAttributeTypeConfiguration implements Serializable{

    private static final long serialVersionUID = 7617959011871570119L;

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
