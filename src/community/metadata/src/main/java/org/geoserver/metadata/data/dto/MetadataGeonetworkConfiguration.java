/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.dto;

import java.io.Serializable;

/**
 * Object that matches yaml structure.
 *
 * Describe a geonetwork endpoint.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class MetadataGeonetworkConfiguration implements Serializable{

    private static final long serialVersionUID = -652210940618705299L;

    String name;

    String url;

    public MetadataGeonetworkConfiguration() {
    }

    public MetadataGeonetworkConfiguration(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
