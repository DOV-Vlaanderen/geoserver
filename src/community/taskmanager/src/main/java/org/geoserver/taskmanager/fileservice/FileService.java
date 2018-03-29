/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.fileservice;

import org.geoserver.taskmanager.util.Named;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;


/**
 * Persist and read files.
 * 
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 *
 */
public interface FileService extends Serializable, Named {
    
    /**
     * User-friendly description of this file service. 
     * 
     * @return description
     */
    public String getDescription();

    /**
     * List existing sub folders in this file service.
     * 
     * @return list of existing sub folders
     * @throws IOException
     */
    List<Path> listSubfolders() throws IOException;

    /**
     * Create a file in the file service
     * 
     * @param filePath the path of the file, relative to this service
     * @param content the content of the file
     * @return a location string that can be used to configure a Geoserver store
     * @throws IOException
     */
    String create(Path filePath, InputStream content) throws IOException;

    /**
     * Check if this file exists.
     * 
     * @param filePath the path of the file, relative to this service
     * @return whether the file exists
     * @throws IOException
     */
    boolean checkFileExists(Path filePath) throws IOException;

    /**
     * Delete this file.
     * 
     * @param filePath the path of the file, relative to this service
     * @return whether anything was actually deleted.
     * @throws IOException
     */
    boolean delete(Path filePath) throws IOException;

    /**
     * Read this file.
     * 
     * @param filePath the path of the file, relative to this service
     * @return inputstream with data
     * @throws IOException
     */
    InputStream read(Path filePath) throws IOException;
}
