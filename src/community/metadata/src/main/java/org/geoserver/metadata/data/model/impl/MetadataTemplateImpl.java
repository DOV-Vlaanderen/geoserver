/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.model.impl;


import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.geoserver.metadata.data.model.MetadataTemplate;

/**
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
@XStreamAlias("MetadataTemplate")
public class MetadataTemplateImpl implements Serializable, MetadataTemplate {

    private static final long serialVersionUID = -1907518678061997394L;

    private Integer priority;

    private String name;

    private String description;

    private ComplexMetadataMap metadata;

    private Set<String> linkedLayers = new HashSet<>();

    /* (non-Javadoc)
     * @see org.geoserver.metadata.data.model.impl.MetadataTemplate#getPriority()
     */
    @Override
    public Integer getPriority() {
        return priority;
    }

    /* (non-Javadoc)
     * @see org.geoserver.metadata.data.model.impl.MetadataTemplate#setPriority(java.lang.Integer)
     */
    @Override
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /* (non-Javadoc)
     * @see org.geoserver.metadata.data.model.impl.MetadataTemplate#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.geoserver.metadata.data.model.impl.MetadataTemplate#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see org.geoserver.metadata.data.model.impl.MetadataTemplate#getMetadata()
     */
    @Override
    public ComplexMetadataMap getMetadata() {
        return metadata;
    }

    /* (non-Javadoc)
     * @see org.geoserver.metadata.data.model.impl.MetadataTemplate#setMetadata(org.geoserver.metadata.data.model.ComplexMetadataMap)
     */
    @Override
    public void setMetadata(ComplexMetadataMap metadata) {
        this.metadata = metadata;
    }

    /* (non-Javadoc)
     * @see org.geoserver.metadata.data.model.impl.MetadataTemplate#getLinkedLayers()
     */
    @Override
    public Set<String> getLinkedLayers() {
        return linkedLayers;
    }

    /* (non-Javadoc)
     * @see org.geoserver.metadata.data.model.impl.MetadataTemplate#setLinkedLayers(java.util.Set)
     */
    @Override
    public void setLinkedLayers(Set<String> linkedLayers) {
        this.linkedLayers = linkedLayers;
    }
}
