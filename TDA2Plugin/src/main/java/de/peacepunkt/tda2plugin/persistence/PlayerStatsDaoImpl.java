package de.peacepunkt.tda2plugin.persistence;

import org.bukkit.entity.Player;
import org.hibernate.PessimisticLockException;
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
import java.util.ArrayList;
import java.util.List;

public class PlayerStatsDaoImpl implements EntityDao<PlayerStats> {
    private SessionFactory factory;
    public static int entriesPerToplistPage = 10;
    public static int numberOfEntriesAboveRequestedPlayer = 5;

    public PlayerStatsDaoImpl() {
        factory = HibernateUtil.getSessionFactory();
    }
    private PlayerStats getMyStatsUsername(String username) {
        try {
            Session session = factory.openSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<PlayerStats> criteria = builder.createQuery(PlayerStats.class);
            Root<PlayerStats> root = criteria.from(PlayerStats.class);
            criteria.select(root);
            criteria.where(
                    builder.equal(root.get("username"), username) //.getUniqueId().toString()
            );
            TypedQuery<PlayerStats> result = session.createQuery(criteria);
            PlayerStats ret = result.getSingleResult();
            session.close();
            return ret;
        } catch (NoResultException exception) {
            return null;
        }
    }
    public PlayerStats getMyStats(String uuid) {
        try {
            Session session = factory.openSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<PlayerStats> criteria = builder.createQuery(PlayerStats.class);
            Root<PlayerStats> root = criteria.from(PlayerStats.class);
            criteria.select(root);
            criteria.where(
                    builder.equal(root.get("uuid"), uuid) //.getUniqueId().toString()
            );
            TypedQuery<PlayerStats> result = session.createQuery(criteria);
            PlayerStats ret = result.getSingleResult();
            session.close();
            return ret;
        } catch (NoResultException exception) {
            return null;
        }
    }
    public List<PlayerStats> getTopList(int i) {
        Session session = factory.openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<PlayerStats> criteria = builder.createQuery( PlayerStats.class );
        Root<PlayerStats> root = criteria.from( PlayerStats.class );
        criteria.select(root);
        criteria.orderBy(builder.desc(root.get("score")));
        TypedQuery<PlayerStats> result = session.createQuery(criteria);
        result.setFirstResult(i*entriesPerToplistPage);
        result.setMaxResults(entriesPerToplistPage);
        List<PlayerStats> ret = result.getResultList();
        session.close();
        return ret;
    }
    public int getNumberOfPlayers() {
        Session session = factory.openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<PlayerStats> criteria = builder.createQuery( PlayerStats.class );
        Root<PlayerStats> root = criteria.from( PlayerStats.class );
        criteria.select(root);
        TypedQuery<PlayerStats> result = session.createQuery(criteria);
        List<PlayerStats> ret = result.getResultList();
        session.close();
        return ret.size();
    }
    public long getRankOfPlayer(Player p) {
        try {
            Session session = factory.openSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<PlayerStats> criteria = builder.createQuery(PlayerStats.class);
            Root<PlayerStats> root = criteria.from(PlayerStats.class);
            criteria.select(root);
            criteria.orderBy(builder.desc(root.get("score")));
            //TODO completly inefficient fetches all
            TypedQuery<PlayerStats> result = session.createQuery(criteria);
            List<PlayerStats> ret = result.getResultList();
            session.close();
            for(PlayerStats ps : ret) {
                if(ps.getUsername().equals(p.getName())) {
                    return ret.indexOf(ps);
                }
            }
            return -1;
        } catch (NoResultException exception) {
            return -1;
        }
    }
    public long getRankOfPlayerUsername(String username) {
        try {
        Session session = factory.openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<PlayerStats> criteria = builder.createQuery( PlayerStats.class );
        Root<PlayerStats> root = criteria.from( PlayerStats.class );
        criteria.select(root);
        criteria.orderBy(builder.desc(root.get("score")));
        //TODO completly inefficient fetches all
        TypedQuery<PlayerStats> result = session.createQuery(criteria);
        List<PlayerStats> ret = result.getResultList();
        for(PlayerStats ps : ret) {
            if(ps.getUsername().equals(username)) {
                return ret.indexOf(ps);
            }
        }

        session.close();
        return -1;
       // return ret.indexOf(getMyStatsUsername(username));
        } catch (NoResultException exception) {
            return -1;
        }
    }
    public List<PlayerStats> getToplistAround(Player p) {
        try {
            Session session = factory.openSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<PlayerStats> criteria = builder.createQuery( PlayerStats.class );
            Root<PlayerStats> root = criteria.from( PlayerStats.class );
            criteria.select(root);
            criteria.orderBy(builder.desc(root.get("score")));
            TypedQuery<PlayerStats> result = session.createQuery(criteria);
            int tmp = result.getResultList().indexOf(getMyStats(p.getUniqueId().toString()))-numberOfEntriesAboveRequestedPlayer;
            result.setFirstResult(Math.max(tmp, 0));
            result.setMaxResults(entriesPerToplistPage);
            List<PlayerStats> ret = result.getResultList();
            session.close();
            return ret;
        } catch (NoResultException exception) {
            return null;
        }
    }
    public List<PlayerStats> getPlayerOnDay(LocalDate date) {
        try {
            Session session = factory.openSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<PlayerStats> criteria = builder.createQuery(PlayerStats.class);
            Root<PlayerStats> root = criteria.from(PlayerStats.class);
            criteria.select(root);
            System.out.println("between " +  date.atStartOfDay().toString() + " and " + date.plusDays(1).atStartOfDay());
            criteria.where(
                    builder.and(
                            builder.greaterThan(root.get("modifyDate"), date.atStartOfDay()), //.getUniqueId().toString()
                            builder.lessThan(root.get("modifyDate"), date.plusDays(1).atStartOfDay())
                    )
            );
            TypedQuery<PlayerStats> result = session.createQuery(criteria);
            List<PlayerStats> ret = result.getResultList();
            session.close();
            return ret;
        } catch (NoResultException e) {
        }
        return null;
    }
    public List<PlayerStats> getToplistAroundUsername(String username) {
        try {
            Session session = factory.openSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<PlayerStats> criteria = builder.createQuery( PlayerStats.class );
            Root<PlayerStats> root = criteria.from( PlayerStats.class );
            criteria.select(root);
            criteria.orderBy(builder.desc(root.get("score")));
            TypedQuery<PlayerStats> result = session.createQuery(criteria);
            PlayerStats ps = getMyStatsUsername(username);
            List<PlayerStats> playerStatsList = result.getResultList();
            if(playerStatsList != null && ps != null) {
                int tmp = 0;
                for (PlayerStats p : playerStatsList) {
                    if (p.getUuid().equals(ps.getUuid())) {
                        break;
                    }
                    tmp++;
                }
                tmp -= numberOfEntriesAboveRequestedPlayer;
                result.setFirstResult(Math.max(tmp, 0));
                result.setMaxResults(entriesPerToplistPage);
                List<PlayerStats> ret = result.getResultList();
                session.close();
                return ret;
            } else {
                return new ArrayList<PlayerStats>();
            }
        } catch (NoResultException exception) {
            return null;
        }
    }

    @Override
    public List getAll() {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        List<PlayerStats> ret = HibernateUtil.loadAllData(PlayerStats.class, session);
        session.close();
        return ret;
    }

    @Override
    public void update(PlayerStats o) {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        session.update(o);
        t.commit();
        session.close();
    }

    @Override
    public void delete(PlayerStats o) {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        session.delete(o);
        t.commit();
        session.close();
    }
    public boolean exists(Player player) {
        PlayerStats p = getMyStats(player.getUniqueId().toString());
        return (p != null);
    }
    @Override
    public void add(PlayerStats o) {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        session.persist(o);
        t.commit();
        session.close();
    }
}
