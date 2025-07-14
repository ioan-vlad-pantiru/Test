package com.example.transport.server.repository;

import com.example.transport.server.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

@Repository
@Transactional
public class UserRepository {

    @PersistenceContext
    private EntityManager em;

    /** Find a User by primary key. */
    public User findById(Long id) {
        return em.find(User.class, id);
    }

    /** Find all users. */
    public List<User> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        cq.select(root);
        return em.createQuery(cq).getResultList();
    }

    /** Find a user by username. */
    public User findByUsername(String username) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        cq.select(root)
                .where(cb.equal(root.get("username"), username));
        TypedQuery<User> query = em.createQuery(cq);
        List<User> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    public User findByUsernameAndPassword(String username, String password) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        cq.select(root)
                .where(cb.and(
                        cb.equal(root.get("username"), username),
                        cb.equal(root.get("password"), password)
                ));
        TypedQuery<User> query = em.createQuery(cq);
        List<User> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    /** Save or update a user. */
    public User save(User user) {
        if (user.getId() == null) {
            em.persist(user);
            return user;
        } else {
            return em.merge(user);
        }
    }

    /** Delete a user by ID; returns true if deleted. */
    public boolean deleteById(Long id) {
        User u = em.find(User.class, id);
        if (u != null) {
            em.remove(u);
            return true;
        }
        return false;
    }
}