/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
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

    /**
     * 
     */
    private static final long serialVersionUID = -255037580716257623L;

    public static Property<AttributeInput> NAME = new BeanProperty<AttributeInput>("Name", "attributeConfiguration.label");

    public static Property<AttributeInput> VALUE = new BeanProperty<AttributeInput>("Value", "inputValue");

    private final MetadataAttributeConfiguration template;

    private List<AttributeInput> items = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public RepeatableAttributeDataProvider(MetadataAttributeConfiguration attributeConfiguration, IModel<MetadataMap> metadataModel) {
        this.template = new MetadataAttributeConfiguration(attributeConfiguration);
        template.setOccurrence(OccurenceEnum.SINGLE);
        MetadataMap metadataMap = metadataModel.getObject();

        if (metadataMap.get(attributeConfiguration.getLabel()) == null) {
            ArrayList<Object> elements = new ArrayList<>();
            AttributeInput attributeInput = new AttributeInput(template);
            elements.add(attributeInput);
            metadataMap.put(attributeConfiguration.getLabel(), elements);
        }
        items = (List<AttributeInput>) metadataMap.get(attributeConfiguration.getLabel());
        for (AttributeInput item : items) {
            if(item.getAttributeConfiguration() == null){
                item.setAttributeConfiguration(new MetadataAttributeConfiguration(template));
            }
        }
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
    }

    public void removeFields(List<AttributeInput> attributes) {
        items.removeAll(attributes);
    }
}
