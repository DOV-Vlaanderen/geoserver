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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.geoserver.catalog.AttributeTypeInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.geoserver.metadata.data.model.MetadataLocationEnum;
import org.geoserver.metadata.data.model.MetadataTemplate;
import org.geoserver.metadata.data.model.impl.ComplexMetadataMapImpl;
import org.geoserver.metadata.data.service.ComplexMetadataService;
import org.geoserver.metadata.data.service.GeonetworkXmlParser;
import org.geoserver.metadata.data.service.RemoteDocumentReader;
import org.geoserver.metadata.data.service.impl.MetadataConstants;
import org.geoserver.metadata.web.panel.ImportGeonetworkPanel;
import org.geoserver.metadata.web.panel.ImportTemplatePanel;
import org.geoserver.metadata.web.panel.MetadataPanel;
import org.geoserver.web.GeoServerApplication;
import org.geoserver.web.publish.PublishedEditTabPanel;
import org.geoserver.web.wicket.GeoServerDialog;
import org.geoserver.web.wicket.ParamResourceModel;
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

    public final static String CUSTOM_METADATA_KEY = "custom";

    public final static String CUSTOM_DESCRIPTION_KEY = "descriptionMap";

    private ImportTemplatePanel linkTemplatePanel;

    HashMap<String, List<Integer>> descriptionMap ;

    public MetadataTabPanel(String id, IModel<LayerInfo> model, IModel<?> linkedTemplatesModel) {
        super(id, model);


        MetadataMap metadataMap = model.getObject().getResource().getMetadata();
        descriptionMap = (HashMap<String, List<Integer>>)
                metadataMap.get(MetadataLocationEnum.CUSTOM_DESCRIPTION_KEY.getKey());

        Serializable custom = metadataMap.get(MetadataLocationEnum.CUSTOM_METADATA_KEY.getKey());
        if (!(custom instanceof HashMap<?, ?>)) {
            custom = new HashMap<String, Serializable>();
            metadataMap.put(MetadataLocationEnum.CUSTOM_METADATA_KEY.getKey(), custom);
        }
        if (!(descriptionMap instanceof HashMap<?, ?>)) {
            descriptionMap = new HashMap<String, List<Integer>>();
            metadataMap.put(MetadataLocationEnum.CUSTOM_DESCRIPTION_KEY.getKey(), descriptionMap);
        }


        @SuppressWarnings("unchecked")
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

        add( new AjaxSubmitLink("generateFeatureCatalog") {

            private static final long serialVersionUID = -8488748673536090206L;
            
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                dialog.showOkCancel(target, 
                        new GeoServerDialog.DialogDelegate() {
                            private static final long serialVersionUID = -8716380894588651422L;

                            @Override
                            protected Component getContents(String id) {
                                return new Label(id, new ParamResourceModel(
                                        "confirmGenerate", 
                                        MetadataTabPanel.this));
                            }

                            @Override
                            protected boolean onSubmit(AjaxRequestTarget target,
                                    Component contents) {
                                generateFeatureCatalog(metadataModel.getObject());
                                target.add(metadataPanel().replaceWith(
                                        new MetadataPanel("metadataPanel", metadataModel, descriptionMap)));
                                return true;
                            }
                            
                        });                
            }
            
        }.setVisible(model.getObject().getResource() instanceof FeatureTypeInfo));


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
                target.add(metadataPanel().replaceWith(
                        new MetadataPanel("metadataPanel", metadataModel, descriptionMap)));
            }
        };
        add(geonetworkPanel);
    }
    
    protected MetadataPanel metadataPanel() {
        return (MetadataPanel) get("metadataPanel");
    }
    
    public void generateFeatureCatalog(ComplexMetadataMap metadata) {
        ComplexMetadataService service =
                GeoServerApplication.get().getApplicationContext().getBean(ComplexMetadataService.class);
        
        LayerInfo layerInfo = (LayerInfo) getDefaultModelObject();
        FeatureTypeInfo fti = (FeatureTypeInfo) layerInfo.getResource();
        
        //we will save the old details for attributes that still exist
        Map<String, ComplexMetadataMap> old = new HashMap<>();
        for(int i = 0; i < metadata.size(MetadataConstants.FEATURE_CATALOG); i++) {
            ComplexMetadataMap attMap 
                = metadata.subMap(MetadataConstants.FEATURE_CATALOG, i);            
            old.put(attMap.get(String.class, MetadataConstants.FEATURE_CATALOG_ATT_NAME).getValue(),
                    attMap.clone());
        }
        
        //clear everything and build again
        metadata.delete(MetadataConstants.FEATURE_CATALOG);
        int index = 0;
        try {
            for (AttributeTypeInfo att : fti.attributes()) {
                ComplexMetadataMap attMap 
                    = metadata.subMap(MetadataConstants.FEATURE_CATALOG, index++);
                
                ComplexMetadataMap oldMap = old.get(att.getName());
                if (oldMap != null) {
                    service.merge(attMap, oldMap, MetadataConstants.FEATURE_CATALOG_TYPENAME, descriptionMap);
                }
                
                attMap.get(String.class, MetadataConstants.FEATURE_CATALOG_ATT_NAME).setValue(
                        att.getName());
                if (att.getBinding() != null) {
                    String type = MetadataConstants.FEATURECATALOG_TYPE_UNKNOWN;
                    for (Class<?> clazz : MetadataConstants.FEATURE_CATALOG_KNOWN_TYPES) {
                        if (clazz.isAssignableFrom(att.getBinding())) {
                            type = clazz.getSimpleName();
                            break;
                        }
                    }
                    attMap.get(String.class, MetadataConstants.FEATURE_CATALOG_ATT_TYPE)
                        .setValue(type);
                }
                if (att.getLength() != null) {
                    attMap.get(Integer.class, MetadataConstants.FEATURE_CATALOG_ATT_TYPE).setValue(
                        att.getLength());      
                }
                attMap.get(Integer.class, MetadataConstants.FEATURE_CATALOG_ATT_MIN_OCCURENCE).setValue(
                        att.getMinOccurs());
                attMap.get(Integer.class, MetadataConstants.FEATURE_CATALOG_ATT_MAX_OCCURENCE).setValue(
                        att.getMaxOccurs());
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Could not read attributes for " + fti.getName(), 
                    e);
        }
    }

    @Override
    public void save() throws IOException {
        linkTemplatePanel.save();
    }

}
