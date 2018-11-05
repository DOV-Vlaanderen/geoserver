/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel.attribute;

import org.geoserver.metadata.data.dto.MetadataAttributeTypeConfiguration;
import org.geoserver.metadata.data.dto.MetadataAttributeConfiguration;
import org.geoserver.metadata.data.service.MetadataEditorConfigurationService;
import org.geoserver.web.GeoServerApplication;
import org.geoserver.web.wicket.GeoServerDataProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AttributeDataProvider extends GeoServerDataProvider<MetadataAttributeConfiguration> {

    private static final long serialVersionUID = -4454769618643460913L;

    public static Property<MetadataAttributeConfiguration> NAME = new BeanProperty<MetadataAttributeConfiguration>("name", "name");

    public static Property<MetadataAttributeConfiguration> VALUE = new BeanProperty<MetadataAttributeConfiguration>("value", "value");

    private List<MetadataAttributeConfiguration> items = new ArrayList<>();

    public AttributeDataProvider() {
        MetadataEditorConfigurationService metadataConfigurationService = GeoServerApplication.get().getApplicationContext().getBean(MetadataEditorConfigurationService.class);
        for (MetadataAttributeConfiguration config : metadataConfigurationService.readConfiguration().getAttributes()) {
            items.add(config);
        }
    }

    /**
     * Provide attributes for the given complex type configuration.
     *
     * @param typename
     */
    public AttributeDataProvider(String typename) {
        super();
        MetadataEditorConfigurationService metadataConfigurationService = GeoServerApplication.get().getApplicationContext().getBean(MetadataEditorConfigurationService.class);
        MetadataAttributeTypeConfiguration typeConfiguration 
            = metadataConfigurationService.readConfiguration().findType(typename);
        if (typeConfiguration != null) {
            for (MetadataAttributeConfiguration config : typeConfiguration.getAttributes()) {
                items.add(config);
            }
        }
    }


    @Override
    protected List<Property<MetadataAttributeConfiguration>> getProperties() {
        return Arrays.asList(NAME, VALUE);
    }

    @Override
    protected List<MetadataAttributeConfiguration> getItems() {
        return items;
    }
}
