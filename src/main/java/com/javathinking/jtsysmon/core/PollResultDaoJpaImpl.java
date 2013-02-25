package com.javathinking.jtsysmon.core;

import com.javathinking.commons.persistence.Comparison;
import com.javathinking.commons.persistence.SimpleJpqQuery;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.support.JpaDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * @author paul
 */
@Repository
@Transactional
public class PollResultDaoJpaImpl extends JpaDaoSupport implements PollResultDao, ResultListener {

    public void save(PollResult result) {
        getJpaTemplate().persist(result);
    }

    public void handleResult(PollResult pollResult) {
        save(pollResult);
    }

    public List<PollResult> list(Date start, Date end, Long minDuration, PollResult.Status... statuses) {
        SimpleJpqQuery query = query(start, end).add(SimpleJpqQuery.Op.and, Comparison.ge, PollResult.DURATION, minDuration);
        if (statuses != null) {
            query.group(SimpleJpqQuery.Op.and, Comparison.eq, PollResult.STATUS, statuses[0]);
            for (PollResult.Status status : statuses) {
                query.add(SimpleJpqQuery.Op.or, Comparison.eq, PollResult.STATUS, status);
            }
            query.ungroup();
        }
//        System.out.println("query="+query.getSelectAll());
        query.order(PollResult.START);
        return (List<PollResult>) getJpaTemplate().findByNamedParams(query.getSelectAll(), query.getParams());

    }

    SimpleJpqQuery query(Date start, Date end) {
        return new SimpleJpqQuery(PollResult.class).add(SimpleJpqQuery.Op.and, Comparison.ge, PollResult.START, start.getTime()).add(SimpleJpqQuery.Op.and, Comparison.lt, PollResult.START, end.getTime());
    }

    public void delete(final MonitorConfig monitorConfig) {
        getJpaTemplate().execute(new JpaCallback() {
            public Integer doInJpa(EntityManager em) throws PersistenceException {
                final Query query = em.createQuery("delete from PollResult where monitorConfig=?");
                query.setParameter(1, monitorConfig);
                return query.executeUpdate();
            }
        });
    }
}
