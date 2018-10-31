/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel.attribute;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.geoserver.metadata.data.dto.MetadataAttributeConfiguration;
import org.geoserver.metadata.data.model.ComplexMetadataAttributeModel;

/**
 * Factory to generate a component based on the configuration.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class EditorFactory {


    static Component create(MetadataAttributeConfiguration configuration, String id, 
            IModel<ComplexMetadataMap> metadataModel) {
        switch (configuration.getFieldType()) {
            case TEXT:
                return new TextFieldPanel(id, 
                        new ComplexMetadataAttributeModel<String>(
                                metadataModel.getObject().get(String.class, configuration.getKey())));
            case NUMBER:
                return new NumberFieldPanel(id,
                        new ComplexMetadataAttributeModel<Integer>(
                                metadataModel.getObject().get(Integer.class, configuration.getKey())));
            case DROPDOWN:
                return new DropDownPanel(id, 
                        new ComplexMetadataAttributeModel<String>(
                                metadataModel.getObject().get(String.class, configuration.getKey())),
                        configuration.getValues());
            case COMPLEX:
                return new AttributesTablePanel(id, new AttributeDataProvider(configuration.getTypename()),
                        new Model<ComplexMetadataMap>(metadataModel.getObject().subMap(configuration.getKey())), null);
        }
        return null;
    }


}
