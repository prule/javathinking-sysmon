package com.javathinking.jtsysmon.core;

import org.springframework.orm.jpa.support.JpaDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author paul
 */
@Repository
@Transactional
public class PollDaoJpaImpl extends JpaDaoSupport implements PollDao {

    public void save(PollResult result) {
        getJpaTemplate().persist(result);
    }

}
