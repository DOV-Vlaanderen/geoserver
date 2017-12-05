package org.geoserver.taskmanager.external;

import java.net.MalformedURLException;
import java.net.URL;

import org.geoserver.taskmanager.util.Named;

import it.geosolutions.geoserver.rest.GeoServerRESTManager;

public interface ExternalGS extends Named {

    String getUrl();

    String getUsername();

    String getPassword();

    default GeoServerRESTManager getRESTManager() throws MalformedURLException {
        return new GeoServerRESTManager(new URL(getUrl()), getUsername(), getPassword());
    }

}