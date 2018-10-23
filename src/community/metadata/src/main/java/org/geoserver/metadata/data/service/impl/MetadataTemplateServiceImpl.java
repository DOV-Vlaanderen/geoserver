/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service.impl;

import org.geoserver.config.GeoServerDataDirectory;
import org.geoserver.config.GeoServerFactory;
import org.geoserver.config.impl.GeoServerImpl;
import org.geoserver.config.util.XStreamPersister;
import org.geoserver.config.util.XStreamPersisterFactory;
import org.geoserver.metadata.data.model.MetadataTemplate;
import org.geoserver.metadata.data.service.MetadataTemplateService;
import org.geoserver.platform.resource.Files;
import org.geoserver.platform.resource.Resource;
import org.geoserver.platform.resource.Resources;
import org.geotools.util.logging.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
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

    public MetadataTemplateServiceImpl() {
        this.persister = new XStreamPersisterFactory().createXMLPersister();
        this.persister.getXStream().allowTypesByWildcard(new String[]{"org.geoserver.metadata.data.model.**"});

    }

    private Resource getFolder() {
        return dataDirectory.get(MetaDataConstants.DIRECTORY);
    }

    @Override
    public List<MetadataTemplate> list() throws IOException {
        return readTemplates();
    }

    @Override
    public List<MetadataTemplate> listLinked(String workspace, String layerName) throws IOException {
        ArrayList<MetadataTemplate> results = new ArrayList<>();
        String key = getKey(workspace, layerName);
        for (MetadataTemplate template : list()) {
            if (template.getLinkedLayers() != null && template.getLinkedLayers().contains(key)){
                results.add(template);
            }
        }
        return results;
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
        /*TODO gans het gedoe met updaten van gelinkte metadata*/
        delete(metadataTemplate);

        List<MetadataTemplate> tempates = list();
        tempates.add(metadataTemplate);
        updateTemplates(tempates);
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
            } catch (EOFException exception) {
                LOGGER.warning("File is empty");
            }
        }
        return new ArrayList<>();
    }


    private void updateTemplates(List<MetadataTemplate> tempates) throws IOException {
        Resource folder = getFolder();
        Resource file = folder.get("templates.xml");

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

    @Override
    public void addLink(MetadataTemplate modelObject, String workspace, String layerName) throws IOException {
        String key = getKey(workspace, layerName);
        if(modelObject.getLinkedLayers() == null){
            modelObject.setLinkedLayers(new HashSet<>());
        }
        if (!modelObject.getLinkedLayers().contains(key)) {
            modelObject.getLinkedLayers().add(key);
        }
        update(modelObject);
    }

    @Override
    public void removeLink(MetadataTemplate modelObject, String workspace, String layerName) throws IOException {
        String key = getKey(workspace, layerName);
        if (modelObject.getLinkedLayers() != null && modelObject.getLinkedLayers().contains(key)) {
            modelObject.getLinkedLayers().remove(key);
        }
        update(modelObject);
    }


    private String getKey(String workspace, String layerName) {
        return workspace + ":" + layerName;
    }
}

