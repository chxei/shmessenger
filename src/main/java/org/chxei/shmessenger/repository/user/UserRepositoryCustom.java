package org.chxei.shmessenger.repository.user;

import com.google.common.collect.Lists;
import jakarta.persistence.Query;
import org.chxei.shmessenger.entity.user.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;


//todo autocommit transaction
//todo test all methods
//todo checks for not update or delete whole tables
@Repository
public class UserRepositoryCustom implements CrudRepository<User, Long> {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private DataSource dataSource; //todo use datasource

    //todo start transactions automatically
    //todo session.close automatically
    @NotNull
    @Override
    public <S extends User> S save(@NotNull S entity) {
        Session session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.save(entity);
        session.close();
        return entity;
    }

    @NotNull
    @Override
    public <S extends User> Iterable<S> saveAll(Iterable<S> entities) {
        Session session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        for (S entity : entities) {
            session.save(entity);
        }
        session.close();
        return entities;
    }

    @NotNull
    @Override
    public Optional<User> findById(@NotNull Long aLong) {
        Session session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        User user = session.get(User.class, aLong);
        session.close();
        if (user != null) {
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    @NotNull
    public Optional<User> findbyusername(@NotNull String username) {
        Session session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        Optional<User> user = session.createQuery("select s from users s where s.username = :username").setParameter("username", username).getResultList().stream().findFirst();
        session.close();
        return user;
    }

    @Override
    public boolean existsById(@NotNull Long aLong) {
        Session session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        User user = session.get(User.class, aLong);
        session.close();
        return user != null;
    }

    public boolean existsByUsername(@NotNull String username) {
        return findbyusername(username).isPresent();
    }

    @NotNull
    @Override
    public Iterable<User> findAll() {
        Session session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        List<User> users = (List<User>) session.createQuery("select s from users s").list();
        session.close();
        return users;
    }

    @NotNull
    @Override
    public Iterable<User> findAllById(@NotNull Iterable<Long> longs) {
        Session session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();

        List<User> users = (List<User>) session.createQuery("select s from users s where s.id in :id").setParameterList("id", Lists.newArrayList(longs)).list();
        session.close();
        return users;
    }


    public void updateUser(User user) {
        Session session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.update(user);
    }

    @Override
    public long count() {
        Session session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery("select count(*) from users s");
        long count = query.getFirstResult();
        session.close();
        return count;
    }

    @Override
    public void deleteById(@NotNull Long aLong) {
        Session session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.delete(session.get(User.class, aLong));
        session.close();
    }

    @Override
    public void delete(@NotNull User entity) {
        Session session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.delete(entity);
        session.close();
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
        //TODO
    }

    @Override
    public void deleteAll(@NotNull Iterable<? extends User> entities) {
        Session session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        for (User entity : entities) {
            session.delete(entity);
        }
        session.close();
    }

    @Override
    public void deleteAll() {
        Session session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.createQuery("delete from users").executeUpdate();
        session.close();
    }
}
