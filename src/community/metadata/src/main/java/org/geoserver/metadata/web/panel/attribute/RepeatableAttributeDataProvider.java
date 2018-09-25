/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel.attribute;

import org.apache.wicket.model.IModel;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.metadata.data.model.AttributeInput;
import org.geoserver.metadata.data.dto.MetadataAttributeConfiguration;
import org.geoserver.metadata.data.dto.OccurenceEnum;
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

    public RepeatableAttributeDataProvider(MetadataAttributeConfiguration attributeConfiguration, IModel<MetadataMap> metadataModel) {
        this.template = new MetadataAttributeConfiguration(attributeConfiguration);
        template.setOccurrence(OccurenceEnum.SINGLE);
        if (metadataModel.getObject().get(attributeConfiguration.getLabel()) == null) {
            ArrayList<Object> elements = new ArrayList<>();
            AttributeInput attributeInput = new AttributeInput(template);
            elements.add(attributeInput);
            metadataModel.getObject().put(attributeConfiguration.getLabel(), elements);
        }
        items = (List<AttributeInput>) metadataModel.getObject().get(attributeConfiguration.getLabel());
    }


    @Override
    protected List<Property<AttributeInput>> getProperties() {
        return Arrays.asList(VALUE);
    }

    @Override
    protected List<AttributeInput> getItems() {
        return items;
    }

    public void addField() {
        AttributeInput attributeInput = new AttributeInput(template);
        items.add(attributeInput);
        /*AttributeInput attributeInput = new AttributeInput(template);
        String label;
        if (count != 0) {
            label = attributeInput.getAttributeConfiguration().getLabel() + "-" + count;
        } else {
            label = attributeInput.getAttributeConfiguration().getLabel();
        }
        attributeInput.getAttributeConfiguration().setLabel(label);
        items.add(attributeInput);
        count++;*/
    }

    public void removeFields(List<AttributeInput> attributes) {
        items.removeAll(attributes);
    }
}
