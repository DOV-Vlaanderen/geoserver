/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel;

import org.geoserver.metadata.data.model.MetadataTemplate;
import org.geoserver.metadata.data.service.MetadataTemplateService;
import org.geoserver.web.GeoServerApplication;
import org.geoserver.web.wicket.GeoServerDataProvider;
import org.geotools.util.logging.Logging;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * DataProvider that manages the list of linked templates for a layer.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class ImportTemplateDataProvider extends GeoServerDataProvider<MetadataTemplate> {

    private static final java.util.logging.Logger LOGGER = Logging.getLogger(ImportTemplateDataProvider.class);

    private static final long serialVersionUID = -8246320435114536132L;

    public static final Property<MetadataTemplate> NAME = new BeanProperty<MetadataTemplate>("name", "name");

    public static final Property<MetadataTemplate> DESCRIPTION = new BeanProperty<MetadataTemplate>("description", "description");

    private final String workspace;

    private final String layerName;

    public ImportTemplateDataProvider(String workspace, String layerName) {
        this.workspace = workspace;
        this.layerName = layerName;
        getService();
    }


    @Override
    protected List<Property<MetadataTemplate>> getProperties() {
        return Arrays.asList(NAME, DESCRIPTION);
    }

    @Override
    protected List<MetadataTemplate> getItems() {
        try {
            return getService().listLinked(workspace, layerName);
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    public void addLink(MetadataTemplate modelObject) throws IOException {
        getService().addLink(modelObject, workspace, layerName);
    }

    public void removeLink(MetadataTemplate modelObject) throws IOException {
        getService().removeLink(modelObject, workspace, layerName);
    }

    public List<MetadataTemplate> getUnlinkedItems() {
        try {
            List<MetadataTemplate> currentLinks = getService().listLinked(workspace, layerName);
            List<MetadataTemplate> result = getService().list();

            result.removeAll(currentLinks);
            return result;
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
        return Collections.emptyList();

    }


    private MetadataTemplateService getService() {
       return GeoServerApplication.get().getApplicationContext().getBean(MetadataTemplateService.class);
    }
}
