/* (c) 2020 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.annotation.PostConstruct;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.geoserver.config.GeoServerDataDirectory;
import org.geoserver.platform.resource.Resource;
import org.geoserver.platform.resource.Resources;
import org.geoserver.taskmanager.external.DbSource;
import org.geoserver.taskmanager.schedule.ParameterInfo;
import org.geoserver.taskmanager.schedule.TaskContext;
import org.geoserver.taskmanager.schedule.TaskException;
import org.geoserver.taskmanager.util.PlaceHolderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppSchemaLocalPublicationTaskTypeImpl extends FileLocalPublicationTaskTypeImpl {

    public static final String NAME = "LocalAppSchemaPublication";

    public static final String PARAM_DB = "database";

    @Autowired private GeoServerDataDirectory dataDirectory;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    @PostConstruct
    public void initParamInfo() {
        super.initParamInfo();
        paramInfo.put(PARAM_DB, new ParameterInfo(PARAM_DB, extTypes.dbName, true));
    }

    @Override
    protected URI process(URI uri, TaskContext ctx) throws TaskException {
        final DbSource db = (DbSource) ctx.getParameterValues().get(PARAM_DB);

        String path;
        try {
            path = uri.toURL().getPath();
        } catch (MalformedURLException e) {
            throw new TaskException(e);
        }

        String newPath;
        try {
            if (path.toUpperCase().endsWith("ZIP")) {
                newPath = processZip(path, db.getParameters());
            } else {
                newPath = processSingle(path, db.getParameters());
            }
        } catch (IOException e) {
            throw new TaskException(e);
        }

        try {
            return new URI("file:" + newPath);
        } catch (URISyntaxException e) {
            throw new TaskException(e);
        }
    }

    private String processZip(String path, Map<String, Serializable> parameters)
            throws IOException, TaskException {
        try (ZipInputStream is = new ZipInputStream(Resources.fromPath(path).in())) {
            for (ZipEntry entry = is.getNextEntry(); entry != null; entry = is.getNextEntry()) {
                String template = IOUtils.toString(is, "UTF-8");
                String pub = PlaceHolderUtil.replacePlaceHolders(template, parameters);

                Resource res =
                        dataDirectory.get(
                                path.substring(0, path.lastIndexOf("/")) + "/" + entry.getName());

                try (OutputStream os = res.out()) {
                    os.write(pub.getBytes());
                }
            }
        }
        String newPath = FilenameUtils.removeExtension(path) + ".xml";

        if (!Resources.exists(dataDirectory.get(newPath))) {
            throw new TaskException("Zip file must include xml file with same name.");
        }

        return newPath;
    }

    private String processSingle(String path, Map<String, Serializable> parameters)
            throws IOException {
        String newPath = FilenameUtils.removeExtension(path) + "_local.xml";
        Resource res = dataDirectory.get(newPath);

        try (InputStream is = Resources.fromPath(path).in()) {
            String template = IOUtils.toString(is, "UTF-8");
            String pub = PlaceHolderUtil.replacePlaceHolders(template, parameters);

            try (OutputStream os = res.out()) {
                os.write(pub.getBytes());
            }
        }

        return newPath;
    }
}
