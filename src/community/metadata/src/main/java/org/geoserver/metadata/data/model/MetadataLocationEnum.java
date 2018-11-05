/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.model;

/**
 *
 * The keys where we find our metada in the MetadataMap.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public enum MetadataLocationEnum {
    CUSTOM_METADATA_KEY("custom"), CUSTOM_DESCRIPTION_KEY("descriptionMap");

    String key;

    MetadataLocationEnum(String code) {
        this.key = code;
    }

    public String getKey() {
        return key;
    }
}
