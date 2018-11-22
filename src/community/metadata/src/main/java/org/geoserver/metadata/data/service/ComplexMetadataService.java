/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service;

import java.util.HashMap;
import java.util.List;
import org.geoserver.metadata.data.model.ComplexMetadataMap;

/**
 * TODO consolidate methods.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public interface ComplexMetadataService {

    /**
     * The values in the template are applied in reverse order, i.e. the first child has the highest
     * priority.
     *
     * @param parent
     * @param children
     * @param derivedAtts
     */
    void merge(
            ComplexMetadataMap destination,
            List<ComplexMetadataMap> sources,
            HashMap<String, List<Integer>> derivedAtts);

    /**
     * Apply the values from the source to the target.
     *
     * @param destination
     * @param source
     * @param derivedAtts
     */
    void merge(
            ComplexMetadataMap destination,
            ComplexMetadataMap source,
            String typeName,
            HashMap<String, List<Integer>> derivedAtts);
}
