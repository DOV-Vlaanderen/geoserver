/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.external;


/**
 * Generic implementation for the Dialect interface.
 * This should work with moest databases. But is also limits the functionality of the taskmanager;
 */
public class GenericDialectImpl implements Dialect {

    /**
     * Do not quote table names since this not supported by all db's.
     *
     * @param tableName
     * @return
     */
    @Override
    public String quote(String tableName) {
        return tableName;
    }


    @Override
    public String sqlRenameView(String currentViewName, String newViewName) {
        return "ALTER VIEW " + currentViewName + " RENAME TO " + newViewName;
    }
}
