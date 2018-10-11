/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel.attribute;

import org.apache.wicket.model.IModel;
import org.geoserver.metadata.data.ComplexMetadataMap;
import org.geoserver.metadata.data.dto.MetadataAttributeConfiguration;
import org.geoserver.web.wicket.GeoServerDataProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RepeatableComplexAttributeDataProvider
    extends GeoServerDataProvider<ComplexMetadataMap> {

    private static final long serialVersionUID = -255037580716257623L;
    
    public static String KEY_VALUE = "value";

    public static final Property<ComplexMetadataMap> VALUE = 
            new BeanProperty<ComplexMetadataMap>(KEY_VALUE, "value");
    
    private IModel<ComplexMetadataMap> metadataModel;
    
    private MetadataAttributeConfiguration attributeConfiguration;

    private List<ComplexMetadataMap> items = new ArrayList<>();
    
    public RepeatableComplexAttributeDataProvider(MetadataAttributeConfiguration attributeConfiguration, 
            IModel<ComplexMetadataMap> metadataModel) {
        this.metadataModel = metadataModel;
        this.attributeConfiguration = attributeConfiguration;
        
        items = new ArrayList<ComplexMetadataMap>();
        for (int i = 0; i < metadataModel.getObject().size(attributeConfiguration.getKey()); i++) {
            items.add(metadataModel.getObject().subMap(attributeConfiguration.getKey(), i));
        }        
    }

    @Override
    protected List<Property<ComplexMetadataMap>> getProperties() {
        return Arrays.asList(VALUE);
    }

    @Override
    protected List<ComplexMetadataMap> getItems() {
        return items;
    }

    public void addField() {
        items.add(metadataModel.getObject().subMap(attributeConfiguration.getKey(), items.size()));
    }

    public void removeFields(List<ComplexMetadataMap> attributes) {
        items.removeAll(attributes);
    }
    
    public MetadataAttributeConfiguration getConfiguration() {
        return attributeConfiguration;
    }
}
