package org.geoserver.metadata.data.model;

import java.io.Serializable;
import java.util.Set;

public interface MetadataTemplate extends Serializable {

    Integer getPriority();

    void setPriority(Integer priority);

    String getName();

    void setName(String name);

    ComplexMetadataMap getMetadata();

    void setMetadata(ComplexMetadataMap metadata);

    Set<String> getLinkedLayers();

    void setLinkedLayers(Set<String> linkedLayers);

}