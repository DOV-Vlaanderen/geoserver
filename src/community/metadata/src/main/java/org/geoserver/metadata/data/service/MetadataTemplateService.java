/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service;

import java.io.IOException;
import java.util.List;

import org.geoserver.metadata.data.model.MetadataTemplate;

/**
 * @author Timothy De Bock
 */
public interface MetadataTemplateService {

    List<MetadataTemplate> list() throws IOException;

    void save(MetadataTemplate metadataTemplate) throws IOException;

    void update(MetadataTemplate metadataTemplate) throws IOException;

    MetadataTemplate load(String templateName) throws IOException;

    void delete(MetadataTemplate metadataTemplate) throws IOException;
}
