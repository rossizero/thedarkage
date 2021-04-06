package de.peacepunkt.tda2plugin.kits;

import de.peacepunkt.tda2plugin.persistence.EntityDao;
import de.peacepunkt.tda2plugin.persistence.HibernateUtil;
import org.bukkit.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class AbstractKitSuperclassDaoImpl<T extends AbstractKitSuperclass> implements EntityDao<T> {
    SessionFactory factory;
    Class<T> clazz;

    public AbstractKitSuperclassDaoImpl(Class<T> clazz) {
        this.factory = HibernateUtil.getSessionFactory();
        this.clazz = clazz;
    }

    @Override
    public List<T> getAll() {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        List<T> ret = HibernateUtil.loadAllData(clazz, session);
        session.close();
        return ret;
    }

    @Override
    public void update(AbstractKitSuperclass abstractKitSuperclass) {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        session.update(abstractKitSuperclass);
        t.commit();
        session.close();
    }

    @Override
    public void delete(AbstractKitSuperclass abstractKitSuperclass) {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        session.delete(abstractKitSuperclass);
        t.commit();
        session.close();
    }

    public T get(String uuid) {
        Session session = factory.openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(clazz);
        Root<T> root = criteria.from(clazz);
        criteria.select(root);
        criteria.where(
                builder.equal(root.get("uuid"), uuid) //.getUniqueId().toString()
        );
        TypedQuery<T> result = session.createQuery(criteria);
        T ret = result.getSingleResult();
        session.close();
        return ret;
    }

    @Override
    public void add(AbstractKitSuperclass abstractKitSuperclass) {
        SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        session.persist(abstractKitSuperclass);
        t.commit();
        session.close();
    }

    public boolean hasKit(Player player) {
        try {
            Session session = factory.openSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<T> criteria = builder.createQuery(clazz);
            Root<T> root = criteria.from(clazz);
            criteria.select(root);
            criteria.where(
                    builder.equal(root.get("uuid"), player.getUniqueId().toString()) //.getUniqueId().toString()
            );
            TypedQuery<T> result = session.createQuery(criteria);
            T ret = result.getSingleResult();
            session.close();
            return true;
        } catch (NoResultException exception) {
            try {
                AbstractKitSuperclass c = ((AbstractKitSuperclass)clazz.getConstructors()[0].newInstance());
                //System.out.println("Player did not have kit " + c + " " + c.getKitDescription().getName());
                if (c.isDefault()) {
                    c.setUuid(player.getUniqueId().toString());
                    add(c);
                    return true;
                } else {
                    return false;
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
