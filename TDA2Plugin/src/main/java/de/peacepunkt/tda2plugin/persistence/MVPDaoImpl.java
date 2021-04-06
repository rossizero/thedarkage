package de.peacepunkt.tda2plugin.persistence;

import org.bukkit.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class MVPDaoImpl implements EntityDao<MVP> {
    private SessionFactory factory;
    public MVPDaoImpl() {
        factory = HibernateUtil.getSessionFactory();
    }
    public List<MVP> getAllMVPofPlayer(Player p) {
        return getAllMVPofPlayer(p.getUniqueId().toString());
    }
    public  List<MVP> getAllMVPofPlayer(String uuid) {
        Session session = factory.openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<MVP> criteria = builder.createQuery( MVP.class );
        Root<MVP> root = criteria.from( MVP.class );
        criteria.select(root);
        criteria.where(
                builder.equal(root.get("uuid"), uuid)
        );
        TypedQuery<MVP> result = session.createQuery(criteria);
        List<MVP>  ret = result.getResultList();
        session.close();
        return ret;
    }
    @Override
    public List getAll() {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        List<MVP> ret = HibernateUtil.loadAllData(MVP.class, session);
        session.close();
        return ret;
    }

    @Override
    public void update(MVP o) {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        session.update(o);
        t.commit();
        session.close();
    }

    @Override
    public void delete(MVP o) {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        session.delete(o);
        t.commit();
        session.close();
    }

    @Override
    public void add(MVP o) {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        session.persist(o);
        t.commit();
        session.close();
    }
}
