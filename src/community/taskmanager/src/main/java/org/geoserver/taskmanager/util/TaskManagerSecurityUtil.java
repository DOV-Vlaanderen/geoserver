package org.geoserver.taskmanager.util;

import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.security.SecureCatalogImpl;
import org.geoserver.security.WorkspaceAccessLimits;
import org.geoserver.taskmanager.data.Batch;
import org.geoserver.taskmanager.data.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class TaskManagerSecurityUtil {

    @Autowired
    @Qualifier("rawCatalog")
    private Catalog catalog;

    @Autowired
    private SecureCatalogImpl secureCatalog;

    private WorkspaceInfo getWorkspace(String workspaceName) {
        if (workspaceName == null) {
            return catalog.getDefaultWorkspace();
        } else {
            return catalog.getWorkspaceByName(workspaceName);
        }
    }

    public boolean isReadable(Authentication user, Configuration config) {
        WorkspaceInfo wi = getWorkspace(config.getWorkspace());
        if (wi == null) {
            return false;
        } else {
            WorkspaceAccessLimits limits = secureCatalog.getResourceAccessManager()
                    .getAccessLimits(user, wi);
            return limits == null || limits.isReadable();
        }
    }

    public boolean isReadable(Authentication user, Batch batch) {
        WorkspaceInfo wi = getWorkspace(batch.getWorkspace());
        WorkspaceInfo wif = null;
        if (batch.getConfiguration() != null) {
            wif = getWorkspace(batch.getConfiguration().getWorkspace());
        }
        boolean check1 = false, check2 = batch.getConfiguration() == null;
        if (wi != null) {
            WorkspaceAccessLimits limits = secureCatalog.getResourceAccessManager()
                    .getAccessLimits(user, wi);
            check1 = limits == null || limits.isReadable();
        }
        if (wif != null) {
            WorkspaceAccessLimits limits = secureCatalog.getResourceAccessManager()
                    .getAccessLimits(user, wif);
            check2 = limits == null || limits.isReadable();
        }
        return check1 && check2;
    }

    public boolean isWritable(Authentication user, Configuration config) {
        WorkspaceInfo wi = getWorkspace(config.getWorkspace());
        if (wi == null) {
            return false;
        } else {
            WorkspaceAccessLimits limits = secureCatalog.getResourceAccessManager()
                    .getAccessLimits(user, wi);
            return limits == null || limits.isWritable();
        }
    }

    public boolean isWritable(Authentication user, Batch batch) {
        WorkspaceInfo wi = getWorkspace(batch.getWorkspace());
        WorkspaceInfo wif = null;
        if (batch.getConfiguration() != null) {
            wif = getWorkspace(batch.getConfiguration().getWorkspace());
        }
        boolean check1 = false, check2 = batch.getConfiguration() == null;
        if (wi != null) {
            WorkspaceAccessLimits limits = secureCatalog.getResourceAccessManager()
                    .getAccessLimits(user, wi);
            check1 = limits == null || limits.isWritable();
        }
        if (wif != null) {
            WorkspaceAccessLimits limits = secureCatalog.getResourceAccessManager()
                    .getAccessLimits(user, wif);
            check2 = limits == null || limits.isWritable();
        }
        return check1 && check2;
    }

}
