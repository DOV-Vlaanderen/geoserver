/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.model.comparator;


import org.geoserver.metadata.data.model.MetadataTemplate;

import java.util.Comparator;

/**
 * Sort templates according to the priority.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class MetadataTemplateComparator implements Comparator<MetadataTemplate> {

    public int compare(MetadataTemplate obj1, MetadataTemplate obj2) {
        int priority1 = 0;
        if (obj1 != null) {
            priority1 = obj1.getPriority();
        }
        int priority2 = 0;
        if (obj2 != null) {
            priority2 = obj2.getPriority();
        }
        return Integer.compare(priority2, priority1);
    }
}