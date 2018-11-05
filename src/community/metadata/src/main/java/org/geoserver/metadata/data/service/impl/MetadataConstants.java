package org.geoserver.metadata.data.service.impl;

import java.sql.Date;

import com.vividsolutions.jts.geom.Geometry;

public final class MetadataConstants {
    
    public final static String DIRECTORY = "metadata";

    public final static String CUSTOM_METADATA_KEY = "custom";

    public final static String DESCRIPTION_KEY = "descriptionMap";
    
    public final static String FEATURE_CATALOG_CONFIG_FILE = "featureCatalog.yaml";
    
    public final static String FEATURE_CATALOG = "object-catalog";
    
    public final static String FEATURE_CATALOG_TYPENAME = "objectCatalog";
    
    public final static String FEATURE_CATALOG_ATT_NAME = "name";
    
    public final static String FEATURE_CATALOG_ATT_TYPE = "type";
    
    public final static String FEATURE_CATALOG_ATT_MIN_OCCURENCE = "min-occurence";
    
    public final static String FEATURE_CATALOG_ATT_MAX_OCCURENCE = "max-occurence";
    
    public final static Class<?>[] FEATURE_CATALOG_KNOWN_TYPES = new Class<?>[] {
            String.class, Number.class, Geometry.class, Date.class
    };

    public static final String FEATURECATALOG_TYPE_UNKNOWN = "unknown";
    
    private MetadataConstants() {}
}
