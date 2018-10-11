/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service.impl;

import org.geoserver.config.GeoServerDataDirectory;
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
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Timothy De Bock
 */
@Component
public class MetadataTemplateServiceImpl implements MetadataTemplateService {


    private static final Logger LOGGER = Logging.getLogger(MetadataTemplateServiceImpl.class);


    @Autowired
    private GeoServerDataDirectory dataDirectory;

    private Resource getFolder() {
        return dataDirectory.get("metadata");
    }

    @Override
    public List<MetadataTemplate> list() throws IOException {
        return readTemplates();
    }


    @Override
    public void save(MetadataTemplate metadataTemplate) throws IOException {
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
            if(tempate.getName().equals(metadataTemplate.getName())){
                toDelete = tempate;
                break;
            }
        }
        tempates.remove(toDelete);
        updateTemplates(tempates);
    }


    private List<MetadataTemplate> readTemplates() throws IOException {
        Resource folder = getFolder();
        Resource file = folder.get("templates.config");

        if (file != null) {
            try {
                ObjectInputStream objectIn = new ObjectInputStream(file.in());
                Object obj = objectIn.readObject();
                if (obj instanceof List) {
                    return (List) obj;
                }
            } catch (ClassNotFoundException e) {
                LOGGER.severe(e.getMessage());
            } catch (EOFException exception){
                LOGGER.warning("File is empty");
            }
        }
        return new ArrayList<>();
    }


    private void updateTemplates(List<MetadataTemplate> tempates) throws IOException {
        Resource folder = getFolder();
        Resource file = folder.get("templates.config");

        if (file == null) {
            File fileResource = Resources.createNewFile(Files.asResource(new File(folder.dir(), "templates.config")));
            file = Files.asResource(fileResource);
        }

        ObjectOutputStream objectOut = new ObjectOutputStream(file.out());
        objectOut.writeObject(tempates);
        objectOut.close();

    }

}

