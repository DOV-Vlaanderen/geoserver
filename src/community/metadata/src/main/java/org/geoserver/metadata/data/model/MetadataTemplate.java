/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.model;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class MetadataTemplate implements Serializable {

    private String name;

    private String description;

    private ComplexMetadataMap metadata;

    private Set<String> linkedLayers = new HashSet<>();

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

    public Set<String> getLinkedLayers() {
        return linkedLayers;
    }

    public void setLinkedLayers(Set<String> linkedLayers) {
        this.linkedLayers = linkedLayers;
    }
}
