package de.peacepunkt.tda2plugin.persistence;

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
import java.util.List;
import java.util.UUID;

public class MiniArenaBlocksDaoImpl implements EntityDao<MiniArenaBlocks> {
    private SessionFactory factory;
    public MiniArenaBlocksDaoImpl() {
        factory = HibernateUtil.getSessionFactory();
    }

    @Override
    public List<MiniArenaBlocks> getAll() {
        return null;
    }

    @Override
    public void update(MiniArenaBlocks miniArenaBlocks) {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        session.saveOrUpdate(miniArenaBlocks);
        t.commit();
        session.close();
    }

    @Override
    public void delete(MiniArenaBlocks miniArenaBlocks) {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        session.delete(miniArenaBlocks);
        t.commit();
        session.close();
    }

    @Override
    public void add(MiniArenaBlocks miniArenaBlocks) {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        session.persist(miniArenaBlocks);
        t.commit();
        session.close();
        System.out.println("added block!");
    }

    public List<MiniArenaBlocks> get(int x, int z) {
        try {
            Session session = factory.openSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<MiniArenaBlocks> criteria = builder.createQuery(MiniArenaBlocks.class);
            Root<MiniArenaBlocks> root = criteria.from(MiniArenaBlocks.class);
            criteria.select(root);
            criteria.where(
                    builder.and(
                        builder.equal(root.get("x"), x), builder.equal(root.get("z"), z)
                    )
            );
            TypedQuery<MiniArenaBlocks> result = session.createQuery(criteria);
            List<MiniArenaBlocks> ret = result.getResultList();
            session.close();
            return ret;
        } catch (NoResultException e) {
        }
        return null;
    }
    public int getNumberOfBlocksPlacedBy(Player player) {
        try {
            Session session = factory.openSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<MiniArenaBlocks> criteria = builder.createQuery(MiniArenaBlocks.class);
            Root<MiniArenaBlocks> root = criteria.from(MiniArenaBlocks.class);
            criteria.select(root);
            criteria.where(
                    builder.equal(root.get("uuid"), player.getUniqueId().toString())
            );
            TypedQuery<MiniArenaBlocks> result = session.createQuery(criteria);
            List<MiniArenaBlocks> ret = result.getResultList();
            session.close();
            return ret.size();
        } catch (NoResultException e) {
        }
        return 0;
    }
}
