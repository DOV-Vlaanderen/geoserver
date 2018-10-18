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

public class EditorFactory {


    static Component create(MetadataAttributeConfiguration configuration, String id, 
            IModel<ComplexMetadataMap> metadataModel) {
        switch (configuration.getFieldType()) {
            case TEXT: {
                
                ComplexMetadataAttributeModel<String> model =
                    new ComplexMetadataAttributeModel<String>(
                        metadataModel.getObject().get(String.class, configuration.getKey()));
                model.setObject(null);
                return new TextFieldPanel(id, model);
                
            } case NUMBER: {
                
                ComplexMetadataAttributeModel<String> model =
                new ComplexMetadataAttributeModel<String>(
                    metadataModel.getObject().get(String.class, configuration.getKey()));
                model.setObject(null);
                return new TextFieldPanel(id, model);
                
            } case DROPDOWN: {

                ComplexMetadataAttributeModel<String> model =
                      new ComplexMetadataAttributeModel<String>(
                               metadataModel.getObject().get(String.class, configuration.getKey()));
                model.setObject(null);                
                return new DropDownPanel(id, model,
                        configuration.getValues());
                
            } case COMPLEX:
                return new AttributesTablePanel(id, new AttributeDataProvider(configuration.getTypename()),
                        new Model<ComplexMetadataMap>(metadataModel.getObject().subMap(configuration.getKey())));
        }
        return null;
    }


}
