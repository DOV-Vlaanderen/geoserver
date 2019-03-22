/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service;

import java.io.IOException;
import java.util.List;
import org.geoserver.metadata.data.model.MetadataTemplate;

/** @author Timothy De Bock */
public interface MetadataTemplateService {

    List<MetadataTemplate> list();

    void add(MetadataTemplate metadataTemplate) throws IOException;

    MetadataTemplate load(String templateName);

    void delete(MetadataTemplate metadataTemplate) throws IOException;

    void increasePriority(MetadataTemplate metadataTemplate);

    void decreasePriority(MetadataTemplate metadataTemplate);

    /**
     * Update the template and linked layers. Without or with cascading the changes.
     *
     * @param metadataTemplate
     * @param updateLayers cascade changes to layers
     * @throws IOException
     */
    void save(MetadataTemplate metadataTemplate, boolean updateLayers) throws IOException;
}
