/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.external;

import org.geoserver.taskmanager.util.SqlUtil;

/**
 * Default implementation for the Dialect interface.
 */
public class DefaultDialectImpl implements Dialect {

    @Override
    public String quote(String tableName) {
        return SqlUtil.quote(tableName);
    }
}
