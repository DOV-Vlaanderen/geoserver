/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel.attribute;

import org.geoserver.metadata.data.dto.AttributeInput;
import org.geoserver.metadata.data.dto.MetadataAttributeComplexTypeConfiguration;
import org.geoserver.metadata.data.dto.MetadataAttributeConfiguration;
import org.geoserver.metadata.data.dto.OccurenceEnum;
import org.geoserver.metadata.data.service.MetadataEditorConfigurationService;
import org.geoserver.web.GeoServerApplication;
import org.geoserver.web.wicket.GeoServerDataProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RepeatableAttributeDataProvider extends GeoServerDataProvider<AttributeInput> {

    public static Property<AttributeInput> NAME = new BeanProperty<AttributeInput>("Name", "attributeConfiguration.label");

    public static Property<AttributeInput> VALUE = new BeanProperty<AttributeInput>("Value", "inputValue");

    private int count;

    private final MetadataAttributeConfiguration template;

    private List<AttributeInput> items = new ArrayList<>();

    public RepeatableAttributeDataProvider(MetadataAttributeConfiguration attributeConfiguration) {
        this.template = new MetadataAttributeConfiguration(attributeConfiguration);
        template.setOccurrence(OccurenceEnum.SINGLE);
        addField();
    }


    @Override
    protected List<Property<AttributeInput>> getProperties() {
        return Arrays.asList(NAME, VALUE);
    }

    @Override
    protected List<AttributeInput> getItems() {
        return items;
    }

    public void addField() {
        AttributeInput attributeInput = new AttributeInput(template);
        String label;
        if (count != 0) {
            label = attributeInput.getAttributeConfiguration().getLabel() + "-" + count;
        } else {
            label = attributeInput.getAttributeConfiguration().getLabel();
        }
        attributeInput.getAttributeConfiguration().setLabel(label);
        items.add(attributeInput);
        count++;
    }

    public void removeFields(List<AttributeInput> attributes) {
        items.removeAll(attributes);
    }
}
