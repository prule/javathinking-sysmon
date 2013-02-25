package com.javathinking.jtsysmon.core;

import com.javathinking.jtsysmon.core.monitor.Monitor;
import com.javathinking.jtsysmon.core.monitor.MonitorListener;
import com.javathinking.jtsysmon.core.monitor.MonitorResult;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author prule
 */
public class Poller implements MonitorListener {
    Logger log = Logger.getLogger(Poller.class);
    private long timeout;
    private static final int WAITFOR = 2000;
    private long start;
    private long finished;
    private String instanceId;
    PollResult pollResult;
    MonitorResult result;

    private List<ResultListener> listeners = new ArrayList();

    public Poller(long timeout, String instanceId) {
        this.timeout = timeout;
        this.instanceId = instanceId;
    }

    public void poll(final MonitorConfig monitor) {
        log.debug("Polling " + monitor.getName());
        start = 0;
        finished = 0;
        result = null;
        pollResult = null;

        PollThread pollthread = new PollThread(monitor.getMonitor());
        pollthread.addListener(this);
        Thread thread = new Thread(pollthread);
        start = System.currentTimeMillis();
        thread.start();
        while (result == null) {
            try {
                Thread.sleep(WAITFOR);
            } catch (InterruptedException ex) {
            }
            long now = System.currentTimeMillis();
            if (result == null && now - start > timeout) {
                thread.interrupt();
                result = new MonitorResult();
                result.setTimeout("Timeout @ " + (now - start) + "ms");
            }
        }
        pollResult = new PollResult(start, finished, instanceId, monitor);
        pollResult.setStatus(PollResult.Status.SUCCESS);
        if (result.hasTimedOut()) pollResult.setStatus(PollResult.Status.TIMEOUT);
        if (result.hasFailures()) pollResult.setStatus(PollResult.Status.FAIL);
        if (result.hasErrors()) pollResult.setStatus(PollResult.Status.ERROR);
        for (ResultListener resultListener : listeners) {
            resultListener.handleResult(pollResult);
        }
    }

    public void finished(MonitorResult result) {
        this.result = result;
        this.finished = System.currentTimeMillis();
    }

    public void addListener(ResultListener listener) {
        listeners.add(listener);
    }

}

class PollThread implements Runnable {
    Logger log = Logger.getLogger(PollThread.class);
    Monitor monitor;
    MonitorResult result;
    List<MonitorListener> listeners = new ArrayList();

    public PollThread(Monitor monitor) {
        this.monitor = monitor;
    }


    public void run() {
        log.debug("running " + monitor);
        result = monitor.poll();
        log.debug("finished " + monitor);
        for (MonitorListener listener : listeners) {
            listener.finished(result);
        }
    }

    public void addListener(MonitorListener listener) {
        listeners.add(listener);
    }
}
