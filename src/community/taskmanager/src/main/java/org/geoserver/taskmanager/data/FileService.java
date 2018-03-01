/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;


/**
 * Persist and read files.
 * 
 * @author Timothy De Bock
 *
 */
public interface FileService extends Serializable {

    String getName();

    boolean checkFileExists(Path filePath) throws IOException;

    void create(Path filePath, InputStream content) throws IOException;

    boolean delete(Path filePath) throws IOException;

    InputStream read(Path filePath) throws IOException;

    List<Path> listSubfolders() throws IOException;
}
