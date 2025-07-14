package com.example.transport.server.repository;

import com.example.transport.server.entity.Reservation;
import com.example.transport.server.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import java.util.List;

@Repository
@Transactional
public class ReservationRepository {

    @PersistenceContext
    private EntityManager em;

    /** Find a Reservation by its primary key. */
    public Reservation findById(Long id) {
        return em.find(Reservation.class, id);
    }

    /** Find all reservations. */
    public List<Reservation> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Reservation> cq = cb.createQuery(Reservation.class);
        Root<Reservation> root = cq.from(Reservation.class);
        cq.select(root);
        return em.createQuery(cq).getResultList();
    }

    /** Find all reservations for a specific user ID. */
    public List<Reservation> findByUserId(Long userId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Reservation> cq = cb.createQuery(Reservation.class);
        Root<Reservation> root = cq.from(Reservation.class);
        Join<Reservation, User> userJoin = root.join("user");
        cq.select(root)
                .where(cb.equal(userJoin.get("id"), userId));
        TypedQuery<Reservation> query = em.createQuery(cq);
        return query.getResultList();
    }

    /** Save a new or update an existing reservation. */
    public Reservation save(Reservation reservation) {
        if (reservation.getId() == null) {
            em.persist(reservation);
            return reservation;
        } else {
            return em.merge(reservation);
        }
    }

    /** Delete a reservation by ID; returns true if deleted. */
    public boolean deleteById(Long id) {
        Reservation res = em.find(Reservation.class, id);
        if (res != null) {
            em.remove(res);
            return true;
        }
        return false;
    }
}