/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel.attribute;

import org.geoserver.metadata.data.dto.AttributeInput;
import org.geoserver.metadata.data.dto.MetadataAttributeComplexTypeConfiguration;
import org.geoserver.metadata.data.dto.MetadataAttributeConfiguration;
import org.geoserver.metadata.data.service.MetadataEditorConfigurationService;
import org.geoserver.web.GeoServerApplication;
import org.geoserver.web.wicket.GeoServerDataProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AttributeDataProvider extends GeoServerDataProvider<AttributeInput> {

    public static Property<AttributeInput> NAME = new BeanProperty<AttributeInput>("Name", "attributeConfiguration.label");

    public static Property<AttributeInput> VALUE = new BeanProperty<AttributeInput>("Value", "inputValue");

    private List<AttributeInput> items = new ArrayList<>();

    public AttributeDataProvider() {
        MetadataEditorConfigurationService metadataConfigurationService = GeoServerApplication.get().getApplicationContext().getBean(MetadataEditorConfigurationService.class);
        for (MetadataAttributeConfiguration config : metadataConfigurationService.readConfiguration().getAttributes()) {
            items.add(new AttributeInput(config));
        }
    }

    /**
     *  Provide attributes for the given complex type configuration.
     * @param typename
     */
    public AttributeDataProvider(String typename) {
        super();
        MetadataEditorConfigurationService metadataConfigurationService = GeoServerApplication.get().getApplicationContext().getBean(MetadataEditorConfigurationService.class);
        for (MetadataAttributeComplexTypeConfiguration complexTypeConfiguration : metadataConfigurationService.readConfiguration().getComplextypes()) {
            if (complexTypeConfiguration.getTypename().equals(typename)){
                for (MetadataAttributeConfiguration config : complexTypeConfiguration.getAttributes()) {
                    items.add(new AttributeInput(config));
                }
            }
        }
    }



    @Override
    protected List<Property<AttributeInput>> getProperties() {
        return Arrays.asList(NAME, VALUE);
    }

    @Override
    protected List<AttributeInput> getItems() {
        return items;
    }
}
