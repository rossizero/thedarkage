package de.peacepunkt.tda2plugin.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class GenericDaoImpl<T> implements EntityDao<T> {
    private SessionFactory factory;
    private Class<T> clazz;

    public GenericDaoImpl(Class<T> clazz) {
        factory = HibernateUtil.getSessionFactory();
        this.clazz = clazz;
    }

    public T get(Object o, String keyName, String value) {
        try {
            Session session = factory.openSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<T> criteria = builder.createQuery(clazz);
            Root<T> root = criteria.from(clazz);
            criteria.select(root);
            criteria.where(
                    builder.equal(root.get(keyName), value) //.getUniqueId().toString()
            );
            TypedQuery<T> result = session.createQuery(criteria);
            T ret = result.getSingleResult();
            session.close();
            return ret;
        } catch (NoResultException e) {
        }
        return null;
    }

    @Override
    public void update(T t) {
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();
        session.saveOrUpdate(t);
        transaction.commit();
        session.close();
    }

    @Override
    public void delete(T t) {
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();
        session.delete(t);
        transaction.commit();
        session.close();
    }

    @Override
    public void add(T t) {
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();
        session.persist(t);
        transaction.commit();
        session.close();
    }

    @Override
    public List<T> getAll() {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        List<T> ret = HibernateUtil.loadAllData(clazz, session);
        session.close();
        return ret;
    }
}
