/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.javathinking.jtsysmon.core.monitor;


import org.junit.Assert;
import org.junit.Test;

/**
 * @author prule
 */
public class HttpMonitorTest {
    @Test
    public void testPollNoServer() {
        final HttpMonitor monitor = new HttpMonitor();
        monitor.setUrl("http://localhost/");
        final MonitorResult result = monitor.poll();
        Assert.assertTrue(result.hasErrors());
    }

    @Test
    public void testPollValid() {
        final HttpMonitor monitor = new HttpMonitor();
        monitor.setUrl("http://javathinking.com/");
        final MonitorResult result = monitor.poll();
        Assert.assertFalse(result.hasErrors());
        Assert.assertFalse(result.hasTimedOut());
        Assert.assertFalse(result.hasFailures());
    }

    @Test
    public void testPollValidWithCheck() {
        final HttpMonitor monitor = new HttpMonitor();
        monitor.setUrl("http://javathinking.com/");
        monitor.setCheckFor("JavaThinking");
        final MonitorResult result = monitor.poll();
        Assert.assertFalse(result.hasErrors());
        Assert.assertFalse(result.hasTimedOut());
        Assert.assertFalse(result.hasFailures());
    }

    @Test
    public void testPollBadCheck() {
        final HttpMonitor monitor = new HttpMonitor();
        monitor.setUrl("http://javathinking.com/");
        monitor.setCheckFor("thistextdoesnotexist");
        final MonitorResult result = monitor.poll();
        Assert.assertFalse(result.hasErrors());
        Assert.assertFalse(result.hasTimedOut());
        Assert.assertTrue(result.hasFailures());
    }

}
