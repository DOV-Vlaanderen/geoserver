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

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    // jdbc:h2:mem:test;INIT=RUNSCRIPT FROM 'classpath:create.sql'\;RUNSCRIPT FROM 'classpath:data.sql'
    @Override
    public DataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        String url = "jdbc:h2:mem:myotherdb;";
        dataSource.setUrl(url);
        return dataSource;
    }

    @Override
    public GSAbstractStoreEncoder getStoreEncoder(String name) {
        GSPostGISDatastoreEncoder encoder = new GSPostGISDatastoreEncoder(name);

        encoder.setDatabase(db);

        return encoder;
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(PostgisNGDataStoreFactory.DBTYPE.key, "postgis");
        params.put(PostgisNGDataStoreFactory.USER.key, "sa");
        params.put(PostgisNGDataStoreFactory.PASSWD.key, "sa");
        params.put(PostgisNGDataStoreFactory.DATABASE.key, db);
        return params;
    }

    @Override
    public String getSchema() {
        return "";
    }

    /*
     * @Override public InputStream dump(String realTableName, String tempTableName) throws IOException { String url = "jdbc:postgresql://" + username +
     * ":" + password + "@" + host + ":" + port + "/" + db; if (ssl) { url += "?sslmode=require"; } Process pr = Runtime.getRuntime().exec(
     * "pg_dump --dbname=" + url + " --table " + (schema == null ? "" : schema + ".") + realTableName);
     * 
     * //to do: remove the search_path from the script //+ replace all names (table, sequences, indexes, constraints) to temporary names
     * 
     * return pr.getInputStream(); }
     * 
     * @Override public OutputStream script() throws IOException { String url = "jdbc:postgresql://" + username + ":" + password + "@" + host + ":" + port
     * + "/" + db; if (ssl) { url += "?sslmode=require"; } if (schema != null) { url += (ssl ? "&" : "?") + "options=--search_path%3D" + schema; } Process
     * pr = Runtime.getRuntime().exec("psql --dbname=" + url); return pr.getOutputStream(); }
     */

    @Override
    public GSAbstractStoreEncoder postProcess(GSAbstractStoreEncoder encoder, DbTable table) {
        if (table != null) {
            String schema = SqlUtil.schema(table.getTableName());
            if (schema != null) {
                ((GSPostGISDatastoreEncoder) encoder).setSchema(schema);
            }
        }
        return encoder;
    }

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        System.out.println("initialize the H2 DB");

        if (createDBSqlResource != null) {
            Connection connection = null;
            connection = getDataSource().getConnection();

            runSql(createDBSqlResource, connection);
        }
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
}
