package org.tafia.smartroute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.CaseFormat;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SmartDao {

    private DataSource dataSource;

    private SmartDao(){}

    public static SmartDao of(DataSource dataSource) {
        SmartDao dao = new SmartDao();
        dao.dataSource = dataSource;
        return dao;
    }

    public <T extends SmartEntity> void saveOne(T entity) {
        Map<String, Object> fields;
        if (entity == null || (fields = getFields(entity)).isEmpty()) return;
        List<String> columns = new ArrayList<>(fields.keySet());
        String sql = entity.getId() == null ? constructInsertSql(entity.getClass(), columns) : constructUpdateSql(entity.getClass(), columns);
        String id = entity.getId() == null ? uuid() : entity.getId();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            for (int i = 0; i < columns.size(); i++) {
                statement.setObject(i + 1, fields.get(columns.get(i)));
            }
            statement.setObject(columns.size() + 1, id);
            statement.execute();
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public <T extends SmartEntity> void saveList(List<T> entities) {
        entities.forEach(this::saveOne);
    }

    public <T extends SmartEntity> T findOne(T entity) {
        List<T> list = findList(entity);
        return list.isEmpty() ? null : list.get(0);
    }

    public <T extends SmartEntity> List<T> findList(T entity) {
        Map<String, Object> fields;
        if (entity == null || (fields = getFields(entity)).isEmpty()) return Collections.emptyList();
        List<String> columns = new ArrayList<>(fields.keySet());
        Object[] values = columns.stream().map(fields::get).toArray();
        @SuppressWarnings("unchecked")
        Class<T> entityClass = (Class<T>) entity.getClass();
        return findList(entityClass, constructSelectSql(entityClass, columns), values);
    }

    public <T extends SmartEntity> List<T> findList(Class<T> entityClass, String sql, Object... parameters) {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Map<String, Object>> result = extract(resultSet);
                return ((JSONArray) JSON.toJSON(result)).toJavaList(entityClass);
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private List<Map<String, Object>> extract(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        List<String> columnNames = new ArrayList<>(metaData.getColumnCount());
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            columnNames.add(lowerCamel(metaData.getColumnName(i)));
        }
        List<Map<String, Object>> result = new ArrayList<>();
        while (resultSet.next()) {
            Map<String, Object> map = new LinkedHashMap<>();
            for (int i = 0; i < columnNames.size(); i++) {
                map.put(columnNames.get(i), resultSet.getObject(i + 1));
            }
            result.add(map);
        }
        return result;
    }

    private <T extends SmartEntity> Map<String, Object> getFields(T entity) {
        JSONObject jsonObject = (JSONObject) JSON.toJSON(entity);
        jsonObject.remove("id");
        return jsonObject;
    }

    private String constructInsertSql(Class<?> entityClass, List<String> columns) {
        return "INSERT INTO `" + lowerUnderscore(entityClass.getSimpleName()) + "` ("
                + columns.stream().map(e -> "`" + lowerUnderscore(e) + "`, ").collect(Collectors.joining())
                + "`id`) VALUES (" + IntStream.range(0, columns.size() + 1).mapToObj(e -> "?").collect(Collectors.joining(", "))
                + ");";
    }

    private String constructUpdateSql(Class<?> entityClass, List<String> columns) {
        return "UPDATE `" + lowerUnderscore(entityClass.getSimpleName()) + "` SET "
                + columns.stream().map(e -> "`" + lowerUnderscore(e) + "` = ?").collect(Collectors.joining(", "))
                + " WHERE `id` = ?;";
    }

    private String constructSelectSql(Class<?> entityClass, List<String> columns) {
        return "SELECT * FROM `" + lowerUnderscore(entityClass.getSimpleName()) + "` WHERE "
                + columns.stream().map(e -> "`" + lowerUnderscore(e) + "` = ?").collect(Collectors.joining(" AND "));
    }

    private String uuid() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    private String lowerUnderscore(String s) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, s);
    }

    private String lowerCamel(String s) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, s);
    }

}
