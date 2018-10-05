/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel.attribute;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.geoserver.metadata.data.ComplexMetadataMap;
import org.geoserver.metadata.data.dto.MetadataAttributeConfiguration;

public class EditorFactory {


    static Component create(MetadataAttributeConfiguration configuration, String id, 
            IModel<ComplexMetadataMap> metadataModel) {
        switch (configuration.getFieldType()) {
            case TEXT:
                return new TextFieldPanel(id, 
                        new ComplexMetadataAttributeModel<String>(
                                metadataModel.getObject().get(String.class, configuration.getLabel())));
            case NUMBER:
                return new TextFieldPanel(id, 
                        new ComplexMetadataAttributeModel<String>(
                                metadataModel.getObject().get(String.class, configuration.getLabel())));
            case DROPDOWN:
                return new DropDownPanel(id, 
                        new ComplexMetadataAttributeModel<String>(
                                metadataModel.getObject().get(String.class, configuration.getLabel())),
                        configuration.getValues());
            case COMPLEX:
                return new AttributesTablePanel(id, new AttributeDataProvider(configuration.getTypename(), 
                        configuration.getLabel()), 
                        new Model<ComplexMetadataMap>(metadataModel.getObject().subMap(configuration.getLabel())));
        }
        return null;
    }


}
