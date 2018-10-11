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
 * The part describes one mapping for an object. The object mapping is made from a list of mappings for each attribute.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class AttributeComplexTypeMapping implements Serializable{

    private static final long serialVersionUID = 8056316409852056776L;

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
