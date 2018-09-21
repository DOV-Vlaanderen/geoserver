/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.dto;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

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
