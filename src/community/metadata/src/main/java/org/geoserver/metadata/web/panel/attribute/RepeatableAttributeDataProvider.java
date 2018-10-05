/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel.attribute;

import org.apache.wicket.model.IModel;
import org.geoserver.metadata.data.ComplexMetadataAttribute;
import org.geoserver.metadata.data.ComplexMetadataMap;
import org.geoserver.metadata.data.dto.MetadataAttributeConfiguration;
import org.geoserver.metadata.data.dto.OccurenceEnum;
import org.geoserver.web.wicket.GeoServerDataProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RepeatableAttributeDataProvider<T extends Serializable> 
    extends GeoServerDataProvider<ComplexMetadataAttribute<T>> {

    private static final long serialVersionUID = -255037580716257623L;

    /*
    public static Property<ComplexMetadataAttribute<?>> NAME = 
            new BeanProperty<ComplexMetadataAttribute<?>>("Name", "attributeConfiguration.label");*/
    
    public static String KEY_VALUE = "value";

    private final Property<ComplexMetadataAttribute<T>> VALUE = 
            new BeanProperty<ComplexMetadataAttribute<T>>(KEY_VALUE, "value");

    private final MetadataAttributeConfiguration template;
    
    private IModel<ComplexMetadataMap> metadataModel;

    private List<ComplexMetadataAttribute<T>> items = new ArrayList<>();
    
    private Class<T> clazz;

    public RepeatableAttributeDataProvider(Class<T> clazz, MetadataAttributeConfiguration attributeConfiguration, 
            IModel<ComplexMetadataMap> metadataModel) {
        this.clazz = clazz;
        this.metadataModel = metadataModel;

        this.template = new MetadataAttributeConfiguration(attributeConfiguration);
        template.setOccurrence(OccurenceEnum.SINGLE); //note: I don't get this

        /*if (metadataMap.get(attributeConfiguration.getLabel()) == null) {
            ArrayList<Object> elements = new ArrayList<>();
            ComplexMetadataAttribute<T> att = new ComplexMetadataAttribute<T>(template);
            elements.add(ComplexMetadataAttribute<T>);
            metadataMap.put(attributeConfiguration.getLabel(), elements);
        }
        items = (List<ComplexMetadataAttribute<T>>) metadataMap.get(attributeConfiguration.getLabel());
        for (ComplexMetadataAttribute<T> item : items) {
            if(item.getAttributeConfiguration() == null){
                item.setAttributeConfiguration(new MetadataAttributeConfiguration(template));
            }
        }*/
        
        items = new ArrayList<ComplexMetadataAttribute<T>>();
        for (int i = 0; i < metadataModel.getObject().size(attributeConfiguration.getLabel()); i++) {
            items.add(metadataModel.getObject().get(clazz, attributeConfiguration.getLabel(), i));
        }
        
    }


    @Override
    protected List<Property<ComplexMetadataAttribute<T>>> getProperties() {
        return Arrays.asList(VALUE);
    }

    @Override
    protected List<ComplexMetadataAttribute<T>> getItems() {
        return items;
    }

    public void addField() {
        items.add(metadataModel.getObject().get(clazz, template.getLabel(), 
                items.size()));
    }

    public void removeFields(List<ComplexMetadataAttribute<T>> attributes) {
        items.removeAll(attributes);
    }
    
    public MetadataAttributeConfiguration getConfiguration() {
        return template;
    }
}
