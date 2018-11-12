/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.dto.impl;

import org.geoserver.metadata.data.dto.MetadataAttributeConfiguration;
import org.geoserver.metadata.data.dto.MetadataAttributeTypeConfiguration;

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
public class MetadataAttributeTypeConfigurationImpl implements MetadataAttributeTypeConfiguration {

    private static final long serialVersionUID = 7617959011871570119L;

    String typename;

    List<MetadataAttributeConfiguration> attributes = new ArrayList<>();
    @Override
    public List<MetadataAttributeConfiguration> getAttributes() {
        return attributes;
    }
    @Override
    public void setAttributes(List<MetadataAttributeConfiguration> attributes) {
        this.attributes = attributes;
    }
    @Override
    public String getTypename() {
        return typename;
    }
    @Override
    public void setTypename(String typename) {
        this.typename = typename;
    }
}
