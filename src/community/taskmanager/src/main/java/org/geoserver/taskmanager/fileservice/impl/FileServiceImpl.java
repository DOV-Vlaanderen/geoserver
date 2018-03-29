/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.fileservice.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.taskmanager.fileservice.FileService;
import org.springframework.web.context.ServletContextAware;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

/**
 * Local file storage.
 *
 * @author Timothy De Bock
 */
public class FileServiceImpl implements FileService, ServletContextAware {

    private static final long serialVersionUID = -1948411877746516243L;
    
    private Path dataDirectory;
    
    private Path rootFolder;
    
    private String name;
    
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return "Local File System: " + name;
    }

    public void setRootFolder(String rootFolder) {
        this.rootFolder = Paths.get(rootFolder);
    }

    @Override
    public boolean checkFileExists(String filePath) throws IOException {
        return Files.exists(getAbsolutePath(filePath));
    }

    @Override
    public String create(String filePath, InputStream content) throws IOException {
        //Check parameters
        if (content == null) {
            throw new IOException("Content of a file can not be null.");
        }
        if (filePath == null) {
            throw new IOException("Name of a file can not be null.");
        }
        if (checkFileExists(filePath)) {
            throw new IOException("The file already exists");
        }

        File targetFile = new File(getAbsolutePath(filePath).toUri());
        FileUtils.copyInputStreamToFile(content, targetFile);
        if (dataDirectory == null) {
            return getAbsolutePath(filePath).toString();
        } else {
            return dataDirectory.relativize(getAbsolutePath(filePath)).toString();
        }
    }

    @Override
    public boolean delete(String filePath) throws IOException {
        if (filePath == null) {
            throw new IOException("Name of a filePath can not be null.");
        }
        if (checkFileExists(filePath)) {
            File file = new File(getAbsolutePath(filePath).toUri());
            return file.delete();
        } else {
            return false;
        }
    }

    @Override
    public InputStream read(String filePath) throws IOException {
        if (checkFileExists(filePath)) {
            File file = new File(getAbsolutePath(filePath).toUri());
            return FileUtils.openInputStream(file);
        } else {
            throw new IOException("The file does not exit:" + filePath.toString());
        }
    }

    @Override
    public List<String> listSubfolders() throws IOException {
        if (rootFolder == null) {
            throw new IOException("No rootFolder is not configured in this file service.");
        }
        File file = new File(rootFolder.toUri());
        file.mkdirs();
        String[] folders = file.list(FileFilterUtils.directoryFileFilter());
        ArrayList<String> paths = new ArrayList<>();
        if (folders != null) {  
          for (String folder : folders) {
              paths.add(folder);
          }
        }
        return paths;
    }

    private Path getAbsolutePath(String file) throws IOException {
        if (rootFolder == null) {
            throw new IOException("No rootFolder is not configured in this file service.");
        }
        return rootFolder.resolve(Paths.get(file));
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        String dataDirectory = GeoServerResourceLoader.lookupGeoServerDataDirectory(servletContext);
        if (dataDirectory != null) {
            this.dataDirectory = Paths.get(dataDirectory);
        } else {
            throw new IllegalStateException("Unable to determine data directory");
        }
    }

    public String getRootFolder() {
        return rootFolder.toString();
    }

}
