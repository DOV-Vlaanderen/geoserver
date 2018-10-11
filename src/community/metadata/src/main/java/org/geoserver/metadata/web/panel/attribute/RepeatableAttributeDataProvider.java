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
        template.setOccurrence(OccurenceEnum.SINGLE);
        
        items = new ArrayList<ComplexMetadataAttribute<T>>();
        for (int i = 0; i < metadataModel.getObject().size(attributeConfiguration.getKey()); i++) {
            items.add(metadataModel.getObject().get(clazz, attributeConfiguration.getKey(), i));
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
        items.add(metadataModel.getObject().get(clazz, template.getKey(), items.size()));
    }

    public void removeFields(List<ComplexMetadataAttribute<T>> attributes) {
        items.removeAll(attributes);
    }
    
    public MetadataAttributeConfiguration getConfiguration() {
        return template;
    }
}
