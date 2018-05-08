package core;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.CollectionUtil;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by liq on 2018/5/7.
 */
public final class DatabaseCore {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseCore.class);
    private static final QueryRunner QUERY_RUNNER;
    //将connection放入ThreadLocal中，确保一个线程只有一个connection。
    private static final ThreadLocal<Connection> CONNECTION_HOLDER;

    private static ComboPooledDataSource DATA_SOURCE = null;

    static{
        //创建c3p0连接池
        DATA_SOURCE = new ComboPooledDataSource();
        CONNECTION_HOLDER = new ThreadLocal<Connection>();
        QUERY_RUNNER = new QueryRunner();
        try {
            DATA_SOURCE.setDriverClass(ConfigCore.getJdbcDriver());
            DATA_SOURCE.setJdbcUrl(ConfigCore.getJdbcUrl());
            DATA_SOURCE.setUser(ConfigCore.getJdbcUsername());
            DATA_SOURCE.setPassword(ConfigCore.getJdbcPassword());
        } catch (PropertyVetoException e) {
            LOGGER.error("datasource set failure", e);
        }
    }

    /**
     * 获取数据源
     */
    public static DataSource getDataSource(){
        return DATA_SOURCE;
    }

    /**
     * 获取数据库连接
     */
    public static Connection getConnection(){
        Connection conn = CONNECTION_HOLDER.get();
        if (conn == null){
            try {
                conn = DATA_SOURCE.getConnection();
            } catch (SQLException e) {
                LOGGER.error("get connection failure", e);
                throw new RuntimeException(e);
            }finally{
                CONNECTION_HOLDER.set(conn);
            }
        }
        return conn;
    }


    /**
     * 查询实体
     */
    public static <T> T getEntity(Class<T> entityClass, String sql) {
        T entity;
        try {
            Connection conn = getConnection();
            entity = QUERY_RUNNER.query(conn, sql, new BeanHandler<T>(entityClass));
        } catch (SQLException e) {
            LOGGER.error("query entity failure", e);
            throw new RuntimeException(e);
        }
        return entity;
    }


    /**
     * 查询实体列表
     */
    public static <T> List<T> getListEntity(Class<T> entityClass, String sql){
        List<T> resultList;
        try {
            Connection conn = getConnection();
            //DbUtils首先执行SQL返回ResultSet 随后通过反射创建初始化实体对象
            resultList = QUERY_RUNNER.query(conn, sql, new BeanListHandler<T>(entityClass));
        } catch (SQLException e) {
            LOGGER.error("query entity list failure", e);
            throw new RuntimeException(e);
        }
        return  resultList;
    }


    /**
     * 执行查询语句
     */
    public static List<Map<String, Object>> executeQuery(String sql, Object...params){
        List<Map<String, Object>> resultList;
        try {
            Connection conn = getConnection();
            resultList = QUERY_RUNNER.query(conn, sql, new MapListHandler(), params);
        } catch (SQLException e) {
            LOGGER.error("execute query failure", e);
            throw new RuntimeException(e);
        }
        return resultList;
    }

    /**
     * 执行更新语句（包括插入，更新，删除语句）
     */
    public static int executeUpdate(String sql, Object...params) {
        int rows = 0;
        try {
            Connection conn = getConnection();
            rows = QUERY_RUNNER.update(conn, sql, params);
        } catch (SQLException e) {
            LOGGER.error("execute update failure", e);
            throw new RuntimeException(e);
        }
        return rows;
    }

    /**
     * 插入实体
     */
    public static <T> boolean insertEntity(Class<T> entityClass, Map<String, Object> fieldMap) {
        if (CollectionUtil.isEmpty(fieldMap)) {
            LOGGER.error("can not insert entity : fieldMap is null");
            return false;
        }

        String sql = "INSERT INTO " + getTableName(entityClass);
        StringBuilder columns = new StringBuilder("(");
        StringBuilder values = new StringBuilder("(");
        for (String fieldName : fieldMap.keySet()) {
            columns.append(fieldName + ", ");
            values.append("?, ");
        }
        columns.replace(columns.lastIndexOf(","), columns.length(), ")");
        values.replace(values.lastIndexOf(","), values.length(), ")");
        sql += columns + " VALUES " + values;

        Object[] params = fieldMap.values().toArray();
        return executeUpdate(sql, params) == 1;
    }

    /**
     * 更新实体
     */
    public static <T> boolean updateEntity(Class<T> entityClass,long id, Map<String, Object> fieldMap) {
        if (CollectionUtil.isEmpty(fieldMap)) {
            LOGGER.error("can not updateEntity ：fieldMap is empty");
            return false;
        }
        String sql = "UPDATE " + getTableName(entityClass) + " SET ";
        StringBuilder columns = new StringBuilder();
        for (String fieldName : fieldMap.keySet()) {
            columns.append(fieldName).append("=?, ");
        }
        sql += columns.substring(0, columns.lastIndexOf(", ")) + " WHERE id=?";

        ArrayList<Object> paramList = new ArrayList<Object>();
        paramList.addAll(fieldMap.values());
        paramList.add(id);
        Object[] params = paramList.toArray();

        return executeUpdate(sql, params) == 1;
    }

    /**
     * 删除实体
     */
    public static <T> boolean deleteEntity(Class<T> entityClass, long id){
        String sql = "DELETE FROM "+getTableName(entityClass)+" WHERE id=?";
        return executeUpdate(sql, id) == 1;
    }

    private static String getTableName(Class<?> entityClass) {
        return entityClass.getSimpleName();
    }


    /**
     * 开启事务
     */
    public static void beginTransaction(){
        Connection conn = getConnection();
        if (conn != null){
            try {
                conn.setAutoCommit(false);
            } catch (SQLException e) {
                LOGGER.error("begin transaction failure", e);
                throw new RuntimeException(e);
            }finally {
                CONNECTION_HOLDER.set(conn);
            }
        }
    }

    /**
     * 提交事务
     */
    public static void commitTransaction(){
        Connection conn = getConnection();
        try {
            conn.commit();
            conn.close();
        } catch (SQLException e) {
            LOGGER.error("commit transaction failure", e);
            throw new RuntimeException(e);
        }finally {
            CONNECTION_HOLDER.remove();
        }
    }

    /**
     * 回滚事务
     */
    public static void rollbackTransaction(){
        Connection conn = getConnection();
        try {
            conn.rollback();
            conn.close();
        } catch (SQLException e) {
            LOGGER.error("rollback transaction failure", e);
            throw new RuntimeException(e);
        }finally {
            CONNECTION_HOLDER.remove();
        }
    }
}
