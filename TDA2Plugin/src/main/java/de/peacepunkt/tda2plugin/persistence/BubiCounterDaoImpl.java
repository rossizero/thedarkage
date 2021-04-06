package de.peacepunkt.tda2plugin.persistence;

import org.bukkit.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.List;

public class BubiCounterDaoImpl implements EntityDao<BubiCounter> {
    private SessionFactory factory;
    public BubiCounterDaoImpl() {
        factory = HibernateUtil.getSessionFactory();
    }

    @Override
    public List<BubiCounter> getAll() {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        List<BubiCounter> ret = HibernateUtil.loadAllData(BubiCounter.class, session);
        session.close();
        return ret;
    }

    @Override
    public void update(BubiCounter bubiCounter) {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        session.saveOrUpdate(bubiCounter);
        t.commit();
        session.close();
    }

    @Override
    public void delete(BubiCounter bubiCounter) {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        session.delete(bubiCounter);
        t.commit();
        session.close();
    }

    @Override
    public void add(BubiCounter bubiCounter) {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        session.persist(bubiCounter);
        t.commit();
        session.close();
    }

    public void increase(Player player) {
        BubiCounter counter = get(player);
        if(counter == null) {
            counter = new BubiCounter(player.getUniqueId().toString());
            counter.increase();
            add(counter);
        } else {
            counter.increase();
            update(counter);
        }
    }

    public BubiCounter get(Player player) {
        try {
            Session session = factory.openSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<BubiCounter> criteria = builder.createQuery(BubiCounter.class);
            Root<BubiCounter> root = criteria.from(BubiCounter.class);
            criteria.select(root);
            criteria.where(
                    builder.equal(root.get("uuid"), player.getUniqueId().toString()) //.getUniqueId().toString()
            );
            TypedQuery<BubiCounter> result = session.createQuery(criteria);
            BubiCounter ret = result.getSingleResult();
            session.close();
            return ret;
        } catch (NoResultException e) {
        }
        return null;
    }

    public List<BubiCounter> getTopList() {
        Session session = factory.openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<BubiCounter> criteria = builder.createQuery( BubiCounter.class );
        Root<BubiCounter> root = criteria.from( BubiCounter.class );
        criteria.select(root);
        criteria.orderBy(builder.desc(root.get("count")));
        TypedQuery<BubiCounter> result = session.createQuery(criteria);
        result.setFirstResult(0);
        result.setMaxResults(13);
        List<BubiCounter> ret = result.getResultList();
        session.close();
        return ret;
    }
}
