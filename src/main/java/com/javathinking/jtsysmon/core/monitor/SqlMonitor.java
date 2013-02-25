package com.javathinking.jtsysmon.core.monitor;

import org.apache.log4j.Logger;

import javax.persistence.Transient;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * @author prule
 */
public class SqlMonitor implements Monitor {
    @Transient
    private static Logger log = Logger.getLogger(SqlMonitor.class);
    private String connectionString;
    private String driverClass;
    private String sql;

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public MonitorResult poll() {
        MonitorResult result = new MonitorResult();

        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(getDriverClass());
            conn = DriverManager.getConnection(getConnectionString());
            stmt = conn.createStatement();
            stmt.execute(getSql());
        } catch (Exception ex) {
            result.addError(ex.getMessage());
        } finally {
            if (stmt != null) try {
                stmt.close();
            } catch (SQLException ex) {
                log.error(ex);
            }
            if (conn != null) try {
                conn.close();
            } catch (SQLException ex) {
                log.error(ex);
            }
        }
        return result;
    }
}
