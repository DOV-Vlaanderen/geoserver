/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service.impl;

import com.vividsolutions.jts.geom.Geometry;
import java.sql.Date;

public final class MetadataConstants {

    public static final String DIRECTORY = "metadata";

    public static final String TEMPLATES_DIRECTORY = "metadata-template";

    public static final String CUSTOM_METADATA_KEY = "custom";

    public static final String DERIVED_KEY = "custom-derived-attributes";

    public static final String FEATURE_CATALOG_CONFIG_FILE = "featureCatalog.yaml";

    public static final String FEATURE_CATALOG = "object-catalog";

    public static final String FEATURE_CATALOG_TYPENAME = "objectCatalog";

    public static final String FEATURE_CATALOG_ATT_NAME = "name";

    public static final String FEATURE_CATALOG_ATT_TYPE = "type";

    public static final String FEATURE_CATALOG_ATT_MIN_OCCURENCE = "min-occurence";

    public static final String FEATURE_CATALOG_ATT_MAX_OCCURENCE = "max-occurence";

    public static final String FEATURE_CATALOG_ATT_DOMAIN = "domain";

    public static final String DOMAIN_TYPENAME = "domain";

    public static final String DOMAIN_ATT_VALUE = "value";

    public static final Class<?>[] FEATURE_CATALOG_KNOWN_TYPES =
            new Class<?>[] {String.class, Number.class, Geometry.class, Date.class};

    public static final String FEATURECATALOG_TYPE_UNKNOWN = "unknown";

    private MetadataConstants() {}
}
