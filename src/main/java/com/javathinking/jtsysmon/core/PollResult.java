package com.javathinking.jtsysmon.core;

import com.javathinking.commons.persistence.PropertyName;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author prule
 */
@Entity
public class PollResult implements Serializable {

    public static enum Status {SUCCESS, FAIL, ERROR, TIMEOUT}

    ;

    public static final PropertyName START = new PropertyName("startMs");
    public static final PropertyName DURATION = new PropertyName("duration");
    public static final PropertyName STATUS = new PropertyName("status");

    @Column
    Long startMs;
    @Column
    Long endMs;
    @Column
    Long duration;
    @Column
    String instanceId;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    MonitorConfig monitorConfig;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column
    @Enumerated(EnumType.STRING)
    Status status;


    public PollResult() {
    }

    public PollResult(Long start, Long end, String instanceId, MonitorConfig monitorConfig) {
        this.startMs = start;
        this.endMs = end;
        this.monitorConfig = monitorConfig;
        this.instanceId = instanceId;
        if (end != null && end > 0) {
            this.duration = end - start;
        }
    }

    public MonitorConfig getMonitorConfig() {
        return monitorConfig;
    }

    public void setMonitorConfig(MonitorConfig monitorConfig) {
        this.monitorConfig = monitorConfig;
    }


    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }


    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public Long getEnd() {
        return endMs;
    }

    public void setEnd(Long end) {
        this.endMs = end;
    }

    public Long getStart() {
        return startMs;
    }

    public void setStart(Long start) {
        this.startMs = start;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


}
