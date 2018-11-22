/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.dto.impl;

import java.util.ArrayList;
import java.util.List;
import org.geoserver.metadata.data.dto.AttributeComplexTypeMapping;
import org.geoserver.metadata.data.dto.AttributeMapping;

/**
 * Object that matches yaml structure.
 *
 * <p>The part describes one mapping for an object. The object mapping is made from a list of
 * mappings for each attribute.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class AttributeComplexTypeMappingImpl implements AttributeComplexTypeMapping {

    private static final long serialVersionUID = 8056316409852056776L;

    String typename;

    List<AttributeMapping> mapping = new ArrayList<>();

    @Override
    public String getTypename() {
        return typename;
    }

    @Override
    public void setTypename(String typename) {
        this.typename = typename;
    }

    @Override
    public List<AttributeMapping> getMapping() {
        return mapping;
    }

    @Override
    public void setMapping(List<AttributeMapping> mapping) {
        this.mapping = mapping;
    }
}
