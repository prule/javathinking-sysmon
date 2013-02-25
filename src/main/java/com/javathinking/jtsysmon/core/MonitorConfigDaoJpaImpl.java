package com.javathinking.jtsysmon.core;

import org.springframework.orm.jpa.support.JpaDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author paul
 */
@Repository
@Transactional
public class MonitorConfigDaoJpaImpl extends JpaDaoSupport implements MonitorConfigDao {

    public void save(MonitorConfig config) {
        getJpaTemplate().persist(config);
    }

    public List<MonitorConfig> list() {
        return getJpaTemplate().find("from MonitorConfig");
    }

    public MonitorConfig get(long id) {
        return getJpaTemplate().find(MonitorConfig.class, id);
    }

    public void delete(MonitorConfig monitorConfig) {
        getJpaTemplate().remove(monitorConfig);
    }
}
