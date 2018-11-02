/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel;

import org.apache.wicket.model.IModel;
import org.geoserver.metadata.data.model.MetadataTemplate;
import org.geoserver.metadata.data.model.comparator.MetadataTemplateComparator;
import org.geoserver.web.wicket.GeoServerDataProvider;
import org.geotools.util.logging.Logging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
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

    private List<MetadataTemplate> allTemplates = new ArrayList<>();

    private List<MetadataTemplate> linkedTemplates = new ArrayList<>();

    public ImportTemplateDataProvider(String workspace, String layerName, IModel<?> templatesModel) {
        this.workspace = workspace;
        this.layerName = layerName;

        allTemplates = (List<MetadataTemplate>) templatesModel.getObject();

        for (MetadataTemplate template : allTemplates) {
            if (template.getLinkedLayers() != null &&
                    template.getLinkedLayers().contains(getKey(workspace, layerName))) {
                linkedTemplates.add(template);
            }
        }

    }


    @Override
    protected List<Property<MetadataTemplate>> getProperties() {
        return Arrays.asList(NAME, DESCRIPTION);
    }

    @Override
    protected List<MetadataTemplate> getItems() {
        return linkedTemplates;
    }

    public void addLink(MetadataTemplate modelObject) throws IOException {
        if (modelObject.getLinkedLayers() == null) {
            modelObject.setLinkedLayers(new HashSet<>());
        }
        modelObject.getLinkedLayers().add(getKey(workspace, layerName));
        linkedTemplates.add(modelObject);
    }

    public void removeLink(MetadataTemplate modelObject) throws IOException {
        if (modelObject.getLinkedLayers() == null) {
            modelObject.setLinkedLayers(new HashSet<>());
        }
        modelObject.getLinkedLayers().remove(getKey(workspace, layerName));
        linkedTemplates.remove(modelObject);
    }

    /**
     * The remain values are used in the dropdown.
     * @return
     */
    public List<MetadataTemplate> getUnlinkedItems() {
        List<MetadataTemplate> result = new ArrayList<>(allTemplates);
        result.removeAll(linkedTemplates);
        Collections.sort(linkedTemplates, new MetadataTemplateComparator());
        return result;
    }


    private String getKey(String workspace, String layerName) {
        return workspace + ":" + layerName;
    }
}
