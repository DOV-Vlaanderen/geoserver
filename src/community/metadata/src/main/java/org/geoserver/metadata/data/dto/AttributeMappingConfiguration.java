/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.dto;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class AttributeMappingConfiguration {

    List<AttributeMapping> geonetworkmapping = new ArrayList<>();

    List<AttributeComplexTypeMapping> objectmapping = new ArrayList<>();

    public List<AttributeMapping> getGeonetworkmapping() {
        return geonetworkmapping;
    }

    public void setGeonetworkmapping(List<AttributeMapping> geonetworkmapping) {
        this.geonetworkmapping = geonetworkmapping;
    }

    public List<AttributeComplexTypeMapping> getObjectmapping() {
        return objectmapping;
    }

    public void setObjectmapping(List<AttributeComplexTypeMapping> objectmapping) {
        this.objectmapping = objectmapping;
    }
}
