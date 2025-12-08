package ru.murad.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.murad.exception.DaoException;
import ru.murad.util.HibernateUtil;

import java.util.List;
import java.util.Optional;

public abstract class AbstractHibernateDao<T, ID> implements Dao<T, ID> {

    private final Class<T> clazz;

    protected AbstractHibernateDao(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T save(T entity) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();

            return entity;

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new DaoException("Error saving " + clazz.getSimpleName(), e);
        }
    }

    @Override
    public T update(T entity) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            tx = session.beginTransaction();
            T merged = session.merge(entity);
            tx.commit();

            return merged;

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new DaoException("Error updating " + clazz.getSimpleName(), e);
        }
    }

    @Override
    public boolean delete(ID id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            tx = session.beginTransaction();

            T entity = session.find(clazz, id);
            if (entity == null) {
                return false;
            }

            session.remove(entity);
            tx.commit();

            return true;

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new DaoException("Error deleting " + clazz.getSimpleName(), e);
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            T entity = session.find(clazz, id);
            return Optional.ofNullable(entity);

        } catch (Exception e) {
            throw new DaoException("Error finding " + clazz.getSimpleName(), e);
        }
    }

    @Override
    public List<T> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            return session.createQuery(
                    "SELECT e FROM " + clazz.getSimpleName() + " e", clazz
            ).getResultList();

        } catch (Exception e) {
            throw new DaoException("Error loading all " + clazz.getSimpleName(), e);
        }
    }
}
