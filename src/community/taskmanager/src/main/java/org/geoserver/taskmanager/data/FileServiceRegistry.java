/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.data;


import java.util.List;


/**
 * Registry for easy access to the @FileService objects.
 * 
 * @author Timothy De Bock
 *
 */
public interface FileServiceRegistry {

    List<String> getFileServiceNames();

    FileService getService(String name);
}
