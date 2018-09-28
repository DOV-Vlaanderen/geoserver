// Copyright (C) 2010-2011 DOV, http://dov.vlaanderen.be/
// All rights reserved
package org.geoserver.metadata.data.service;

import org.geoserver.metadata.data.dto.AttributeMappingConfiguration;
import org.geoserver.metadata.data.dto.MetadataEditorConfiguration;

import java.io.IOException;

/**
 * Service responsible for interaction with yaml files. It will search for all *.yaml files in a given directory and try
 * to parse the files. Yaml files that cannot do parsed will be ignored.
 *
 * @author Timothy De Bock
 */
@SuppressWarnings("UnusedDeclaration")
public interface YamlService {

    MetadataEditorConfiguration readConfiguration(String folder) throws IOException;

    AttributeMappingConfiguration readMapping(String folder) throws IOException;


}
