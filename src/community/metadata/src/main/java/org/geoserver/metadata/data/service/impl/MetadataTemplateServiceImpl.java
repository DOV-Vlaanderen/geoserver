/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service.impl;

import com.thoughtworks.xstream.io.StreamException;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerDataDirectory;
import org.geoserver.config.util.XStreamPersister;
import org.geoserver.config.util.XStreamPersisterFactory;
import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.geoserver.metadata.data.model.MetadataTemplate;
import org.geoserver.metadata.data.model.comparator.MetadataTemplateComparator;
import org.geoserver.metadata.data.model.impl.ComplexMetadataIndexReference;
import org.geoserver.metadata.data.model.impl.ComplexMetadataMapImpl;
import org.geoserver.metadata.data.service.ComplexMetadataService;
import org.geoserver.metadata.data.service.MetadataTemplateService;
import org.geoserver.metadata.web.layer.MetadataTabPanel;
import org.geoserver.platform.resource.Files;
import org.geoserver.platform.resource.Resource;
import org.geoserver.platform.resource.Resources;
import org.geotools.util.logging.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service that manages the list of templates.
 * When the config of a template is updated all linked metadata is also updated.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
@Component
public class MetadataTemplateServiceImpl implements MetadataTemplateService {


    private static final Logger LOGGER = Logging.getLogger(MetadataTemplateServiceImpl.class);

    XStreamPersister persister;

    private static String FILE_NAME = "templates.xml";

    @Autowired
    private GeoServerDataDirectory dataDirectory;

    @Autowired
    private ComplexMetadataService metadataService;

    //TODO is this correct?
    @Autowired
    protected GeoServer geoServer;

    public MetadataTemplateServiceImpl() {
        this.persister = new XStreamPersisterFactory().createXMLPersister();
        this.persister.getXStream().allowTypesByWildcard(new String[]{"org.geoserver.metadata.data.model.**"});
        this.persister.getXStream().processAnnotations(MetadataTemplate.class);
        this.persister.getXStream().processAnnotations(ComplexMetadataMapImpl.class);
        this.persister.getXStream().processAnnotations(ComplexMetadataIndexReference.class);

    }

    private Resource getFolder() {
        return dataDirectory.get(MetadataConstants.DIRECTORY);
    }

    @Override
    public List<MetadataTemplate> list() throws IOException {
        return readTemplates();
    }

    @Override
    public void save(MetadataTemplate metadataTemplate) throws IOException {
        if (metadataTemplate.getName() == null) {
            throw new IOException("Template with name required.");
        }

        List<MetadataTemplate> tempates = list();
        for (MetadataTemplate tempate : tempates) {
            if (tempate.getName().equals(metadataTemplate.getName())) {
                throw new IOException("Template with name " + metadataTemplate.getName() + "allready exists");
            }
        }
        tempates.add(metadataTemplate);
        updateTemplates(tempates);
    }


    @Override
    public void update(MetadataTemplate metadataTemplate) throws IOException {
        delete(metadataTemplate);

        List<MetadataTemplate> templates = list();
        templates.add(metadataTemplate);
        updateTemplates(templates);
        //update layers
        Collections.sort(templates, new MetadataTemplateComparator());

        if (metadataTemplate.getLinkedLayers() != null) {
            for (String key : metadataTemplate.getLinkedLayers()) {
                //TODO where did the workspace go?
                //TODO move the static variable to an Enum
                LayerInfo layer = geoServer.getCatalog().getLayerByName(key.split(":")[1]);

                if (layer != null) {
                    HashMap<String, List<Integer>>  descriptionMap = (HashMap<String, List<Integer>>)
                            layer.getResource().getMetadata().get(MetadataTabPanel.CUSTOM_DESCRIPTION_KEY);

                    Serializable custom = layer.getResource().getMetadata().get(MetadataTabPanel.CUSTOM_METADATA_KEY);
                    ComplexMetadataMapImpl model = new ComplexMetadataMapImpl((HashMap<String, Serializable>) custom);

                    ArrayList<ComplexMetadataMap> sources = new ArrayList<>();
                    for (MetadataTemplate template : templates) {
                        if (template.getLinkedLayers() != null && template.getLinkedLayers().contains(key)) {
                            sources.add(template.getMetadata());
                        }
                    }

                    metadataService.merge(model, sources, descriptionMap);

                    geoServer.getCatalog().save(layer);
                } else {
                    LOGGER.severe("Update metadata for linked layer failed: " + key);
                }
            }
        }

    }


    @Override
    public MetadataTemplate load(String templateName) throws IOException {
        List<MetadataTemplate> tempates = list();
        for (MetadataTemplate tempate : tempates) {
            if (tempate.getName().equals(templateName)) {
                return tempate;
            }
        }
        return null;
    }

    @Override
    public void delete(MetadataTemplate metadataTemplate) throws IOException {
        List<MetadataTemplate> tempates = list();
        MetadataTemplate toDelete = null;
        for (MetadataTemplate tempate : tempates) {
            if (tempate.getName().equals(metadataTemplate.getName())) {
                toDelete = tempate;
                break;
            }
        }
        tempates.remove(toDelete);
        updateTemplates(tempates);
    }


    @SuppressWarnings("unchecked")
    private List<MetadataTemplate> readTemplates() throws IOException {
        Resource folder = getFolder();
        Resource file = folder.get(FILE_NAME);

        if (file != null) {
            try {

                List<MetadataTemplate> tempates = persister.load(file.in(), List.class);
                return tempates;
            } catch (StreamException exception) {
                LOGGER.warning("File is empty");
            }
        }
        return new ArrayList<>();
    }


    private void updateTemplates(List<MetadataTemplate> tempates) throws IOException {
        Resource folder = getFolder();
        Resource file = folder.get(FILE_NAME);

        if (file == null) {
            File fileResource = Resources.createNewFile(Files.asResource(new File(folder.dir(), FILE_NAME)));
            file = Files.asResource(fileResource);
        }
        OutputStream out = file.out();
        try {
            persister.save(tempates, out);
        } finally {
            out.close();
        }

    }

}

