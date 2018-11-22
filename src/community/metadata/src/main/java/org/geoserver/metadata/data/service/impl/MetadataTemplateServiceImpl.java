/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service.impl;

import com.thoughtworks.xstream.io.StreamException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerDataDirectory;
import org.geoserver.config.util.XStreamPersister;
import org.geoserver.config.util.XStreamPersisterFactory;
import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.geoserver.metadata.data.model.MetadataTemplate;
import org.geoserver.metadata.data.model.impl.ComplexMetadataIndexReference;
import org.geoserver.metadata.data.model.impl.ComplexMetadataMapImpl;
import org.geoserver.metadata.data.model.impl.MetadataTemplateImpl;
import org.geoserver.metadata.data.service.ComplexMetadataService;
import org.geoserver.metadata.data.service.MetadataTemplateService;
import org.geoserver.platform.resource.Files;
import org.geoserver.platform.resource.Resource;
import org.geoserver.platform.resource.Resources;
import org.geotools.util.logging.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Service that manages the list of templates. When the config of a template is updated all linked
 * metadata is also updated.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
@Component
public class MetadataTemplateServiceImpl implements MetadataTemplateService {

    private static final Logger LOGGER = Logging.getLogger(MetadataTemplateServiceImpl.class);

    XStreamPersister persister;

    private static String FILE_NAME = "templates.xml";

    @Autowired private GeoServerDataDirectory dataDirectory;

    @Autowired private ComplexMetadataService metadataService;

    // TODO is this correct?
    @Autowired protected GeoServer geoServer;

    public MetadataTemplateServiceImpl() {
        this.persister = new XStreamPersisterFactory().createXMLPersister();
        this.persister
                .getXStream()
                .allowTypesByWildcard(new String[] {"org.geoserver.metadata.data.model.**"});
        this.persister.getXStream().processAnnotations(MetadataTemplateImpl.class);
        this.persister.getXStream().processAnnotations(ComplexMetadataMapImpl.class);
        this.persister.getXStream().processAnnotations(ComplexMetadataIndexReference.class);
    }

    private Resource getFolder() {
        return dataDirectory.get(MetadataConstants.TEMPLATES_DIRECTORY);
    }

    @Override
    public List<MetadataTemplate> list() {
        try {
            return readTemplates();
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public void save(MetadataTemplate metadataTemplate) throws IOException {
        if (metadataTemplate.getName() == null) {
            throw new IOException("Template with name required.");
        }

        List<MetadataTemplate> templates = list();
        for (MetadataTemplate tempate : templates) {
            if (tempate.getName().equals(metadataTemplate.getName())) {
                throw new IOException(
                        "Template with name " + metadataTemplate.getName() + "already exists");
            }
        }
        templates.add(metadataTemplate);
        updateTemplates(templates);
    }

    @Override
    public void update(MetadataTemplate metadataTemplate) throws IOException {
        List<MetadataTemplate> templates = list();

        int index = -1;
        for (MetadataTemplate template : templates) {
            if (template.getName().equals(metadataTemplate.getName())) {
                index = templates.indexOf(template);
            }
        }
        if (index != -1) {
            templates.remove(index);
            templates.add(index, metadataTemplate);

            updateTemplates(templates);
            // update layers
            if (metadataTemplate.getLinkedLayers() != null) {
                for (String key : metadataTemplate.getLinkedLayers()) {
                    LayerInfo layer = geoServer.getCatalog().getLayerByName(key);

                    if (layer != null) {
                        @SuppressWarnings("unchecked")
                        HashMap<String, List<Integer>> derivedAtts =
                                (HashMap<String, List<Integer>>)
                                        layer.getResource()
                                                .getMetadata()
                                                .get(MetadataConstants.DERIVED_KEY);

                        Serializable custom =
                                layer.getResource()
                                        .getMetadata()
                                        .get(MetadataConstants.CUSTOM_METADATA_KEY);
                        @SuppressWarnings("unchecked")
                        ComplexMetadataMapImpl model =
                                new ComplexMetadataMapImpl((HashMap<String, Serializable>) custom);

                        ArrayList<ComplexMetadataMap> sources = new ArrayList<>();
                        for (MetadataTemplate template : templates) {
                            if (template.getLinkedLayers() != null
                                    && template.getLinkedLayers().contains(key)) {
                                sources.add(template.getMetadata());
                            }
                        }

                        metadataService.merge(model, sources, derivedAtts);

                        geoServer.getCatalog().save(layer);
                    } else {
                        LOGGER.severe("Update metadata for linked layer failed: " + key);
                    }
                }
            }
        } else {
            throw new IOException(
                    "The template "
                            + metadataTemplate.getName()
                            + " was not found and could not be updated.");
        }
    }

    @Override
    public MetadataTemplate load(String templateName) {
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
        List<MetadataTemplate> templates = list();
        MetadataTemplate toDelete = null;
        for (MetadataTemplate tempate : templates) {
            if (tempate.getName().equals(metadataTemplate.getName())) {
                toDelete = tempate;
                break;
            }
        }
        if (toDelete != null) {
            if (toDelete.getLinkedLayers() == null || toDelete.getLinkedLayers().isEmpty()) {
                templates.remove(toDelete);
                updateTemplates(templates);
            } else {
                throw new IOException("The template is still linked.");
            }
        }
    }

    @Override
    public void increasePriority(MetadataTemplate template) {
        try {
            List<MetadataTemplate> templates = list();
            int index = getIndex(template, templates);
            templates.remove(index);
            templates.add(index - 1, template);
            updateTemplates(templates);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    @Override
    public void decreasePriority(MetadataTemplate template) {
        try {
            List<MetadataTemplate> templates = list();
            int index = getIndex(template, templates);
            templates.remove(index);
            templates.add(index + 1, template);
            updateTemplates(templates);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private List<MetadataTemplate> readTemplates() throws IOException {
        Resource folder = getFolder();
        Resource file = folder.get(FILE_NAME);

        if (file != null) {
            try (InputStream in = file.in()) {
                List<MetadataTemplate> list = persister.load(in, List.class);
                for (int i = 0; i < list.size(); i++) {
                    MetadataTemplate template = list.get(i);
                    template.setOrder(i);
                }
                return list;
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
            File fileResource =
                    Resources.createNewFile(Files.asResource(new File(folder.dir(), FILE_NAME)));
            file = Files.asResource(fileResource);
        }
        try (OutputStream out = file.out()) {
            persister.save(tempates, out);
        }
    }

    private int getIndex(MetadataTemplate template, List<MetadataTemplate> templates) {
        for (int i = 0; i < templates.size(); i++) {
            MetadataTemplate current = templates.get(i);
            if (template.getName().equals(current.getName())) {
                return i;
            }
        }
        return -1;
    }
}
