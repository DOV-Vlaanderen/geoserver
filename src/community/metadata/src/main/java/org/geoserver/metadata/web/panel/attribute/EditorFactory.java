/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel.attribute;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.geoserver.metadata.data.dto.FieldTypeEnum;
import org.geoserver.metadata.data.model.ComplexMetadataAttribute;
import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.geoserver.metadata.data.dto.MetadataAttributeConfiguration;
import org.geoserver.metadata.data.model.ComplexMetadataAttributeModel;
import org.geoserver.metadata.data.model.impl.ComplexMetadataMapImpl;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
 * Factory to generate a component based on the configuration.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class EditorFactory {

    private static final EditorFactory instance = new EditorFactory();

    //private constructor to avoid client applications to use constructor
    private EditorFactory(){}

    public static EditorFactory getInstance(){
        return instance;
    }

    public Component create(MetadataAttributeConfiguration configuration, String id, ComplexMetadataMap metadataMap) {

        ComplexMetadataAttribute metadataModel = metadataMap.get(getItemClass(configuration), configuration.getKey());
        IModel model = new ComplexMetadataAttributeModel(metadataModel);

        return create(configuration, id, model, metadataMap.subMap(configuration.getKey()));
    }

    public Component create(MetadataAttributeConfiguration configuration, String id,
                            ComplexMetadataAttribute metadataAttribute) {

        IModel model = new ComplexMetadataAttributeModel<String>(metadataAttribute);
        return create(configuration, id, model, new ComplexMetadataMapImpl(new HashMap<String, Serializable>()));
    }

    private Component create(MetadataAttributeConfiguration configuration,
                             String id,
                             IModel model,
                             ComplexMetadataMap submap) {

        switch (configuration.getFieldType()) {
            case TEXT:
                return new TextFieldPanel(id, model);
            case NUMBER:
                return new NumberFieldPanel(id, model);
            case DROPDOWN:
                return new DropDownPanel(id, model, configuration.getValues());
            case TEXT_AREA:
                return new TextAreaPanel(id, model);
            case DATE:
                return new TextFieldPanel(id, model);
            case UUID:
                return new UUIDFieldPanel(id, model);
            case SUGGESTBOX:
                return new DropDownPanel(id, model, configuration.getValues());
            case COMPLEX:
                return new AttributesTablePanel(id, new AttributeDataProvider(configuration.getTypename()),
                        new Model<ComplexMetadataMap>(submap), null);
        }
        return null;
    }


    public Class getItemClass(MetadataAttributeConfiguration attributeConfiguration) {
        switch (attributeConfiguration.getFieldType()){
            case TEXT:
                break;
            case NUMBER:
                return Integer.class;
            case TEXT_AREA:
                break;
            case DATE:
                return Date.class;
            case UUID:
                break;
            case DROPDOWN:
                break;
            case SUGGESTBOX:
                break;
            case COMPLEX:
                break;
        }
        return String.class;
    }


}
