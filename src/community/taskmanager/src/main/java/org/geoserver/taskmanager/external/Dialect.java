/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.external;

/**
 * Dialect specific commands.
 * 
 * @author Timothy De Bock
 *
 */
public interface Dialect {


    /**
     * Put quots arround the schema name and the table name.
     *
     * @return the quote table name.
     */
    String quote(String tableName);

    String sqlRenameView(String currentViewName, String newViewName);
}
