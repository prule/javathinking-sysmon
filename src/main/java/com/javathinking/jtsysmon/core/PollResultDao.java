package com.javathinking.jtsysmon.core;

import java.util.Date;
import java.util.List;

/**
 * @author prule
 */
public interface PollResultDao {

    void save(PollResult result);

    List<PollResult> list(Date start, Date end, Long minDuration, PollResult.Status... statuses);

    void delete(final MonitorConfig monitorConfig);
}
