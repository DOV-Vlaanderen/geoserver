/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.web.model;

import java.util.Arrays;
import java.util.List;

import org.geoserver.taskmanager.data.Configuration;
import org.geoserver.web.wicket.GeoServerDataProvider;
import org.geoserver.taskmanager.util.TaskManagerBeans;

public class ConfigurationsModel extends GeoServerDataProvider<Configuration> {

    private static final long serialVersionUID = -8246320435114536132L;

    public static final Property<Configuration> WORKSPACE = new BeanProperty<Configuration>("workspace", "workspace");
    public static final Property<Configuration> NAME = new BeanProperty<Configuration>("name", "name");
    public static final Property<Configuration> DESCRIPTION = new BeanProperty<Configuration>("description", "description");

    private Boolean templates;
    
    public ConfigurationsModel(Boolean templates) {
        this.templates = templates;
    }

    @Override
    protected List<Property<Configuration>> getProperties() {
        return Arrays.asList(WORKSPACE, NAME, DESCRIPTION);
    }

    @Override
    protected List<Configuration> getItems() {
        return TaskManagerBeans.get().getDao().getConfigurations(templates);
    }

}
