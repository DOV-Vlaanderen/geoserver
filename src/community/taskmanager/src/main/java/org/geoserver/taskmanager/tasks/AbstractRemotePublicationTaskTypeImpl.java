/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.tasks;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.catalog.StoreInfo;
import org.geoserver.config.GeoServerDataDirectory;
import org.geoserver.taskmanager.data.Batch;
import org.geoserver.taskmanager.data.Task;
import org.geoserver.taskmanager.external.ExtTypes;
import org.geoserver.taskmanager.external.ExternalGS;
import org.geoserver.taskmanager.schedule.ParameterInfo;
import org.geoserver.taskmanager.schedule.TaskException;
import org.geoserver.taskmanager.schedule.TaskResult;
import org.geoserver.taskmanager.schedule.TaskType;
import org.geotools.util.logging.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.geosolutions.geoserver.rest.GeoServerRESTManager;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher.Purge;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher.StoreType;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder.ProjectionPolicy;
import it.geosolutions.geoserver.rest.encoder.coverage.GSCoverageEncoder;
import it.geosolutions.geoserver.rest.encoder.feature.GSFeatureTypeEncoder;

@Component
public abstract class AbstractRemotePublicationTaskTypeImpl implements TaskType {

    public static final String PARAM_EXT_GS = "external-geoserver";

    public static final String PARAM_LAYER = "layer";

    protected final Map<String, ParameterInfo> paramInfo = new LinkedHashMap<String, ParameterInfo>();

    private static final Logger LOGGER = Logging.getLogger(DbRemotePublicationTaskTypeImpl.class);

    @Autowired
    GeoServerDataDirectory geoServerDataDirectory;

    @Autowired
    Catalog catalog;

    @Autowired
    ExtTypes extTypes;

    @PostConstruct
    public void initParamInfo() {
        paramInfo.put(PARAM_EXT_GS, new ParameterInfo(PARAM_EXT_GS, extTypes.extGeoserver, true));
        paramInfo.put(PARAM_LAYER, new ParameterInfo(PARAM_LAYER, extTypes.internalLayer, true));
    }

    @Override
    public Map<String, ParameterInfo> getParameterInfo() {
        return paramInfo;
    }

    @Override
    public TaskResult run(Batch batch, Task task, Map<String, Object> parameterValues,
            Map<Object, Object> tempValues) throws TaskException {
        final ExternalGS extGS = (ExternalGS) parameterValues.get(PARAM_EXT_GS);
        final LayerInfo layer = (LayerInfo) parameterValues.get(PARAM_LAYER);
        final ResourceInfo resource = layer.getResource();
        final StoreInfo store = resource.getStore();
        final StoreType storeType = store instanceof CoverageStoreInfo ? StoreType.COVERAGESTORES
                : StoreType.DATASTORES;
        final String ws = store.getWorkspace().getName();

        final GeoServerRESTManager restManager;
        try {
            restManager = extGS.getRESTManager();
        } catch (MalformedURLException e) {
            throw new TaskException(e);
        }

        if (!restManager.getReader().existGeoserver()) {
            throw new TaskException("Failed to connect to geoserver " + extGS.getUrl());
        }

        final boolean createLayer = !restManager.getReader().existsLayer(ws, resource.getName(),
                true);
        final boolean createResource;
        final boolean createStore;
        final boolean createWorkspace;
        final boolean createStyle;
        if (createLayer) {
            // layer doesn't exist yet, publish
            createWorkspace = !restManager.getReader().existsWorkspace(ws);
            createStyle = !restManager.getReader().existsStyle(layer.getDefaultStyle().getName());
            createStore = !(storeType == StoreType.DATASTORES
                    ? restManager.getReader().existsDatastore(ws, store.getName())
                    : restManager.getReader().existsCoveragestore(ws, store.getName()));
            createResource = !(storeType == StoreType.DATASTORES
                    ? restManager.getReader().existsFeatureType(ws, store.getName(),
                            resource.getName())
                    : restManager.getReader().existsCoverage(ws, store.getName(),
                            resource.getName()));

            try {
                if (createWorkspace) { // workspace doesn't exist yet, publish
                    LOGGER.log(Level.INFO, "Workspace doesn't exist: " + ws + " on "
                            + extGS.getName() + ", creating.");
                    try {
                        if (!restManager.getPublisher().createWorkspace(ws,
                                new URI(catalog.getNamespaceByPrefix(ws).getURI()))) {
                            throw new TaskException("Failed to create workspace " + ws);
                        }
                    } catch (URISyntaxException e) {
                        throw new TaskException("Failed to create workspace " + ws, e);
                    }
                }

                if (createStore) {
                    try {
                        if (!createStore(extGS, restManager, store, parameterValues)) {
                            throw new TaskException(
                                    "Failed to create store " + ws + ":" + store.getName());
                        }
                    } catch (IOException e) {
                        throw new TaskException(
                                "Failed to create store " + ws + ":" + store.getName(), e);
                    }
                } else {
                    LOGGER.log(Level.INFO, "Store exists: " + store.getName() + " on "
                            + extGS.getName() + ", skipping creation.");
                }

                if (createResource) { // resource doesn't exist yet, publish
                    // config resource
                    final GSResourceEncoder re;
                    if (resource instanceof CoverageInfo) {
                        CoverageInfo coverage = (CoverageInfo) resource;
                        final GSCoverageEncoder coverageEncoder = new GSCoverageEncoder();
                        coverageEncoder.setNativeFormat(coverage.getNativeFormat());
                        for (String format : coverage.getSupportedFormats()) {
                            coverageEncoder.addSupportedFormats(format);
                        }
                        for (String srs : coverage.getRequestSRS()) {
                            coverageEncoder.setRequestSRS(srs); // wrong: should be add
                        }
                        for (String srs : coverage.getResponseSRS()) {
                            coverageEncoder.setResponseSRS(srs); // wrong: should be add
                        }
                        coverageEncoder.setNativeCoverageName(coverage.getNativeCoverageName());
                        coverageEncoder.setNativeFormat(coverage.getNativeFormat());
                        re = coverageEncoder;
                    } else {
                        GSFeatureTypeEncoder fte = new GSFeatureTypeEncoder();
                        fte.setSRS(resource.getSRS());
                        fte.setNativeName(resource.getNativeName());
                        re = fte;
                    }
                    postProcess(re, parameterValues);
                    re.setName(resource.getName());
                    re.setTitle(resource.getName());
                    re.setSRS(resource.getSRS());
                    re.setProjectionPolicy(resource.getProjectionPolicy() == null
                            ? ProjectionPolicy.NONE
                            : ProjectionPolicy.valueOf(resource.getProjectionPolicy().toString()));
                    re.setLatLonBoundingBox(resource.getLatLonBoundingBox().getMinX(),
                            resource.getLatLonBoundingBox().getMinY(),
                            resource.getLatLonBoundingBox().getMaxX(),
                            resource.getLatLonBoundingBox().getMaxY(), resource.getSRS());
                    if (resource.getNativeBoundingBox() != null) {
                        re.setNativeBoundingBox(resource.getNativeBoundingBox().getMinX(),
                                resource.getNativeBoundingBox().getMinY(),
                                resource.getNativeBoundingBox().getMaxX(),
                                resource.getNativeBoundingBox().getMaxY(), resource.getSRS());
                    }

                    if (!restManager.getPublisher().createResource(ws, storeType, store.getName(),
                            re)) {
                        throw new TaskException(
                                "Failed to create resource " + ws + ":" + resource.getName());
                    }
                } else {
                    LOGGER.log(Level.INFO, "Resource exists: " + layer.getName() + " on "
                            + extGS.getName() + ", skipping creation.");
                }

                if (createStyle) { // style doesn't exist yet, publish
                    LOGGER.log(Level.INFO,
                            "Style doesn't exist: " + layer.getDefaultStyle().getName() + " on "
                                    + extGS.getName() + ", creating.");
                    if (!restManager.getPublisher().publishStyle(
                            geoServerDataDirectory.style(layer.getDefaultStyle()).file(),
                            layer.getDefaultStyle().getName())) {
                        throw new TaskException("Failed to create style " + ws);
                    }
                }

                // config layer
                final GSLayerEncoder layerEncoder = new GSLayerEncoder();
                layerEncoder.setAdvertised(false);
                layerEncoder.setDefaultStyle(
                        layer.getDefaultStyle().getWorkspace() == null ? null
                                : layer.getDefaultStyle().getWorkspace().getName(),
                        layer.getDefaultStyle().getName());

                if (!restManager.getPublisher().configureLayer(ws, resource.getName(),
                        layerEncoder)) {
                    throw new TaskException(
                            "Failed to publish layer " + ws + ":" + resource.getName());
                }
            } catch (TaskException e) {
                // clean-up if necessary
                restManager.getPublisher().removeLayer(ws, layer.getName());
                if (createStore) {
                    restManager.getPublisher().removeStore(ws, store.getName(), storeType, true,
                            Purge.ALL);
                }
                if (createStyle) {
                    restManager.getPublisher().removeStyle(layer.getDefaultStyle().getName(), true);
                }
                if (createWorkspace) {
                    restManager.getPublisher().removeWorkspace(ws, false);
                }
                throw e;
            }
        } else {
            createWorkspace = false;
            createStore = false;
            createResource = false;
            createStyle = false;
            LOGGER.log(Level.INFO, "Layer exists: " + layer.getName() + " on " + extGS.getName()
                    + ", skipping publication.");
        }

        return new TaskResult() {

            @Override
            public void commit() throws TaskException {
                // advertise the layer
                GSLayerEncoder layerEncoder = new GSLayerEncoder();
                layerEncoder.setAdvertised(true);
                if (!restManager.getPublisher().configureLayer(ws, layer.getName(), layerEncoder)) {
                    throw new TaskException(
                            "Failed to advertise layer " + ws + ":" + resource.getName());
                }
            }

            @Override
            public void rollback() throws TaskException {
                if (createLayer) {
                    if (createResource) {
                        if (!(storeType == StoreType.COVERAGESTORES
                                ? restManager.getPublisher().unpublishCoverage(ws, store.getName(),
                                        resource.getName())
                                : restManager.getPublisher().unpublishFeatureType(ws,
                                        store.getName(), resource.getName()))) {
                            throw new TaskException("Failed to remove layer/resource " + ws + ":"
                                    + resource.getName());
                        }
                    } else {
                        if (!restManager.getPublisher().removeLayer(ws, layer.getName())) {
                            throw new TaskException(
                                    "Failed to remove layer " + ws + ":" + resource.getName());
                        }
                    }
                    if (createStore) {
                        if (!restManager.getPublisher().removeStore(ws, store.getName(), storeType,
                                true, Purge.ALL)) {
                            throw new TaskException(
                                    "Failed to remove store " + ws + ":" + store.getName());
                        }
                    }
                    if (createStyle) {
                        if (!restManager.getPublisher()
                                .removeStyle(layer.getDefaultStyle().getName(), true)) {
                            throw new TaskException(
                                    "Failed to remove style " + layer.getDefaultStyle().getName());
                        }
                    }
                    if (createWorkspace) {
                        if (!restManager.getPublisher().removeWorkspace(ws, true)) {
                            throw new TaskException("Failed to remove workspace " + ws);
                        }
                    }
                }

            }

        };
    }

    @Override
    public void cleanup(Task task, Map<String, Object> parameterValues) throws TaskException {
        final ExternalGS extGS = (ExternalGS) parameterValues.get(PARAM_EXT_GS);
        final LayerInfo layer = (LayerInfo) parameterValues.get(PARAM_LAYER);
        final ResourceInfo resource = layer.getResource();
        final StoreInfo store = resource.getStore();
        final StoreType storeType = store instanceof CoverageStoreInfo ? StoreType.COVERAGESTORES
                : StoreType.DATASTORES;
        final String ws = store.getWorkspace().getName();
        final GeoServerRESTManager restManager;
        try {
            restManager = extGS.getRESTManager();
        } catch (MalformedURLException e) {
            throw new TaskException(e);
        }
        if (restManager.getReader().existsLayer(ws, layer.getName(), true)) {
            if (!(storeType == StoreType.COVERAGESTORES
                    ? restManager.getPublisher().unpublishCoverage(ws, store.getName(),
                            resource.getName())
                    : restManager.getPublisher().unpublishFeatureType(ws, store.getName(),
                            resource.getName()))) {
                throw new TaskException(
                        "Failed to remove layer/resource " + ws + ":" + resource.getName());
            }
            if (!restManager.getPublisher().removeStore(ws, store.getName(), storeType, false,
                    Purge.ALL)) {
                if (mustCleanUpStore()) {
                    throw new TaskException("Failed to remove store " + ws + ":" + store.getName());
                } else {
                    LOGGER.log(Level.INFO, "Failed to clean-up datastore " + store.getName()
                            + ", possibly used by other layers.");
                }
            }
            // will not clean-up style and ws
            // because we don't know if they were created by this task.
        }
    }

    protected abstract boolean createStore(ExternalGS extGS, GeoServerRESTManager restManager,
            StoreInfo store, Map<String, Object> parameterValues) throws IOException;

    protected abstract boolean mustCleanUpStore();

    protected void postProcess(GSResourceEncoder re, Map<String, Object> parameterValues) {
    }
}
