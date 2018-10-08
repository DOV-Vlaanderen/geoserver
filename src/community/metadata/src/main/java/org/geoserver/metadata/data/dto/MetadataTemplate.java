/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.dto;


import org.geoserver.catalog.MetadataMap;

import java.io.Serializable;

/**
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class MetadataTemplate implements Serializable {

    private String name;

    private String description;

    private MetadataMap metadata;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MetadataMap getMetadata() {
        return metadata;
    }

    public void setMetadata(MetadataMap metadata) {
        this.metadata = metadata;
    }
}
