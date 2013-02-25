package com.javathinking.jtsysmon.core;

import com.javathinking.jtsysmon.core.monitor.Monitor;
import com.javathinking.jtsysmon.core.monitor.MonitorResult;
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author prule
 */
public class PollerTest {

    @Test
    public void testTimeout() {
        {
            Poller poller = new Poller(500, "junit");
            poller.poll(new MonitorConfig(-1L, "junit", new WaitMonitor(2000)));
            Assert.assertTrue(poller.result.hasTimedOut());
            Assert.assertEquals(PollResult.Status.TIMEOUT, poller.pollResult.status);
        }
        {
            Poller poller = new Poller(1000, "junit");
            poller.poll(new MonitorConfig(-1L, "junit", new WaitMonitor(10)));
            Assert.assertFalse(poller.result.hasTimedOut());
        }
    }
}

class WaitMonitor implements Monitor {

    long waitfor;

    public WaitMonitor(long waitfor) {
        this.waitfor = waitfor;
    }

    public MonitorResult poll() {
        try {
            Thread.sleep(waitfor);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        return new MonitorResult();
    }
}
