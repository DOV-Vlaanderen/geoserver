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

    void save(MetadataTemplate metadataTemplate) throws IOException;

    /**
     * Update the current template and cascade the changes to all linked layers.
     *
     * @param metadataTemplate
     * @throws IOException
     */
    void update(MetadataTemplate metadataTemplate) throws IOException;

    /**
     * Update the template and linked layers. Without cascading the changes.
     *
     * @param metadataTemplate
     * @throws IOException
     */
    void updateLinkLayers(MetadataTemplate metadataTemplate) throws IOException;

    MetadataTemplate load(String templateName);

    void delete(MetadataTemplate metadataTemplate) throws IOException;

    void increasePriority(MetadataTemplate metadataTemplate);

    void decreasePriority(MetadataTemplate metadataTemplate);
}
