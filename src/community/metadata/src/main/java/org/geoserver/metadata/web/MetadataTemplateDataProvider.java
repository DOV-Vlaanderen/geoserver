/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web;

import org.geoserver.metadata.data.model.MetadataTemplate;
import org.geoserver.metadata.data.service.MetadataTemplateService;
import org.geoserver.web.GeoServerApplication;
import org.geoserver.web.wicket.GeoServerDataProvider;
import org.geotools.util.logging.Logging;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MetadataTemplateDataProvider extends GeoServerDataProvider<MetadataTemplate> {


    private static final java.util.logging.Logger LOGGER = Logging.getLogger(MetadataTemplateDataProvider.class);

    private static final long serialVersionUID = -8246320435114536132L;

    public static final Property<MetadataTemplate> NAME = new BeanProperty<MetadataTemplate>("name", "name");

    public static final Property<MetadataTemplate> DESCRIPTION = new BeanProperty<MetadataTemplate>("description", "description");

    public MetadataTemplateDataProvider() {
    }

    @Override
    protected List<Property<MetadataTemplate>> getProperties() {
        return Arrays.asList(NAME, DESCRIPTION);
    }

    @Override
    protected List<MetadataTemplate> getItems() {
        try {
            MetadataTemplateService service =
                    GeoServerApplication.get().getApplicationContext().getBean(MetadataTemplateService.class);
            return service.list();
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
        return Collections.emptyList();
    }

}
