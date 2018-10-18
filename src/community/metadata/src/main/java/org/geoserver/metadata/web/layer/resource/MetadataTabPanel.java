/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.layer.resource;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Logger;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.geoserver.metadata.data.model.impl.ComplexMetadataMapImpl;
import org.geoserver.metadata.data.service.GeonetworkXmlParser;
import org.geoserver.metadata.data.service.RemoteDocumentReader;
import org.geoserver.metadata.web.panel.ImportGeonetworkPanel;
import org.geoserver.metadata.web.panel.ImportTemplatePanel;
import org.geoserver.metadata.web.panel.MetadataPanel;
import org.geoserver.metadata.web.panel.attribute.AttributeDataProvider;
import org.geoserver.metadata.web.panel.attribute.AttributesTablePanel;
import org.geoserver.web.GeoServerApplication;
import org.geoserver.web.publish.PublishedEditTabPanel;
import org.geotools.util.logging.Logging;
import org.w3c.dom.Document;

/**
 * A generic configuration panel for all basic ResourceInfo properties
 */
public class MetadataTabPanel extends PublishedEditTabPanel<LayerInfo> {

    private static final long serialVersionUID = -552158739086379566L;
    
    private static final Logger LOGGER = Logging.getLogger(MetadataTabPanel.class);

    public final static String CUSTOM_METADATA_KEY = "custom";

    public MetadataTabPanel(String id, IModel<LayerInfo> model/*, IModel<ResourceInfo> resourceModel*/) {
        super(id, model);

        Serializable custom = model.getObject().getResource().getMetadata().get(CUSTOM_METADATA_KEY);
        if (!(custom instanceof HashMap<?, ?>)) {
            custom = new HashMap<String, Serializable>();
            model.getObject().getResource().getMetadata().put(CUSTOM_METADATA_KEY, custom);
        }


        @SuppressWarnings("unchecked")
        IModel<ComplexMetadataMap> metadataModel = new Model<ComplexMetadataMap>(
                new ComplexMetadataMapImpl((HashMap<String, Serializable>) custom));


        this.add(new ImportTemplatePanel("importTemplatePanel", metadataModel));

        MetadataPanel metadataPanel = new MetadataPanel("metadataPanel", metadataModel);
        this.add(metadataPanel);




        //Geonetwork import panel
        ImportGeonetworkPanel geonetworkPanel = new ImportGeonetworkPanel("geonetworkPanel") {
            private static final long serialVersionUID = -4620394948554985874L;

            @Override
            public void handleImport(String url, AjaxRequestTarget target) {
                RemoteDocumentReader geonetworkReader = GeoServerApplication.get().getApplicationContext().getBean(RemoteDocumentReader.class);
                GeonetworkXmlParser xmlParser = GeoServerApplication.get().getApplicationContext().getBean(GeonetworkXmlParser.class);
                //import metadata
                try {
                    Document doc = geonetworkReader.readDocument(new URL(url));
                    xmlParser.parseMetadata(doc, metadataModel.getObject());
                } catch (IOException e) {
                    LOGGER.severe(e.getMessage());
                    getPage().error(e.getMessage());
                }
                metadataPanel.replaceWith(new AttributesTablePanel("attributesPanel", new AttributeDataProvider(), metadataModel));
                target.add(metadataPanel);
                target.add(getPage());
            }
        };
        add(geonetworkPanel);
    }



}
