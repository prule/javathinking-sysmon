package com.javathinking.jtsysmon.core.monitor;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author prule
 */
public class SqlMonitorTest {
    SqlMonitor monitor = new SqlMonitor();

    @Before
    public void setup() {
        monitor = new SqlMonitor();
        monitor.setDriverClass("org.hibernate.dialect.DerbyDialect");
        monitor.setConnectionString("jdbc:derby://localhost:1527/jtsysmon");
        monitor.setSql("select count(*) from APP.MONITORCONFIG");
    }

    @Test
    public void testPoll() {
        final MonitorResult result = monitor.poll();
        Assert.assertFalse(result.hasTimedOut());
        Assert.assertFalse(result.hasErrors());
        Assert.assertFalse(result.hasFailures());
    }

    @Test
    public void testPollBadSql() {
        monitor.setSql("select count(*) from TABLEDOESNOTEXIST");
        final MonitorResult result = monitor.poll();
        Assert.assertFalse(result.hasTimedOut());
        Assert.assertTrue(result.hasErrors());
        Assert.assertFalse(result.hasFailures());
    }

    @Test
    public void testPollBadConnection() {
        monitor.setConnectionString("jdbc:derby://localhost:1527/DOESNOTEXIST");
        final MonitorResult result = monitor.poll();
        Assert.assertFalse(result.hasTimedOut());
        Assert.assertTrue(result.hasErrors());
        Assert.assertFalse(result.hasFailures());
    }

}
