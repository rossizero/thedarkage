package de.peacepunkt.tda2plugin.persistence.novote;

import de.peacepunkt.tda2plugin.persistence.EntityDao;
import de.peacepunkt.tda2plugin.persistence.HibernateUtil;
import de.peacepunkt.tda2plugin.persistence.MVP;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExtraDaoImpl implements EntityDao<Extra> {
    private SessionFactory factory;
    public ExtraDaoImpl() {
        factory = HibernateUtil.getSessionFactory();
    }
    public int getExtraLevelOfPlayer(Player player) {
        Session session = factory.openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Extra> criteria = builder.createQuery( Extra.class );
        Root<Extra> root = criteria.from( Extra.class );
        criteria.select(root);
        criteria.where(
                builder.equal(root.get("uuidProfiteur"), player.getUniqueId().toString())
        );
        TypedQuery<Extra> result = session.createQuery(criteria);
        List<Extra>  ret = result.getResultList();
        session.close();
        return ret == null ? 0 : ret.size();
    }
    public boolean isAlreadyRecruited(Player player) {
        Session session = factory.openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Extra> criteria = builder.createQuery( Extra.class );
        Root<Extra> root = criteria.from( Extra.class );
        criteria.select(root);
        criteria.where(
                builder.equal(root.get("uuidTarget"), player.getUniqueId().toString())
        );
        TypedQuery<Extra> result = session.createQuery(criteria);
        List<Extra>  ret = result.getResultList();
        session.close();
        return ret.size() != 0;
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
    public void update(Extra o) {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        session.update(o);
        t.commit();
        session.close();
    }

    @Override
    public void delete(Extra o) {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        session.delete(o);
        t.commit();
        session.close();
    }

    @Override
    public void add(Extra o) {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        session.persist(o);
        t.commit();
        session.close();
    }
    public String getRecruiter(Player player) {
        Session session = factory.openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Extra> criteria = builder.createQuery( Extra.class );
        Root<Extra> root = criteria.from( Extra.class );
        criteria.select(root);
        criteria.where(
                builder.equal(root.get("uuidTarget"), player.getUniqueId().toString())
        );
        TypedQuery<Extra> result = session.createQuery(criteria);
        try {
            Extra ret = result.getSingleResult();
            session.close();
            if (ret != null) {
                return Bukkit.getOfflinePlayer(UUID.fromString(ret.getUuidProfiteur())).getName();
            } else {
                return "no one";
            }
        } catch (NoResultException e) {
            return "no one";
        }
    }
    public List<String> getRecruited(Player player) {
        List<String> ret = new ArrayList<>();
        Session session = factory.openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Extra> criteria = builder.createQuery( Extra.class );
        Root<Extra> root = criteria.from( Extra.class );
        criteria.select(root);
        criteria.where(
                builder.equal(root.get("uuidProfiteur"), player.getUniqueId().toString())
        );
        TypedQuery<Extra> result = session.createQuery(criteria);
        List<Extra> rett = result.getResultList();
        session.close();

        for(Extra extra : rett) {
            ret.add(Bukkit.getOfflinePlayer(UUID.fromString(extra.getUuidTarget())).getName());
        }
        return ret;
    }
    public boolean isOwnRecruiter(Player recruiter, Player target) {
        Session session = factory.openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Extra> criteria = builder.createQuery( Extra.class );
        Root<Extra> root = criteria.from( Extra.class );
        criteria.select(root);
        criteria.where(
                builder.equal(root.get("uuidProfiteur"), recruiter.getUniqueId().toString())
        );
        TypedQuery<Extra> result = session.createQuery(criteria);
        List<Extra>  ret = result.getResultList();
        System.out.println(ret.size());
        session.close();
        for(Extra extra : ret) {
            if(extra.getUuidTarget().equals(target.getUniqueId().toString())) {
                return true;
            }
        }
        return false;
    }
}
