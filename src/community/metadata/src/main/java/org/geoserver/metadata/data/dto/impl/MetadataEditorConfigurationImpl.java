/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.dto.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.geoserver.metadata.data.dto.MetadataAttributeConfiguration;
import org.geoserver.metadata.data.dto.MetadataAttributeTypeConfiguration;
import org.geoserver.metadata.data.dto.MetadataEditorConfiguration;
import org.geoserver.metadata.data.dto.MetadataGeonetworkConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Toplevel Object that matches yaml structure.
 *
 * Contains the Gui description for the metadata and a list of geonetwork endpoints for importing geonetwork metadata.
 * The Gui is constructed from MetadataAttributeConfiguration and MetadataAttributeComplexTypeConfiguration.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetadataEditorConfigurationImpl implements MetadataEditorConfiguration {

    List<MetadataAttributeConfiguration> attributes = new ArrayList<>();

    List<MetadataGeonetworkConfiguration> geonetworks = new ArrayList<>();

    List<MetadataAttributeTypeConfiguration> types = new ArrayList<>();
    @Override
    public List<MetadataAttributeConfiguration> getAttributes() {
        return attributes;
    }
    @Override
    public void setAttributes(List<MetadataAttributeConfiguration> attributes) {
        this.attributes = attributes;
    }
    @Override
    public List<MetadataGeonetworkConfiguration> getGeonetworks() {
        return geonetworks;
    }
    @Override
    public void setGeonetworks(List<MetadataGeonetworkConfiguration> geonetworks) {
        this.geonetworks = geonetworks;
    }
    @Override
    public List<MetadataAttributeTypeConfiguration> getTypes() {
        return types;
    }
    @Override
    public void setComplextypes(List<MetadataAttributeTypeConfiguration> types) {
        this.types = types;
    }
    @Override
    public MetadataAttributeTypeConfiguration findType(String typename) {
        for (MetadataAttributeTypeConfiguration type : types) {
            if (typename.equals(type.getTypename())) {
                return type;
            }
        }
        return null;
    }
}
