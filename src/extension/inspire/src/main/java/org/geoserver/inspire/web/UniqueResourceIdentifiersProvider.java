/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.inspire.web;

import java.util.Arrays;
import java.util.List;
import org.geoserver.inspire.UniqueResourceIdentifier;
import org.geoserver.inspire.UniqueResourceIdentifiers;
import org.geoserver.web.wicket.GeoServerDataProvider;

@SuppressWarnings("serial")
public class UniqueResourceIdentifiersProvider
        extends GeoServerDataProvider<UniqueResourceIdentifier> {

    UniqueResourceIdentifiers items;

    public UniqueResourceIdentifiersProvider(UniqueResourceIdentifiers items) {
        this.items = items;
        setEditable(true);
    }

    @Override
    protected List<Property<UniqueResourceIdentifier>> getProperties() {
        return Arrays.asList(
                new BeanProperty<UniqueResourceIdentifier>("code", "code"),
                new BeanProperty<UniqueResourceIdentifier>("namespace", "namespace"),
                new BeanProperty<UniqueResourceIdentifier>("metadataURL", "metadataURL"),
                new PropertyPlaceholder<UniqueResourceIdentifier>("remove"));
    }

    @Override
    protected UniqueResourceIdentifiers getItems() {
        return items;
    }
}
