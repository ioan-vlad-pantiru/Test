package com.example.transport.server.repository;

import com.example.transport.server.entity.Ride;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RideRepository {
    private static final Logger logger = LoggerFactory.getLogger(RideRepository.class);
    @PersistenceContext private EntityManager em;

    public List<Ride> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ride> cq = cb.createQuery(Ride.class);
        Root<Ride> root = cq.from(Ride.class);
        cq.select(root);
        return em.createQuery(cq).getResultList();
    }

    public List<Ride> search(String destination, LocalDate date) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ride> cq = cb.createQuery(Ride.class);
        Root<Ride> root = cq.from(Ride.class);

        List<Predicate> predicates = new ArrayList<>();

        if (destination != null && !destination.isEmpty()) {
            predicates.add(
                cb.like(
                    cb.lower(root.get("destination")),
                    "%" + destination.toLowerCase() + "%"
                )
            );
        }
        if (date != null) {
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();
            predicates.add(
                cb.and(
                    cb.greaterThanOrEqualTo(root.get("departureTime"), start),
                    cb.lessThan(root.get("departureTime"), end)
                )
            );
        }

        cq.where(predicates.toArray(new Predicate[0]));

        List<Ride> result = em.createQuery(cq).getResultList();
        logger.info("Found {} rides for search destination='{}', date='{}'",
                result.size(), destination, (date != null ? date : ""));
        return result;
    }

    public Ride findById(Long id) {
        return em.find(Ride.class, id);
    }

    public Ride save(Ride ride) {
        if (ride.getId() == null) {
            em.persist(ride);
            logger.info("Saved new ride to {} ({} seats)",
                    ride.getDestination(), ride.getSeatsAvailable());
        } else {
            ride = em.merge(ride);
            logger.info("Updated ride {} to {}",
                    ride.getId(), ride.getDestination());
        }
        return ride;
    }

    public void delete(Ride ride) {
        em.remove(ride);
        logger.info("Deleted ride {} to {}", ride.getId(), ride.getDestination());
    }

    public boolean deleteById(Long id) {
        Ride ride = findById(id);
        if (ride != null) {
            delete(ride);
        } else {
            logger.warn("Ride with ID {} not found for deletion", id);
        }
        return false;
    }

    public List<Ride> findAll(String destinationFilter, Integer page, Integer size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ride> cq = cb.createQuery(Ride.class);
        Root<Ride> root = cq.from(Ride.class);
        cq.select(root);

        if (destinationFilter != null && !destinationFilter.isEmpty()) {
            Predicate predicate = cb.like(
                cb.lower(root.get("destination")),
                "%" + destinationFilter.toLowerCase() + "%"
            );
            cq.where(predicate);
        }

        if (page != null && size != null) {
            return em.createQuery(cq)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
        } else {
            return em.createQuery(cq).getResultList();
        }
    }

    public List<Ride> findByDestinationAndDate(String destination, LocalDate dateFilter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ride> cq = cb.createQuery(Ride.class);
        Root<Ride> root = cq.from(Ride.class);

        List<Predicate> predicates = new ArrayList<>();

        if (destination != null && !destination.isEmpty()) {
            predicates.add(
                cb.like(
                    cb.lower(root.get("destination")),
                    "%" + destination.toLowerCase() + "%"
                )
            );
        }
        if (dateFilter != null) {
            LocalDateTime start = dateFilter.atStartOfDay();
            LocalDateTime end = dateFilter.plusDays(1).atStartOfDay();
            predicates.add(
                cb.and(
                    cb.greaterThanOrEqualTo(root.get("departureTime"), start),
                    cb.lessThan(root.get("departureTime"), end)
                )
            );
        }

        cq.where(predicates.toArray(new Predicate[0]));

        return em.createQuery(cq).getResultList();
    }

    public List<Ride> findByDestination(String destination) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ride> cq = cb.createQuery(Ride.class);
        Root<Ride> root = cq.from(Ride.class);

        if (destination != null && !destination.isEmpty()) {
            Predicate predicate = cb.like(
                cb.lower(root.get("destination")),
                "%" + destination.toLowerCase() + "%"
            );
            cq.where(predicate);
        }

        return em.createQuery(cq).getResultList();
    }
}