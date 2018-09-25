/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;

import org.apache.wicket.model.IModel;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.metadata.web.panel.attribute.AttributeDataProvider;
import org.geoserver.metadata.web.panel.attribute.AttributesTablePanel;
import org.geotools.util.logging.Logging;

import java.util.logging.Logger;

public class MetadataPanel extends Panel {
    private static final long serialVersionUID = 1297739738862860160L;

    private static final Logger LOGGER = Logging.getLogger(MetadataPanel.class);


    private final IModel<MetadataMap> metadataModel;

    private ImportGeonetworkPanel geonetworkPanel;

    private boolean geonetworkPanelVisible = true;


    public MetadataPanel(String id, IModel<MetadataMap> metadataModel) {
        super(id);
        this.metadataModel = metadataModel;
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        this.setOutputMarkupId(true);
        geonetworkPanel = new ImportGeonetworkPanel("geonetworkPanel"){
            @Override
            public void handleImport(String url, AjaxRequestTarget target) {
                metadataModel.getObject().put("postcode", "negenduust");
                target.add(MetadataPanel.this);
            }
        };
        add(geonetworkPanel);
        geonetworkPanel.setVisible(geonetworkPanelVisible);
        //the attributes panel
        add(new AttributesTablePanel("attributesPanel", new AttributeDataProvider(), metadataModel));

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
