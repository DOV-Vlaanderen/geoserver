/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.layer.resource;

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

    public MetadataTabPanel(String id, IModel<ResourceInfo> model) {
        super(id, model);

        IModel<ComplexMetadataMap> metadataModel = new Model<ComplexMetadataMap>(
                new ComplexMetadataMapImpl(model.getObject().getMetadata()));
        this.add(new MetadataPanel("metadataPanel", metadataModel));
    }


}
