/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.model;


import org.geoserver.catalog.MetadataMap;
import org.geoserver.metadata.data.ComplexMetadataMap;

import java.io.Serializable;

/**
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class MetadataTemplate implements Serializable {

    private String name;

    private String description;

    private ComplexMetadataMap metadata;

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

    public ComplexMetadataMap getMetadata() {
        return metadata;
    }

    public void setMetadata(ComplexMetadataMap metadata) {
        this.metadata = metadata;
    }
}
