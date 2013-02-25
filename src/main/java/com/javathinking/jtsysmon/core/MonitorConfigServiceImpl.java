package com.javathinking.jtsysmon.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author prule
 */
@Service
public class MonitorConfigServiceImpl implements MonitorConfigService {

    @Autowired
    MonitorConfigDao monitorConfigDao;

    @Autowired
    PollResultDao pollResultDao;

    @Transactional
    public boolean delete(Long id) {
        final MonitorConfig mc = monitorConfigDao.get(id);
        if (mc != null) {
            pollResultDao.delete(mc);
            monitorConfigDao.delete(mc);
            return true;
        }
        return false;
    }
}
