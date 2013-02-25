package com.javathinking.jtsysmon.core;

import com.javathinking.jtsysmon.core.monitor.Monitor;
import com.thoughtworks.xstream.XStream;

import javax.persistence.*;

/**
 * @author prule
 */
@Entity
public class MonitorConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @Column
    String name;
    @Column(length = 20000)
    @Lob
    String serializedMonitor;

    @Transient
    Monitor monitor;
    @Transient
    XStream xstream = new XStream();

    public MonitorConfig() {
    }

    public MonitorConfig(Long id, String name, Monitor monitor) {
        this.id = id;
        this.name = name;
        this.monitor = monitor;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerializedMonitor() {
        return serializedMonitor;
    }

    public void setSerializedMonitor(String serializedMonitor) {
        this.serializedMonitor = serializedMonitor;
    }

    public Monitor getMonitor() {
        return monitor;
    }

    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    @PostLoad
    void postLoad() {
        if (getSerializedMonitor() != null) monitor = (Monitor) xstream.fromXML(getSerializedMonitor());
    }

    @PrePersist
    void prepersist() {
        if (getMonitor() != null) setSerializedMonitor(xstream.toXML(getMonitor()));
    }


}
