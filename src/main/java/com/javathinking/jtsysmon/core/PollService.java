package com.javathinking.jtsysmon.core;

import java.util.Date;
import java.util.List;

/**
 * @author prule
 */
public interface PollService {

    void start(long wait, long timeout);

    List<PollResult> listAlarms(Date start, Date end, Long minDuration);

    List<PollResult> listProblems(Date start, Date end);

}
