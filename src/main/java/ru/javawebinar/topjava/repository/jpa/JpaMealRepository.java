package ru.javawebinar.topjava.repository.jpa;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Repository
@Transactional(readOnly = true)
public class JpaMealRepository implements MealRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public Meal save(Meal meal, int userId) {
        User refToUser = em.getReference(User.class, userId);
        meal.setUser(refToUser);
        if (meal.isNew()) {
            em.persist(meal);
            return meal;
        } else {
            Meal refToMeal = em.getReference(Meal.class, meal.getId());
            if (refToMeal != null && refToUser != null &&
                    Objects.equals(refToUser.getId(), refToMeal.getUser().getId())) {
                return em.merge(meal);
            }
        }
        return null;
    }

    @Override
    @Transactional
    public boolean delete(int id, int userId) {
        User refToUser = em.getReference(User.class, userId);
        Meal meal = em.find(Meal.class, id);
        if (meal != null && refToUser != null &&
                Objects.equals(refToUser.getId(), meal.getUser().getId())) {
            em.remove(meal);
            return true;
        }
        return false;
    }

    @Override
    public Meal get(int id, int userId) {
        User refToUser = em.getReference(User.class, userId);
        Meal meal = em.find(Meal.class, id);
        if (meal != null && refToUser != null
                && Objects.equals(refToUser.getId(), meal.getUser().getId())) {
            return meal;
        }
        return null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return em.createNamedQuery(Meal.ALL_SORTED, Meal.class)
                .setParameter(1, userId)
                .getResultList();
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return em.createNamedQuery(Meal.BETWEEN_HALF_OPEN, Meal.class)
                .setParameter(1, userId)
                .setParameter(2, startDateTime)
                .setParameter(3, endDateTime)
                .getResultList();
    }
}