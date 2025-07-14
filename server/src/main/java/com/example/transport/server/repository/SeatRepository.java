package com.example.transport.server.repository;

import com.example.transport.server.entity.Seat;
import com.example.transport.server.entity.Ride;
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
public class SeatRepository {

    @PersistenceContext
    private EntityManager em;

    /** Find a Seat by its primary key. */
    public Seat findById(Long id) {
        return em.find(Seat.class, id);
    }

    /** Find all seats. */
    public List<Seat> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Seat> cq = cb.createQuery(Seat.class);
        Root<Seat> root = cq.from(Seat.class);
        cq.select(root);
        return em.createQuery(cq).getResultList();
    }

    /** Find all seats for a given ride ID. */
    public List<Seat> findByRideId(Long rideId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Seat> cq = cb.createQuery(Seat.class);
        Root<Seat> root = cq.from(Seat.class);
        Join<Seat, Ride> rideJoin = root.join("ride");
        cq.select(root)
                .where(cb.equal(rideJoin.get("id"), rideId))
                .orderBy(cb.asc(root.get("seatNumber")));
        TypedQuery<Seat> query = em.createQuery(cq);
        return query.getResultList();
    }

    /** Find only free (unreserved) seats for a ride. */
    public List<Seat> findFreeSeatsByRideId(Long rideId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Seat> cq = cb.createQuery(Seat.class);
        Root<Seat> root = cq.from(Seat.class);
        Join<Seat, Ride> rideJoin = root.join("ride");
        cq.select(root)
                .where(
                        cb.and(
                                cb.equal(rideJoin.get("id"), rideId),
                                cb.isNull(root.get("reservation"))
                        )
                )
                .orderBy(cb.asc(root.get("seatNumber")));
        TypedQuery<Seat> query = em.createQuery(cq);
        return query.getResultList();
    }

    /** Save or update a seat. */
    public Seat save(Seat seat) {
        if (seat.getId() == null) {
            em.persist(seat);
            return seat;
        } else {
            return em.merge(seat);
        }
    }

    /** Delete a seat by ID; returns true if deleted. */
    public boolean deleteById(Long id) {
        Seat seat = em.find(Seat.class, id);
        if (seat != null) {
            em.remove(seat);
            return true;
        }
        return false;
    }
}