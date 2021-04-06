package de.peacepunkt.tda2plugin.persistence.xp;

import de.peacepunkt.tda2plugin.persistence.EntityDao;
import de.peacepunkt.tda2plugin.persistence.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class XpDaoImpl implements EntityDao<Xp> {

    private SessionFactory factory;
    public XpDaoImpl() {
        factory = HibernateUtil.getSessionFactory();
    }

    @Override
    public List getAll() {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        List<Xp> ret = HibernateUtil.loadAllData(Xp.class, session);
        session.close();
        return ret;
    }

    @Override
    public void update(Xp o) {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        session.update(o);
        t.commit();
        session.close();
    }

    @Override
    public void delete(Xp o) {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        session.delete(o);
        t.commit();
        session.close();
    }

    @Override
    public void add(Xp o) {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        session.persist(o);
        t.commit();
        session.close();
    }

    public Xp get(String uuid) {
        try {
            Session session = factory.openSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Xp> criteria = builder.createQuery(Xp.class);
            Root<Xp> root = criteria.from(Xp.class);
            criteria.select(root);
            criteria.where(
                    builder.equal(root.get("uuid"), uuid) //.getUniqueId().toString()
            );
            TypedQuery<Xp> result = session.createQuery(criteria);
            Xp ret = result.getSingleResult();
            session.close();
            return ret;
        } catch (NoResultException exception) {
            Xp xp = new Xp();
            xp.setUuid(uuid);
            xp.setXp(0);
            add(xp);
            return xp;
        }
    }
}
