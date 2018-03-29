/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.tasks;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.geoserver.taskmanager.fileservice.FileService;
import org.geoserver.taskmanager.fileservice.impl.S3FileServiceImpl;
import org.geoserver.taskmanager.schedule.ParameterInfo;
import org.geoserver.taskmanager.schedule.ParameterType;
import org.geoserver.taskmanager.schedule.TaskContext;
import org.geoserver.taskmanager.schedule.TaskException;
import org.geoserver.taskmanager.schedule.TaskResult;
import org.geoserver.taskmanager.schedule.TaskType;
import org.geoserver.taskmanager.util.LookupService;
import org.springframework.beans.factory.annotation.Autowired;

public class CopyS3FileTaskTypeImpl implements TaskType {

    public static final String NAME = "CopyTable";
    
    public static final String PARAM_SOURCE = "source";

    public static final String PARAM_TARGET = "target";

    @Autowired
    LookupService<FileService> fileServiceRegistry;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Map<String, ParameterInfo> getParameterInfo() {
        Map<String, ParameterInfo> paramInfo = new HashMap<String, ParameterInfo>();
        paramInfo.put(PARAM_SOURCE, new ParameterInfo(PARAM_SOURCE, ParameterType.URI, true));
        paramInfo.put(PARAM_TARGET, new ParameterInfo(PARAM_TARGET, ParameterType.URI, true));
        return paramInfo;
    }

    @Override
    public TaskResult run(TaskContext ctx) throws TaskException {        
        final URI sourceURI = (URI) ctx.getParameterValues().get(PARAM_SOURCE);
        final URI targetURI = (URI) ctx.getParameterValues().get(PARAM_TARGET);
        
        S3FileServiceImpl sourceService = fileServiceRegistry.get("s3-" + sourceURI.getScheme(), S3FileServiceImpl.class);
        if (sourceService == null) {
            throw new TaskException("S3 Service for alias " + sourceURI.getScheme()  + "not found." );
        }
        S3FileServiceImpl targetService = fileServiceRegistry.get("s3-" + targetURI.getScheme(), S3FileServiceImpl.class);
        if (targetService == null) {
            throw new TaskException("S3 Service for alias " + targetURI.getScheme()  + "not found." );
        }
        
        int lastSlashPosition = targetURI.getSchemeSpecificPart().lastIndexOf('/');
        String targetBucket = "";
        if (lastSlashPosition >= 0) {
            targetBucket = targetURI.getSchemeSpecificPart().substring(0, lastSlashPosition);
        }
        
        final URI tempURI;
        try {
            tempURI = new URI(targetURI.getScheme() + ":" + targetBucket + "/" + 
                    "_temp_" + UUID.randomUUID().toString().replace('-', '_'));
        } catch (URISyntaxException e) {
            throw new TaskException(e);
        } 
        ctx.getBatchContext().put(targetURI, tempURI);
        
        try {
            targetService.create(tempURI.getSchemeSpecificPart(), 
                    sourceService.read(sourceURI.getSchemeSpecificPart()));
        } catch (IOException e) {
            throw new TaskException(e);
        }
        
        return new TaskResult() {

            @Override
            public void commit() throws TaskException {
                targetService.rename(tempURI.getSchemeSpecificPart(), targetURI.getSchemeSpecificPart());
            }

            @Override
            public void rollback() throws TaskException {
                try {
                    targetService.delete(tempURI.getSchemeSpecificPart());
                } catch (IOException e) {
                    throw new TaskException(e);
                }
            }
            
        };
    }

    @Override
    public void cleanup(TaskContext ctx) throws TaskException {
        URI targetUri = (URI) ctx.getParameterValues().get(PARAM_TARGET);
        
        S3FileServiceImpl targetService = fileServiceRegistry.get("s3-" + targetUri.getScheme(), S3FileServiceImpl.class);
        if (targetService == null) {
            throw new TaskException("S3 Service for alias " + targetUri.getScheme()  + "not found." );
        }
        
        try {
            targetService.delete(targetUri.getSchemeSpecificPart());
        } catch (IOException e) {
            throw new TaskException(e);
        }
    }

}
