/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;

import org.apache.wicket.model.IModel;
import org.geoserver.metadata.data.ComplexMetadataMap;
import org.geoserver.metadata.data.service.GeonetworkXmlParser;
import org.geoserver.metadata.data.service.RemoteDocumentReader;
import org.geoserver.metadata.web.panel.attribute.AttributeDataProvider;
import org.geoserver.metadata.web.panel.attribute.AttributesTablePanel;
import org.geoserver.platform.resource.URIs;
import org.geoserver.web.GeoServerApplication;
import org.geotools.util.logging.Logging;
import org.w3c.dom.Document;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

public class MetadataPanel extends Panel {
    private static final long serialVersionUID = 1297739738862860160L;

    private static final Logger LOGGER = Logging.getLogger(MetadataPanel.class);


    private ImportGeonetworkPanel geonetworkPanel;

    private boolean geonetworkPanelVisible = true;


    public MetadataPanel(String id, IModel<ComplexMetadataMap> metadataModel) {
        super(id, metadataModel);

    }


    @Override
    public void onInitialize() {
        super.onInitialize();
        this.setOutputMarkupId(true);
        geonetworkPanel = new ImportGeonetworkPanel("geonetworkPanel") {
            private static final long serialVersionUID = -4620394948554985874L;

            @Override
            public void handleImport(String url, AjaxRequestTarget target) {
                RemoteDocumentReader geonetworkReader = GeoServerApplication.get().getApplicationContext().getBean(RemoteDocumentReader.class);
                GeonetworkXmlParser xmlParser = GeoServerApplication.get().getApplicationContext().getBean(GeonetworkXmlParser.class);
                //import metadata
                try {
                    Document doc = geonetworkReader.readDocument(new URL(url));
                    xmlParser.parseMetadata(doc, getMetadataModel().getObject());
                } catch (IOException e) {
                    LOGGER.severe(e.getMessage());
                    getPage().error(e.getMessage());
                }
                target.add(MetadataPanel.this);
                target.add(getPage());
            }
        };
        add(geonetworkPanel);
        geonetworkPanel.setVisible(geonetworkPanelVisible);
        //the attributes panel
        add(new AttributesTablePanel("attributesPanel", new AttributeDataProvider(), 
                getMetadataModel()));

    }
    
    @SuppressWarnings("unchecked")
    public IModel<ComplexMetadataMap> getMetadataModel() {
        return (IModel<ComplexMetadataMap>) getDefaultModel();
    }


    public boolean isGeonetworkPanelVisible() {
        return geonetworkPanel.isVisible();
    }

    public void setGeonetworkPanelVisible(boolean geonetworkPanelVisible) {
        this.geonetworkPanelVisible = geonetworkPanelVisible;
        if (geonetworkPanel != null) {
            geonetworkPanel.setVisible(geonetworkPanelVisible);
        }
    }




}
