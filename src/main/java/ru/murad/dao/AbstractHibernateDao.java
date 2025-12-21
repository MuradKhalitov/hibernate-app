package ru.murad.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import ru.murad.exception.DaoException;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractHibernateDao<T, ID> implements Dao<T, ID> {

    protected final SessionFactory sessionFactory;
    protected final Class<T> clazz;

    protected AbstractHibernateDao(Class<T> clazz, SessionFactory sessionFactory) {
        this.clazz = clazz;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public T save(T entity) {
        return executeInTransaction(session -> {
            session.persist(entity);
            return entity;
        }, "saving");
    }

    @Override
    public T update(T entity) {
        return executeInTransaction(session -> session.merge(entity), "updating");
    }

    @Override
    public boolean delete(ID id) {
        return executeInTransaction(session -> {
            T entity = session.find(clazz, id);
            if (entity == null) {
                return false;
            }
            session.remove(entity);
            return true;
        }, "deleting id=" + id);
    }

    @Override
    public Optional<T> findById(ID id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(clazz, id));
        } catch (Exception e) {
            throw new DaoException(
                    "Error finding " + entityName() + " with id=" + id, e
            );
        }
    }

    @Override
    public List<T> findAll() {
        try (Session session = sessionFactory.openSession()) {
            String entityName = entityName();
            return session.createQuery(
                    "select e from " + entityName + " e", clazz
            ).getResultList();
        } catch (Exception e) {
            throw new DaoException(
                    "Error loading all " + entityName(), e
            );
        }
    }

    protected <R> R executeInTransaction(
            Function<Session, R> action,
            String operation
    ) {
        Transaction tx = null;

        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            R result = action.apply(session);
            tx.commit();
            return result;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new DaoException(
                    "Error " + operation + " " + entityName(), e
            );
        }
    }

    protected String entityName() {
        return sessionFactory
                .getMetamodel()
                .entity(clazz)
                .getName();
    }
}
