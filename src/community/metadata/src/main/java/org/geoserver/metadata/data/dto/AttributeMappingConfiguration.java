/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.geoserver.metadata.data.dto.impl.AttributeMappingConfigurationImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Toplevel Object that matches yaml structure.
 *
 * This part or the yaml contains the configuration that matches fields in the xml (Xpath expressions)
 * to the field configuration of the geoserver metadata GUI.
 *
 * example of the yaml file: metadata-mapping.yaml
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
@JsonDeserialize(as = AttributeMappingConfigurationImpl.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface AttributeMappingConfiguration {

    public List<AttributeMapping> getGeonetworkmapping();

    public void setGeonetworkmapping(List<AttributeMapping> geonetworkmapping);

    public List<AttributeComplexTypeMapping> getObjectmapping();

    public void setObjectmapping(List<AttributeComplexTypeMapping> objectmapping);
}
