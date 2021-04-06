package de.peacepunkt.tda2plugin.persistence;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.kits.kits.*;
import de.peacepunkt.tda2plugin.persistence.novote.Extra;
import de.peacepunkt.tda2plugin.persistence.xp.Xp;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class HibernateUtil {
    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;

    //Sorry for using the Singleton Anti Pattern
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Configuration config = new Configuration();
            config.configure("hibernate.cfg.xml");
            config.setProperty("hibernate.connection.username", Main.sqlUser);
            config.setProperty("hibernate.connection.password", Main.sqlPassword );

            config.addAnnotatedClass(PlayerStats.class);
            config.addAnnotatedClass(MVP.class);
            config.addAnnotatedClass(Extra.class);
            config.addAnnotatedClass(Xp.class);
            config.addAnnotatedClass(DailyStats.class);
            config.addAnnotatedClass(MiniArenaBlocks.class);
            config.addAnnotatedClass(BubiCounter.class);

            config.addAnnotatedClass(KitArcher.class);
            config.addAnnotatedClass(KitTrapper.class);
            config.addAnnotatedClass(KitSwordsman.class);
            config.addAnnotatedClass(KitSpearman.class);
            config.addAnnotatedClass(KitChaos.class);
            config.addAnnotatedClass(KitScout.class);
            config.addAnnotatedClass(KitSense.class);
            config.addAnnotatedClass(KitWaterman.class);
            config.addAnnotatedClass(KitPirate.class);
            config.addAnnotatedClass(KitExecutioner.class);
            config.addAnnotatedClass(KitHealer.class);
            config.addAnnotatedClass(KitAcrobat.class);
            config.addAnnotatedClass(KitHalberdier.class);
            config.addAnnotatedClass(KitBerserker.class);
            config.addAnnotatedClass(KitSticker.class);
            config.addAnnotatedClass(KitHorseman.class);

            sessionFactory = config.buildSessionFactory(new StandardServiceRegistryBuilder().applySettings(config.getProperties()).build());
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    public static <T> List<T> loadAllData(Class<T> type, Session session) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(type);
        criteria.from(type);
        List<T> data = session.createQuery(criteria).getResultList();
        return data;
    }
}
