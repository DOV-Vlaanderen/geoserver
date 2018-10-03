/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.layer.resource;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.metadata.web.panel.MetadataPanel;
import org.geoserver.web.data.resource.ResourceConfigurationPanel;

/**
 * A generic configuration panel for all basic ResourceInfo properties
 */
public class MetadataTabPanel extends ResourceConfigurationPanel  {

    private static final long serialVersionUID = -552158739086379566L;

    public MetadataTabPanel(String id, IModel<ResourceInfo> model) {
        super(id, model);

        IModel<MetadataMap> metadataModel = new CompoundPropertyModel<MetadataMap>(model.getObject().getMetadata());
        this.add(new MetadataPanel("metadataPanel", metadataModel));
    }


}
