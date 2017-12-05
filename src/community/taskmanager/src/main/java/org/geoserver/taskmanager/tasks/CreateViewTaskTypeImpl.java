/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.tasks;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.geoserver.taskmanager.data.Batch;
import org.geoserver.taskmanager.data.Task;
import org.geoserver.taskmanager.external.DbSource;
import org.geoserver.taskmanager.external.DbTable;
import org.geoserver.taskmanager.external.ExtTypes;
import org.geoserver.taskmanager.schedule.ParameterInfo;
import org.geoserver.taskmanager.schedule.ParameterType;
import org.geoserver.taskmanager.schedule.TaskException;
import org.geoserver.taskmanager.schedule.TaskResult;
import org.geoserver.taskmanager.schedule.TaskType;
import org.geoserver.taskmanager.util.SqlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateViewTaskTypeImpl implements TaskType {

    public static final String NAME = "CreateView";

    public static final String PARAM_DB_NAME = "database";

    public static final String PARAM_TABLE_NAME = "table-name";

    public static final String PARAM_VIEW_NAME = "view-name";

    public static final String PARAM_SELECT = "select-clause";

    public static final String PARAM_WHERE = "where-clause";

    public ParameterType SQL = new ParameterType() {

        @Override
        public List<String> getDomain(List<String> dependsOnRawValues) {
            return null;
        }

        @Override
        public String parse(String value, List<String> dependsOnRawValues) {
            // protection against sneaking in extra statement
            if (value.contains(";")) {
                return null;
            }
            return value;
        }

    };

    private final Map<String, ParameterInfo> paramInfo = new LinkedHashMap<String, ParameterInfo>();

    @Autowired
    ExtTypes extTypes;

    @PostConstruct
    public void initParamInfo() {
        paramInfo.put(PARAM_DB_NAME, new ParameterInfo(PARAM_DB_NAME, extTypes.dbName, true));
        paramInfo.put(PARAM_TABLE_NAME,
                new ParameterInfo(PARAM_TABLE_NAME, extTypes.tableName(), true)
                        .dependsOn(paramInfo.get(PARAM_DB_NAME)));
        paramInfo.put(PARAM_VIEW_NAME,
                new ParameterInfo(PARAM_VIEW_NAME, ParameterType.STRING, true));
        paramInfo.put(PARAM_SELECT, new ParameterInfo(PARAM_SELECT, SQL, true));
        paramInfo.put(PARAM_WHERE, new ParameterInfo(PARAM_WHERE, SQL, false));
    }

    @Override
    public Map<String, ParameterInfo> getParameterInfo() {
        return paramInfo;
    }

    @Override
    public TaskResult run(Batch batch, Task task, Map<String, Object> parameterValues,
            Map<Object, Object> tempValues) throws TaskException {
        final DbSource db = (DbSource) parameterValues.get(PARAM_DB_NAME);
        final DbTable table = tempValues.containsKey(parameterValues.get(PARAM_TABLE_NAME))
                ? (DbTable) tempValues.get(parameterValues.get(PARAM_TABLE_NAME))
                : (DbTable) parameterValues.get(PARAM_TABLE_NAME);
        final String select = (String) parameterValues.get(PARAM_SELECT);
        final String where = (String) parameterValues.get(PARAM_WHERE);
        final String viewName = (String) parameterValues.get(PARAM_VIEW_NAME);
        final String tempViewName = "_temp_" + UUID.randomUUID().toString().replace('-', '_');
        try (Connection conn = db.getDataSource().getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                StringBuilder sb = new StringBuilder("CREATE VIEW ").append(tempViewName)
                        .append(" AS SELECT ").append(select).append(" FROM ")
                        .append(table.getTableName());
                if (where != null) {
                    sb.append(" WHERE ").append(where);
                }
                stmt.executeUpdate(sb.toString());
            }
        } catch (SQLException e) {
            throw new TaskException(e);
        }

        return new TaskResult() {
            @Override
            public void commit() throws TaskException {
                try (Connection conn = db.getDataSource().getConnection()) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.executeUpdate("DROP VIEW IF EXISTS " + SqlUtil.quote(viewName));
                        stmt.executeUpdate("ALTER VIEW " + tempViewName + " RENAME TO "
                                + SqlUtil.quote(SqlUtil.notQualified(viewName)));
                    }
                } catch (SQLException e) {
                    throw new TaskException(e);
                }
            }

            @Override
            public void rollback() throws TaskException {
                try (Connection conn = db.getDataSource().getConnection()) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.executeUpdate("DROP VIEW " + tempViewName);
                    }
                } catch (SQLException e) {
                    throw new TaskException(e);
                }
            }

        };
    }

    @Override
    public void cleanup(Task task, Map<String, Object> parameterValues) throws TaskException {
        final DbSource db = (DbSource) parameterValues.get(PARAM_DB_NAME);
        final String viewName = (String) parameterValues.get(PARAM_VIEW_NAME);
        try (Connection conn = db.getDataSource().getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DROP VIEW IF EXISTS " + SqlUtil.quote(viewName));
            }
        } catch (SQLException e) {
            throw new TaskException(e);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

}
