/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Toplevel Object that matches yaml structure.
 *
 * Contains the Gui description for the metadata and a list of geonetwork endpoints for importing geonetwork metadata.
 * The Gui is constructed of MetadataAttributeConfiguration and MetadataAttributeComplexTypeConfiguration.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetadataEditorConfiguration {

    List<MetadataAttributeConfiguration> attributes = new ArrayList<>();

    List<MetadataGeonetworkConfiguration> geonetworks = new ArrayList<>();

    List<MetadataAttributeComplexTypeConfiguration> complextypes = new ArrayList<>();

    public List<MetadataAttributeConfiguration> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<MetadataAttributeConfiguration> attributes) {
        this.attributes = attributes;
    }

    public List<MetadataGeonetworkConfiguration> getGeonetworks() {
        return geonetworks;
    }

    public void setGeonetworks(List<MetadataGeonetworkConfiguration> geonetworks) {
        this.geonetworks = geonetworks;
    }

    public List<MetadataAttributeComplexTypeConfiguration> getComplextypes() {
        return complextypes;
    }

    public void setComplextypes(List<MetadataAttributeComplexTypeConfiguration> complextypes) {
        this.complextypes = complextypes;
    }
}
