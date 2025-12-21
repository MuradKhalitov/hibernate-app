package ru.murad.dao;

import org.hibernate.SessionFactory;
import ru.murad.model.User;

public class UserDao extends AbstractHibernateDao<User, Long> {

    public UserDao(SessionFactory sessionFactory) {
        super(User.class, sessionFactory);
    }
}

