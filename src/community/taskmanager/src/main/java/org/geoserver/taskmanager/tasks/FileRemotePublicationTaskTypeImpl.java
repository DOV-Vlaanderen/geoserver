/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.tasks;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;

import javax.annotation.PostConstruct;

import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.StoreInfo;
import org.geoserver.platform.resource.Resources;
import org.geoserver.taskmanager.external.ExternalGS;
import org.geoserver.taskmanager.schedule.ParameterInfo;
import org.geoserver.taskmanager.schedule.ParameterType;
import org.geoserver.taskmanager.schedule.TaskContext;
import org.geoserver.taskmanager.schedule.TaskException;
import org.springframework.stereotype.Component;

import it.geosolutions.geoserver.rest.GeoServerRESTManager;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher.StoreType;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher.UploadMethod;
import it.geosolutions.geoserver.rest.encoder.GSAbstractStoreEncoder;
import it.geosolutions.geoserver.rest.encoder.utils.NestedElementEncoder;

@Component
public class FileRemotePublicationTaskTypeImpl extends AbstractRemotePublicationTaskTypeImpl {

    public static final String NAME = "RemoteFilePublication";

    public static final String PARAM_FILE = "file";

    @PostConstruct
    public void initParamInfo() {
        super.initParamInfo();
        paramInfo.put(PARAM_FILE, new ParameterInfo(PARAM_FILE, ParameterType.URI, false));
    }
    
    @Override
    protected boolean createStore(ExternalGS extGS, GeoServerRESTManager restManager,
            StoreInfo store, TaskContext ctx) throws IOException, TaskException {        
        final StoreType storeType = store instanceof CoverageStoreInfo ? StoreType.COVERAGESTORES
                : StoreType.DATASTORES;
        
        boolean upload = false;
        URI uri = (URI) ctx.getParameterValues().get(PARAM_FILE);
        if (uri == null) {
            try {
                uri = new URI(getLocation(store));
                upload = uri.getScheme().toLowerCase().equals("file");
            } catch (URISyntaxException e) {
                throw new IOException(e);
            }
        }
        if (upload) {
            final File file = Resources.fromURL(uri.toString()).file();
            return restManager.getPublisher().createStore(store.getWorkspace().getName(), storeType,
                    store.getName(), UploadMethod.FILE, store.getType().toLowerCase(),
                    Files.probeContentType(file.toPath()), file.toURI(), null);
        } else {
            return restManager.getStoreManager().create(store.getWorkspace().getName(), 
               new GSStoreEncoder(storeType, store.getWorkspace().getName(), store.getType(), 
                   store.getName(), uri.toString()));
        }
    }
    
    private String getLocation(StoreInfo storeInfo) {
        if (storeInfo instanceof CoverageStoreInfo) {
            return ((CoverageStoreInfo) storeInfo).getURL();
        } else {
            //this will work for shapefiles, which I believe is the only purely file-based
            //(non-database) vector store
            return ((DataStoreInfo) storeInfo).getConnectionParameters().get("url").toString();
        }
    }
    
    private static class GSStoreEncoder extends GSAbstractStoreEncoder {        
        private String type;
   
        protected GSStoreEncoder(StoreType storeType, String workspace, String type, String storeName, String url) {
            super(storeType, storeName);
            set("workspace", workspace);
            set("name", storeName);
            set("enabled", "true");
            set("type", this.type = type);
            if (storeType == StoreType.COVERAGESTORES) {
                set("url", url);
            } else {
                NestedElementEncoder connectionParameters = new NestedElementEncoder("connectionParameters");
                connectionParameters.set("url", url.toString());
                addContent(connectionParameters.getRoot());
            }
        }

        @Override
        protected String getValidType() {
            return type;
        }
    }

    @Override
    protected boolean mustCleanUpStore() {
        return false;
    }

    @Override
    public String getName() {
        return NAME;
    }


}
