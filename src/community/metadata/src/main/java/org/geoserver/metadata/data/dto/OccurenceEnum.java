/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.dto;

/**
 * Object that matches in fixed choice in the yaml structure.
 *
 * Choose if the field is repeatable or not.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public enum OccurenceEnum {
    SINGLE, REPEAT;

    public static OccurenceEnum fromCode(String code) {
        for (OccurenceEnum c : OccurenceEnum.values()) {
            if (c.name().equals(code)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Can not create a status enum from code 'null'.");
    }
}
