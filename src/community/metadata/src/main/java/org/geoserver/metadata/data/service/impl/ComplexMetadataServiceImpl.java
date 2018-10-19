/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service.impl;


import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.geoserver.metadata.data.service.ComplexMetadataService;
import org.geotools.util.logging.Logging;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.logging.Logger;

@Repository
public class ComplexMetadataServiceImpl implements ComplexMetadataService {


    private static final Logger LOGGER = Logging.getLogger(ComplexMetadataServiceImpl.class);


    @Override
    public void merge(ComplexMetadataMap parent, ComplexMetadataMap child) {

    }

}
