package com.javathinking.jtsysmon.core;

import com.javathinking.commons.SystemUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author prule
 */
@Service
public class PollServiceImpl implements PollService {
    Logger log = Logger.getLogger(PollServiceImpl.class);
    @Autowired
    MonitorConfigDao monitorDao;
    @Autowired
    PollResultDao pollResultDao;

    public void start(long wait, long timeout) {
        log.info("Starting monitors");

        final List<MonitorConfig> monitors = monitorDao.list();

        final Poller poller = new Poller(timeout, SystemUtil.hostname());
        poller.addListener((ResultListener) pollResultDao);
        while (true) {
            for (MonitorConfig monitorConfig : monitors) {
                poller.poll(monitorConfig);
            }
            try {
                Thread.sleep(wait);
            } catch (InterruptedException ex) {
            }
        }
    }

    public List<PollResult> listAlarms(Date start, Date end, Long minDuration) {
        return pollResultDao.list(start, end, minDuration, PollResult.Status.SUCCESS);
    }

    public List<PollResult> listProblems(Date start, Date end) {
        return pollResultDao.list(start, end, null, PollResult.Status.ERROR, PollResult.Status.FAIL, PollResult.Status.TIMEOUT);
    }

}
