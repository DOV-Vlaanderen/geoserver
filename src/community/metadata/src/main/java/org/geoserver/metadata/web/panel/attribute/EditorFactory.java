/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel.attribute;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.metadata.data.dto.MetadataAttributeConfiguration;

public class EditorFactory {



    static Component create(MetadataAttributeConfiguration configuration, String id, IModel<MetadataMap> metadataModel) {
        switch (configuration.getFieldType()) {
            case TEXT:
                return new TextFieldPanel(id, createModel(configuration.getLabel(), metadataModel));
            case NUMBER:
                return new TextFieldPanel(id, createModel(configuration.getLabel(), metadataModel));
            case DROPDOWN:
                return new DropDownPanel(id, createModel(configuration.getLabel(), metadataModel), configuration.getValues());
            case COMPLEX:
                return new AttributesTablePanel(id, new AttributeDataProvider(configuration.getTypename(), configuration.getLabel()), metadataModel);
        }
        return null;
    }


    static private IModel<String> createModel(String key, IModel<MetadataMap> metadataModel) {
        return new IModel<String>() {
            /**
             * 
             */
            private static final long serialVersionUID = 2943279172304236560L;

            @Override
            public String getObject() {
                String val = (String) metadataModel.getObject().get(key);
                if(val == null){
                    val = "";
                }
                return val;
            }

            @Override
            public void setObject(String object) {
                metadataModel.getObject().put(key, object);
            }

            @Override
            public void detach() {

            }
        };
    }


}
