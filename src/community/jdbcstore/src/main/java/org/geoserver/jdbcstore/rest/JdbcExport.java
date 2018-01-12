package org.geoserver.jdbcstore.rest;

import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CatalogInfo;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.MapInfo;
import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.catalog.StoreInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.catalog.event.impl.CatalogAddEventImpl;
import org.geoserver.catalog.event.impl.CatalogModifyEventImpl;
import org.geoserver.catalog.util.CloseableIterator;
import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerPersister;
import org.geoserver.config.ServiceInfo;
import org.geoserver.config.ServicePersister;
import org.geoserver.config.util.XStreamPersisterFactory;
import org.geoserver.config.util.XStreamServiceLoader;
import org.geoserver.jdbcstore.cache.ResourceCache;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.platform.resource.Resource;
import org.geoserver.platform.resource.Resource.Type;
import org.geoserver.security.impl.GeoServerRole;
import org.geotools.util.logging.Logging;
import org.opengis.filter.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

@Controller("/jdbcexport")
public class JdbcExport {

    static Logger LOGGER = Logging.getLogger( "org.geoserver" );

    @Autowired
    GeoServer gs;

    @Autowired
    ResourceCache cache;

    @Autowired
    Catalog catalog;

    @Autowired
    GeoServerResourceLoader loader;


    private <T extends CatalogInfo> void exportType(Class<T> clazz, GeoServerPersister gp) {
        CloseableIterator<T> it = catalog.list(clazz, Filter.INCLUDE);
        while (it.hasNext()) {
            CatalogAddEventImpl event = new CatalogAddEventImpl();
            event.setSource(it.next());
            gp.handleAddEvent(event);
        }
    }

    @RequestMapping(value = "/config", method = RequestMethod.GET)
    public String executeConfig() throws IOException {
        if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(GeoServerRole.ADMIN_ROLE)) {
            throw new AccessDeniedException("You must be administrator.");
        }

        //export jdbc config 

        GeoServerPersister gp = new GeoServerPersister(loader,
                new XStreamPersisterFactory().createXMLPersister());

        //download jdbc catalog
        exportType(NamespaceInfo.class, gp);
        exportType(WorkspaceInfo.class, gp);
        exportType(StoreInfo.class, gp);
        exportType(ResourceInfo.class, gp);
        exportType(LayerInfo.class, gp);
        exportType(LayerGroupInfo.class, gp);
        exportType(LayerInfo.class, gp);
        exportType(LayerGroupInfo.class, gp);
        exportType(StyleInfo.class, gp);
        exportType(MapInfo.class, gp);
        gp.handlePostGlobalChange(gs.getGlobal());
        gp.handlePostLoggingChange(gs.getLogging());
        gp.handleSettingsPostModified(gs.getSettings());
        CatalogModifyEventImpl event = new CatalogModifyEventImpl();
        event.setSource(catalog);
        event.setPropertyNames(Collections.singletonList("defaultWorkspace"));
        event.setNewValues(Collections.singletonList(catalog.getDefaultWorkspace()));
        gp.handleModifyEvent(event);

        @SuppressWarnings("rawtypes")
        final List<XStreamServiceLoader> loaders =
                GeoServerExtensions.extensions(XStreamServiceLoader.class);
        ServicePersister sp = new ServicePersister(loaders, gs);
        for (ServiceInfo si : gs.getServices()) {
            sp.handlePostServiceChange(si);
        }

        return "/";
    }

    @RequestMapping(value = "/store", method = RequestMethod.GET)
    public String executeStore() throws IOException {
        if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(GeoServerRole.ADMIN_ROLE)) {
            throw new AccessDeniedException("You must be administrator.");
        }

        //export jdbc store

        cache(loader.get("/"));

        return "/";
    }

    private void cache(Resource res) throws IOException {
        if (res.getType() == Type.DIRECTORY) {
            res.dir();
            for (Resource child : res.list()) {
                cache(child);
            };
        } else {
            res.file();
        }
    }

}