/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;

import org.apache.wicket.model.IModel;
import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.geoserver.metadata.data.service.GeonetworkXmlParser;
import org.geoserver.metadata.data.service.RemoteDocumentReader;
import org.geoserver.metadata.web.panel.attribute.AttributeDataProvider;
import org.geoserver.metadata.web.panel.attribute.AttributesTablePanel;
import org.geoserver.web.GeoServerApplication;
import org.geotools.util.logging.Logging;
import org.w3c.dom.Document;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * The dynamically generated metadata input panel. All fields are added on the fly based on the yaml configuration.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class MetadataPanel extends Panel {
    private static final long serialVersionUID = 1297739738862860160L;

    private static final Logger LOGGER = Logging.getLogger(MetadataPanel.class);



    public MetadataPanel(String id, IModel<ComplexMetadataMap> metadataModel) {
        super(id, metadataModel);

    }


    @Override
    public void onInitialize() {
        super.onInitialize();
        //the attributes panel
        AttributesTablePanel attributesPanel =
                new AttributesTablePanel("attributesPanel", new AttributeDataProvider(), getMetadataModel());
        attributesPanel.setOutputMarkupId(true);
        add(attributesPanel);

    }
    
    @SuppressWarnings("unchecked")
    public IModel<ComplexMetadataMap> getMetadataModel() {
        return (IModel<ComplexMetadataMap>) getDefaultModel();
    }


}
