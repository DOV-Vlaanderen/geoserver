/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.dto;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.geoserver.metadata.data.dto.impl.AttributeComplexTypeMappingImpl;

import java.io.Serializable;
import java.util.List;


/**
 * Object that matches yaml structure.
 *
 * The part describes one mapping for an object. The object mapping is made from a list of mappings for each attribute.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
@JsonDeserialize(as = AttributeComplexTypeMappingImpl.class)
public interface AttributeComplexTypeMapping extends Serializable{

    public String getTypename();

    public void setTypename(String typename);

    public List<AttributeMapping> getMapping();

    public void setMapping(List<AttributeMapping> mapping);
}
