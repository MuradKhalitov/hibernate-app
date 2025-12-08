package ru.murad.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();

            // ВАЖНО: НЕ вызываем configure() — Hibernate сам читает hibernate.properties

            configuration.addAnnotatedClass(ru.murad.model.User.class);

            StandardServiceRegistryBuilder builder =
                    new StandardServiceRegistryBuilder()
                            .applySettings(configuration.getProperties());

            return configuration.buildSessionFactory(builder.build());

        } catch (Exception ex) {
            System.err.println("Ошибка создания SessionFactory: " + ex.getMessage());
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
