/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.dto;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AttributeComplexTypeMapping implements Serializable{

    String typename;

    List<AttributeMapping> mapping = new ArrayList<>();

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public List<AttributeMapping> getMapping() {
        return mapping;
    }

    public void setMapping(List<AttributeMapping> mapping) {
        this.mapping = mapping;
    }
}