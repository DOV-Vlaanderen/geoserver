/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.geoserver.metadata.data.dto.impl.MetadataGeonetworkConfigurationImpl;

import java.io.Serializable;

/**
 * Object that matches yaml structure.
 *
 * Describe a geonetwork endpoint.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
@JsonDeserialize(as = MetadataGeonetworkConfigurationImpl.class)
public interface MetadataGeonetworkConfiguration extends Serializable {

    public String getName();

    public void setName(String name);

    public String getUrl();

    public void setUrl(String url);
}
