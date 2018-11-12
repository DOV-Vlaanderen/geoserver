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
        this.persister.getXStream().processAnnotations(MetadataTemplateImpl.class);
        this.persister.getXStream().processAnnotations(ComplexMetadataMapImpl.class);
        this.persister.getXStream().processAnnotations(ComplexMetadataIndexReference.class);

    }

    private Resource getFolder() {
        return dataDirectory.get(MetadataConstants.DIRECTORY);
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
                throw new IOException("Template with name " + metadataTemplate.getName() + "allready exists");
            }
        }
        templates.add(metadataTemplate);
        updateTemplates(templates);
    }


    @Override
    public void update(MetadataTemplate metadataTemplate) throws IOException {
        delete(metadataTemplate);

        List<MetadataTemplate> templates = list();
        templates.add(metadataTemplate);
        updateTemplates(templates);
        //update layers
        if (metadataTemplate.getLinkedLayers() != null) {
            for (String key : metadataTemplate.getLinkedLayers()) {
                LayerInfo layer = geoServer.getCatalog().getLayerByName(key);

                if (layer != null) {
                    @SuppressWarnings("unchecked")
                    HashMap<String, List<Integer>>  derivedAtts = (HashMap<String, List<Integer>>)
                            layer.getResource().getMetadata().get(MetadataConstants.DERIVED_KEY);

                    Serializable custom = layer.getResource().getMetadata().get(MetadataConstants.CUSTOM_METADATA_KEY);
                    @SuppressWarnings("unchecked")
                    ComplexMetadataMapImpl model = new ComplexMetadataMapImpl((HashMap<String, Serializable>) custom);

                    ArrayList<ComplexMetadataMap> sources = new ArrayList<>();
                    for (MetadataTemplate template : templates) {
                        if (template.getLinkedLayers() != null && template.getLinkedLayers().contains(key)) {
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
        templates.remove(toDelete);
        updateTemplates(templates);
    }

    @Override
    public void increasePriority(MetadataTemplate template) {
        try {
            List<MetadataTemplate> templates  = list();
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
            List<MetadataTemplate> templates  = list();
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
            InputStream in = file.in();
            try {
                List<MetadataTemplate> templates = persister.load(in, List.class);
                return templates;
            } catch (StreamException exception) {
                LOGGER.warning("File is empty");
            } finally {
                in.close();
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

