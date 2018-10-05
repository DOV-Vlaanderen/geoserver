package org.geoserver.metadata.web.panel.attribute;

import java.io.Serializable;

import org.apache.wicket.model.IModel;
import org.geoserver.metadata.data.ComplexMetadataAttribute;

public  class ComplexMetadataAttributeModel<T extends Serializable> implements IModel<T> {
    private static final long serialVersionUID = 2943279172304236560L;
    
    private ComplexMetadataAttribute<T> att;
    
    public ComplexMetadataAttributeModel(ComplexMetadataAttribute<T> att) {
        super();
        this.att = att;
    }

    @Override
    public T getObject() {
        return att.getValue();
    }

    @Override
    public void setObject(T object) {
        att.setValue(object);
    }

    @Override
    public void detach() {

    }
}