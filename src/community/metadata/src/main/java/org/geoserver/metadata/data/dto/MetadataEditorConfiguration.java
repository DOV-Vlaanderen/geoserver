/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.geoserver.metadata.data.dto.impl.MetadataEditorConfigurationImpl;

/**
 * Toplevel Object that matches yaml structure.
 *
 * <p>Contains the Gui description for the metadata and a list of geonetwork endpoints for importing
 * geonetwork metadata. The Gui is constructed from MetadataAttributeConfiguration and
 * MetadataAttributeComplexTypeConfiguration.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
@JsonDeserialize(as = MetadataEditorConfigurationImpl.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface MetadataEditorConfiguration {

    public List<MetadataAttributeConfiguration> getAttributes();

    public void setAttributes(List<MetadataAttributeConfiguration> attributes);

    public List<MetadataGeonetworkConfiguration> getGeonetworks();

    public void setGeonetworks(List<MetadataGeonetworkConfiguration> geonetworks);

    public List<MetadataAttributeTypeConfiguration> getTypes();

    public void setComplextypes(List<MetadataAttributeTypeConfiguration> types);

    public MetadataAttributeTypeConfiguration findType(String typename);
}
