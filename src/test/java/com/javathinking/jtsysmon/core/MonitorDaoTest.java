package com.javathinking.jtsysmon.core;

import com.javathinking.jtsysmon.core.monitor.HttpMonitor;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author prule
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/integrationTestContext.xml"})
public class MonitorDaoTest {

    @Autowired
    MonitorConfigDao monitorDao;

    @Test
    public void testMonitorConfig() {
        HttpMonitor monitor = new HttpMonitor();
        monitor.setUrl("http://localhost/");
        final MonitorConfig monitorConfig = new MonitorConfig();
        monitorConfig.setMonitor(monitor);
        monitorDao.save(monitorConfig);

        MonitorConfig result = monitorDao.get(monitorConfig.getId());
        Assert.assertNotNull(result.getMonitor());
    }
}
