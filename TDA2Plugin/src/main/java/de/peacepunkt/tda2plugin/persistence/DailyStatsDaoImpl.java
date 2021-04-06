package de.peacepunkt.tda2plugin.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class DailyStatsDaoImpl implements EntityDao<DailyStats> {

    private SessionFactory factory;
    public DailyStatsDaoImpl() {
        factory = HibernateUtil.getSessionFactory();
    }

    @Override
    public List<DailyStats> getAll() {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        List<DailyStats> ret = HibernateUtil.loadAllData(DailyStats.class, session);
        session.close();
        return ret;
    }

    @Override
    public void update(DailyStats dailyStats) {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        session.saveOrUpdate(dailyStats);
        t.commit();
        session.close();
    }

    @Override
    public void delete(DailyStats dailyStats) {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        session.delete(dailyStats);
        t.commit();
        session.close();
    }

    @Override
    public void add(DailyStats dailyStats) {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        session.persist(dailyStats);
        t.commit();
        session.close();
    }

    public DailyStats get(LocalDate timestamp) {
        try {
            Session session = factory.openSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<DailyStats> criteria = builder.createQuery(DailyStats.class);
            Root<DailyStats> root = criteria.from(DailyStats.class);
            criteria.select(root);
            criteria.where(
                    builder.equal(root.get("timestamp"), timestamp) //.getUniqueId().toString()
            );
            TypedQuery<DailyStats> result = session.createQuery(criteria);
            DailyStats ret = result.getSingleResult();
            session.close();
            return ret;
        } catch (NoResultException e) {
        }
        return null;
    }
}
