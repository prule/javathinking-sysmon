package com.javathinking.jtsysmon.core;

import java.util.List;

/**
 * @author prule
 */
public interface MonitorConfigDao {

    List<MonitorConfig> list();

    void save(MonitorConfig config);

    MonitorConfig get(long id);

    void delete(MonitorConfig monitorConfig);
}
