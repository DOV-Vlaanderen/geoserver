/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.layer.resource;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.metadata.data.ComplexMetadataMap;
import org.geoserver.metadata.data.impl.ComplexMetadataMapImpl;
import org.geoserver.metadata.web.panel.MetadataPanel;
import org.geoserver.web.data.resource.ResourceConfigurationPanel;

/**
 * A generic configuration panel for all basic ResourceInfo properties
 */
public class MetadataTabPanel extends ResourceConfigurationPanel  {

    private static final long serialVersionUID = -552158739086379566L;
    
    public final static String CUSTOM_METADATA_KEY = "custom";

    public MetadataTabPanel(String id, IModel<ResourceInfo> model) {
        super(id, model);

        Serializable custom = model.getObject().getMetadata().get(CUSTOM_METADATA_KEY);
        if (!(custom instanceof HashMap<?, ?>)) {
            custom = new HashMap<String, Serializable>();
            model.getObject().getMetadata().put(CUSTOM_METADATA_KEY, custom);
        }
        @SuppressWarnings("unchecked")
        IModel<ComplexMetadataMap> metadataModel = new Model<ComplexMetadataMap>(
                new ComplexMetadataMapImpl((HashMap<String, Serializable>) custom));
        this.add(new MetadataPanel("metadataPanel", metadataModel));
    }


}
