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
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.geoserver.metadata.data.model.MetadataTemplate;
import org.geoserver.metadata.data.model.impl.ComplexMetadataMapImpl;
import org.geoserver.metadata.data.service.GeonetworkXmlParser;
import org.geoserver.metadata.data.service.MetadataTemplateService;
import org.geoserver.metadata.data.service.RemoteDocumentReader;
import org.geoserver.metadata.data.service.impl.MetadataConstants;
import org.geoserver.metadata.web.panel.ImportGeonetworkPanel;
import org.geoserver.metadata.web.panel.ImportTemplatePanel;
import org.geoserver.metadata.web.panel.MetadataPanel;
import org.geoserver.web.GeoServerApplication;
import org.geoserver.web.data.resource.ResourceConfigurationPage;
import org.geoserver.web.publish.PublishedEditTabPanel;
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

    private IModel<List<MetadataTemplate>> linkedTemplatesModel;

    private HashMap<String, List<Integer>> derivedAtts;

    public MetadataTabPanel(
            String id,
            IModel<LayerInfo> model,
            IModel<List<MetadataTemplate>> linkedTemplatesModel) {
        super(id, model);
        this.linkedTemplatesModel = linkedTemplatesModel;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onInitialize() {
        super.onInitialize();
        ResourceInfo resource = findParent(ResourceConfigurationPage.class).getResourceInfo();
        derivedAtts =
                (HashMap<String, List<Integer>>)
                        resource.getMetadata().get(MetadataConstants.DERIVED_KEY);

        Serializable custom = resource.getMetadata().get(MetadataConstants.CUSTOM_METADATA_KEY);
        if (!(custom instanceof HashMap<?, ?>)) {
            custom = new HashMap<String, Serializable>();
            resource.getMetadata().put(MetadataConstants.CUSTOM_METADATA_KEY, custom);
        }
        if (!(derivedAtts instanceof HashMap<?, ?>)) {
            derivedAtts = new HashMap<String, List<Integer>>();
            resource.getMetadata().put(MetadataConstants.DERIVED_KEY, derivedAtts);
        }

        IModel<ComplexMetadataMap> metadataModel =
                new Model<ComplexMetadataMap>(
                        new ComplexMetadataMapImpl((HashMap<String, Serializable>) custom));

        String resourceId = resource.getId();
        /*if (getDefaultModelObject() instanceof LayerInfo) {
            layerId = ((LayerInfo) getDefaultModelObject()).getId();
        } else {
            LOGGER.severe("Could not find layer id for:" + resource.getName());
        }*/

        // Link with templates panel
        this.add(
                new ImportTemplatePanel(
                        "importTemplatePanel",
                        resourceId,
                        metadataModel,
                        (IModel<List<MetadataTemplate>>) linkedTemplatesModel,
                        derivedAtts) {
                    private static final long serialVersionUID = -8056914656580115202L;

                    @Override
                    protected void handleUpdate(AjaxRequestTarget target) {
                        target.add(
                                metadataPanel()
                                        .replaceWith(
                                                new MetadataPanel(
                                                        "metadataPanel",
                                                        metadataModel,
                                                        derivedAtts)));
                    }
                });

        add(new MetadataPanel("metadataPanel", metadataModel, derivedAtts).setOutputMarkupId(true));

        // Geonetwork import panel
        ImportGeonetworkPanel geonetworkPanel =
                new ImportGeonetworkPanel("geonetworkPanel") {
                    private static final long serialVersionUID = -4620394948554985874L;

                    @Override
                    public void handleImport(String url, AjaxRequestTarget target) {
                        try {
                            // First unlink all templates
                            importTemplatePanel()
                                    .unlinkTemplate(
                                            target, importTemplatePanel().getLinkedTemplates());
                            // Read the file
                            RemoteDocumentReader geonetworkReader =
                                    GeoServerApplication.get()
                                            .getApplicationContext()
                                            .getBean(RemoteDocumentReader.class);
                            GeonetworkXmlParser xmlParser =
                                    GeoServerApplication.get()
                                            .getApplicationContext()
                                            .getBean(GeonetworkXmlParser.class);
                            // import metadata
                            Document doc = geonetworkReader.readDocument(new URL(url));
                            xmlParser.parseMetadata(doc, metadataModel.getObject());
                        } catch (IOException e) {
                            LOGGER.severe(e.getMessage());
                            getPage().error(e.getMessage());
                        }
                        target.add(
                                metadataPanel()
                                        .replaceWith(
                                                new MetadataPanel(
                                                        "metadataPanel",
                                                        metadataModel,
                                                        derivedAtts)));
                    }
                };
        add(geonetworkPanel);
    }

    protected MetadataPanel metadataPanel() {
        return (MetadataPanel) get("metadataPanel");
    }

    protected ImportTemplatePanel importTemplatePanel() {
        return (ImportTemplatePanel) get("importTemplatePanel");
    }

    @Override
    public void save() throws IOException {
        MetadataTemplateService service =
                GeoServerApplication.get()
                        .getApplicationContext()
                        .getBean(MetadataTemplateService.class);
        try {
            for (MetadataTemplate template : linkedTemplatesModel.getObject()) {
                service.update(template);
            }
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
    }
}
