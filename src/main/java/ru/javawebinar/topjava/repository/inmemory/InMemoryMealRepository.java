package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);
    private static final Logger log = getLogger(InMemoryMealRepository.class);

    @Override
    public Meal save(int userId, Meal meal) {
        log.info("save {}, userId {}", meal, userId);
        Meal tempMeal = new Meal(meal.getId(), userId, meal.getDateTime(), meal.getDescription(), meal.getCalories());
        if (tempMeal.isNew()) {
            tempMeal.setId(counter.incrementAndGet());
            repository.put(tempMeal.getId(), meal);
            return tempMeal;
        }
        // handle case: update, but not present in storage
        return Objects.equals(repository.computeIfPresent(tempMeal.getId(), (id, oldMeal) -> {
            if (!oldMeal.getUserId().equals(tempMeal.getUserId())) {
                return oldMeal;
            }
            return tempMeal;
        }), tempMeal) ? tempMeal : null;
    }

    @Override
    public boolean delete(int userId, int id) {
        log.info("delete {}, userId {}", id, userId);
        return repository.computeIfPresent(id, (oldId, oldMeal) -> {
            if (oldMeal.getUserId().equals(userId)) {
                return null;
            }
            return oldMeal;
        }) != null;
    }

    @Override
    public Meal get(int userId, int id) {
        log.info("get {}, userId {}", id, userId);
        Meal meal;
        return (meal = repository.get(id)).getUserId().equals(userId) ? meal : null;
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        log.info("getAll, userId {}", userId);
        return repository.values().stream()
                .filter(meal -> meal.getUserId().equals(userId))
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}

