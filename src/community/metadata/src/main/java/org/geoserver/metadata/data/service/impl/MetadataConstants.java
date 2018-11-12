/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service.impl;

import java.sql.Date;

import com.vividsolutions.jts.geom.Geometry;

public final class MetadataConstants {
    
    public final static String DIRECTORY = "metadata";

    public final static String CUSTOM_METADATA_KEY = "custom";

    public final static String DERIVED_KEY = "custom-derived-attributes";
    
    public final static String FEATURE_CATALOG_CONFIG_FILE = "featureCatalog.yaml";
    
    public final static String FEATURE_CATALOG = "object-catalog";
    
    public final static String FEATURE_CATALOG_TYPENAME = "objectCatalog";
    
    public final static String FEATURE_CATALOG_ATT_NAME = "name";
    
    public final static String FEATURE_CATALOG_ATT_TYPE = "type";
    
    public final static String FEATURE_CATALOG_ATT_MIN_OCCURENCE = "min-occurence";
    
    public final static String FEATURE_CATALOG_ATT_MAX_OCCURENCE = "max-occurence";
    
    public static final String FEATURE_CATALOG_ATT_DOMAIN = "domain";

    public final static String DOMAIN_TYPENAME = "domain";

    public static final String DOMAIN_ATT_VALUE = "value";
    
    
    public final static Class<?>[] FEATURE_CATALOG_KNOWN_TYPES = new Class<?>[] {
            String.class, Number.class, Geometry.class, Date.class
    };

    public static final String FEATURECATALOG_TYPE_UNKNOWN = "unknown";

    private MetadataConstants() {}
}
