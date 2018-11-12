package org.geoserver.metadata.data.model;

import java.io.Serializable;
import java.util.Set;

public interface MetadataTemplate extends Serializable {

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    ComplexMetadataMap getMetadata();

    void setMetadata(ComplexMetadataMap metadata);

    Set<String> getLinkedLayers();

    void setLinkedLayers(Set<String> linkedLayers);

}