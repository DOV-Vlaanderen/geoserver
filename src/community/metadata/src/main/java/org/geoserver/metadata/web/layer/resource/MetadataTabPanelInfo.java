/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.layer.resource;

import org.geoserver.catalog.LayerInfo;
import org.geoserver.web.publish.PublishedEditTabPanelInfo;

public class MetadataTabPanelInfo extends PublishedEditTabPanelInfo<LayerInfo>  {

    private static final long serialVersionUID = 7092956796960461825L;

    @Override
    public Class<LayerInfo> getPublishedInfoClass() {
        return LayerInfo.class;
    }

}
