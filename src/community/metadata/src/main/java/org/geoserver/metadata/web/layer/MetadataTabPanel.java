/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.layer;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.geoserver.metadata.data.model.MetadataLocationEnum;
import org.geoserver.metadata.data.model.MetadataTemplate;
import org.geoserver.metadata.data.model.impl.ComplexMetadataMapImpl;
import org.geoserver.metadata.data.service.GeonetworkXmlParser;
import org.geoserver.metadata.data.service.RemoteDocumentReader;
import org.geoserver.metadata.data.service.impl.MetadataConstants;
import org.geoserver.metadata.web.panel.ImportGeonetworkPanel;
import org.geoserver.metadata.web.panel.ImportTemplateDataProvider;
import org.geoserver.metadata.web.panel.ImportTemplatePanel;
import org.geoserver.metadata.web.panel.MetadataPanel;
import org.geoserver.web.GeoServerApplication;
import org.geoserver.web.publish.PublishedEditTabPanel;
import org.geoserver.web.wicket.GeoServerDialog;
import org.geotools.util.logging.Logging;
import org.w3c.dom.Document;

/**
 * A tabpanel that adds the metadata configuration to the layer.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class MetadataTabPanel extends PublishedEditTabPanel<LayerInfo> {

    private static final long serialVersionUID = -552158739086379566L;
    
    private static final Logger LOGGER = Logging.getLogger(MetadataTabPanel.class);

    private ImportTemplatePanel linkTemplatePanel;

    private HashMap<String, List<Integer>> descriptionMap ;

    @SuppressWarnings("unchecked")
    public MetadataTabPanel(String id, IModel<LayerInfo> model, IModel<?> linkedTemplatesModel) {
        super(id, model);

        MetadataMap metadataMap = model.getObject().getResource().getMetadata();
        descriptionMap = (HashMap<String, List<Integer>>)
                        model.getObject().getResource().getMetadata().get(MetadataConstants.DESCRIPTION_KEY);

        Serializable custom = model.getObject().getResource().getMetadata().get(MetadataConstants.CUSTOM_METADATA_KEY);
        if (!(custom instanceof HashMap<?, ?>)) {
            custom = new HashMap<String, Serializable>();
            model.getObject().getResource().getMetadata().put(MetadataConstants.CUSTOM_METADATA_KEY, custom);
        }
        if (!(descriptionMap instanceof HashMap<?, ?>)) {
            descriptionMap = new HashMap<String, List<Integer>>();
            model.getObject().getResource().getMetadata().put(MetadataConstants.DESCRIPTION_KEY, descriptionMap);
        }


        IModel<ComplexMetadataMap> metadataModel = new Model<ComplexMetadataMap>(
                new ComplexMetadataMapImpl((HashMap<String, Serializable>) custom));


        String name = model.getObject().getResource().getNativeName();
        String workspace = model.getObject().getResource().getStore().getWorkspace().getName();

        //Link with templates panel
        linkTemplatePanel = new ImportTemplatePanel("importTemplatePanel",
                workspace,
                name,
                metadataModel,
                (IModel<List<MetadataTemplate>>) linkedTemplatesModel,
                descriptionMap) {
            private static final long serialVersionUID = -8056914656580115202L;

            @Override
            protected void handleUpdate(AjaxRequestTarget target) {
                target.add(metadataPanel().replaceWith(
                        new MetadataPanel("metadataPanel", metadataModel, descriptionMap)));
            }

        };


        this.add(linkTemplatePanel);

        add(new MetadataPanel("metadataPanel", metadataModel, descriptionMap).setOutputMarkupId(true));
        
        GeoServerDialog dialog =  new GeoServerDialog("dialog");
        dialog.setInitialHeight(100);
        add(dialog);

        //Geonetwork import panel
        ImportGeonetworkPanel geonetworkPanel = new ImportGeonetworkPanel("geonetworkPanel") {
            private static final long serialVersionUID = -4620394948554985874L;

            @Override
            public void handleImport(String url, AjaxRequestTarget target) {
                try {
                    //First unlink all templates
                    linkTemplatePanel.unlinkTemplate(target, linkTemplatePanel.getLinkedTemplates());
                    //Read the file
                    RemoteDocumentReader geonetworkReader = GeoServerApplication.get().getApplicationContext().getBean(RemoteDocumentReader.class);
                    GeonetworkXmlParser xmlParser = GeoServerApplication.get().getApplicationContext().getBean(GeonetworkXmlParser.class);
                    //import metadata
                    Document doc = geonetworkReader.readDocument(new URL(url));
                    xmlParser.parseMetadata(doc, metadataModel.getObject());
                } catch (IOException e) {
                    LOGGER.severe(e.getMessage());
                    getPage().error(e.getMessage());
                }
                target.add(metadataPanel().replaceWith(
                        new MetadataPanel("metadataPanel", metadataModel, descriptionMap)));
            }
        };
        add(geonetworkPanel);
    }
    
    protected MetadataPanel metadataPanel() {
        return (MetadataPanel) get("metadataPanel");
    }

    @Override
    public void save() throws IOException {
        linkTemplatePanel.save();
    }

}
