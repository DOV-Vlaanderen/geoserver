/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel;

import org.apache.wicket.model.IModel;
import org.geoserver.metadata.data.model.ComplexMetadataAttribute;
import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.geoserver.metadata.data.model.MetadataTemplate;
import org.geoserver.metadata.data.service.MetadataTemplateService;
import org.geoserver.web.GeoServerApplication;
import org.geoserver.web.wicket.GeoServerDataProvider;
import org.geotools.util.logging.Logging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ImportTemplateDataProvider extends GeoServerDataProvider<MetadataTemplate> {

    private static final java.util.logging.Logger LOGGER = Logging.getLogger(ImportTemplateDataProvider.class);

    private static String LINKED_TEMPLATES = "LINKED_TEMPLATES";

    private static final long serialVersionUID = -8246320435114536132L;

    public static final Property<MetadataTemplate> NAME = new BeanProperty<MetadataTemplate>("name", "name");

    public static final Property<MetadataTemplate> DESCRIPTION = new BeanProperty<MetadataTemplate>("description", "description");

    private final IModel<ComplexMetadataMap> metadataModel;

    public ImportTemplateDataProvider(IModel<ComplexMetadataMap> metadataModel) {
        this.metadataModel = metadataModel;
    }

    @Override
    protected List<Property<MetadataTemplate>> getProperties() {
        return Arrays.asList(NAME, DESCRIPTION);
    }

    @Override
    protected List<MetadataTemplate> getItems() {
        try {
            MetadataTemplateService service =
                    GeoServerApplication.get().getApplicationContext().getBean(MetadataTemplateService.class);


            int size = metadataModel.getObject().size(LINKED_TEMPLATES);
            List<String> currentLinks = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                currentLinks.add(metadataModel.getObject().get(String.class, LINKED_TEMPLATES, i).getValue());
            }

            List<MetadataTemplate> result = new ArrayList<>();
            for (MetadataTemplate metadataTemplate : service.list()) {
                if (currentLinks.contains(metadataTemplate.getName())) {
                    result.add(metadataTemplate);
                }
            }
            return result;
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
        return Collections.emptyList();
    }

    //TODO CHOOSE where to manage the links, in the layer,in the template services, or dedicated service
    public void addLink(MetadataTemplate modelObject) {
        int size = metadataModel.getObject().size(LINKED_TEMPLATES);
        metadataModel.getObject().get(String.class, LINKED_TEMPLATES, size).setValue(modelObject.getName());
    }
    public void removeLink(MetadataTemplate modelObject) {
        int size = metadataModel.getObject().size(LINKED_TEMPLATES);
        for (int i = 0; i < size; i++) {
            ComplexMetadataAttribute<String> value = metadataModel.getObject().get(String.class, LINKED_TEMPLATES, i);
            if(modelObject.getName().equals(value.getValue())){
                metadataModel.getObject().delete(LINKED_TEMPLATES, i);
                break;
            }
        }
    }

    public List<MetadataTemplate> getUnlinkedItems() {
        try {
            MetadataTemplateService service =
                    GeoServerApplication.get().getApplicationContext().getBean(MetadataTemplateService.class);


            int size = metadataModel.getObject().size(LINKED_TEMPLATES);
            List<String> currentLinks = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                currentLinks.add(metadataModel.getObject().get(String.class, LINKED_TEMPLATES, i).getValue());
            }

            List<MetadataTemplate> result = new ArrayList<>();
            for (MetadataTemplate metadataTemplate : service.list()) {
                if (!currentLinks.contains(metadataTemplate.getName())) {
                    result.add(metadataTemplate);
                }
            }
            return result;
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
        return Collections.emptyList();

    }
}
