//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.geoserver.metadata.web;

import org.apache.wicket.model.CompoundPropertyModel;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.metadata.web.panel.MetadataPanel;
import org.geoserver.web.ComponentAuthorizer;
import org.geoserver.web.GeoServerBasePage;
import org.geotools.util.logging.Logging;

import java.util.logging.Logger;

public class MetadataTemplatePage extends GeoServerBasePage {

    private static final Logger LOGGER = Logging.getLogger(MetadataTemplatePage.class);

    private static final long serialVersionUID = 2273966783474224452L;

    public MetadataTemplatePage() {
    }

    public void onInitialize() {
        super.onInitialize();
        CompoundPropertyModel<MetadataMap> metadataModel = new CompoundPropertyModel<MetadataMap>(new MetadataMap());
        MetadataPanel metadataTemplatePanel = new MetadataPanel("metadataTemplatePanel", metadataModel);
        this.add(metadataTemplatePanel);
        metadataTemplatePanel.setGeonetworkPanelVisible(false);
    }

    protected ComponentAuthorizer getPageAuthorizer() {
        return ComponentAuthorizer.AUTHENTICATED;
    }
}
