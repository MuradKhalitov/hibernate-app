package ru.murad.dao;

import ru.murad.model.User;

public class UserDao extends AbstractHibernateDao<User, Long> {

    public UserDao() {
        super(User.class);
    }
}

