/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.external;

import it.geosolutions.geoserver.rest.encoder.GSAbstractStoreEncoder;
import it.geosolutions.geoserver.rest.encoder.datastore.GSPostGISDatastoreEncoder;
import org.geoserver.taskmanager.util.NamedImpl;
import org.geoserver.taskmanager.util.SqlUtil;
import org.geotools.data.postgis.PostgisNGDataStoreFactory;
import org.h2.tools.RunScript;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * DbSource for Postgres.
 *
 * @author Timothy De Bock
 */
public class H2DbSourceImpl extends NamedImpl implements DbSource {

    private String path;

    private String db;

    private Resource createDBSqlResource;

    private Resource createDataSqlResource;

    private Dialect dialect = new H2DialectImpl();

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public Resource getCreateDBSqlResource() {
        return createDBSqlResource;
    }

    public void setCreateDBSqlResource(Resource createDBSqlResource) {
        this.createDBSqlResource = createDBSqlResource;
    }

    public Resource getCreateDataSqlResource() {
        return createDataSqlResource;
    }

    public void setCreateDataSqlResource(Resource createDataSqlResource) {
        this.createDataSqlResource = createDataSqlResource;
    }

    @Override
    public DataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        String url = "jdbc:h2:" + path + ":" + db + ";DB_CLOSE_DELAY=-1";
        dataSource.setUrl(url);
        dataSource.setUsername("sa");
        dataSource.setPassword("sa");
        return dataSource;
    }

    @Override
    public GSAbstractStoreEncoder getStoreEncoder(String name) {
        throw new UnsupportedOperationException("Generic datasource cannot be used as a store.");
    }

    @Override
    public Map<String, Object> getParameters() {
        throw new UnsupportedOperationException("Generic datasource cannot be used as a store.");
    }

    @Override
    public GSAbstractStoreEncoder postProcess(GSAbstractStoreEncoder encoder, DbTable table) {
        throw new UnsupportedOperationException("Generic datasource cannot be used as a store.");
    }

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        if (createDBSqlResource != null) {
            Connection connection = null;
            connection = getDataSource().getConnection();

            runSql(createDBSqlResource, connection);
        }
    }

    @Override
    public String getSchema() {
        return "";
    }

    // utility method to read a .sql txt input stream
    private void runSql(Resource resource, Connection connection) throws IOException, SQLException {
        InputStream is = null;
        try {
            is = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            RunScript.execute(connection, reader);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    @Override
    public Dialect getDialect() {
        return dialect;
    }
}
