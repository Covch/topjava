package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Logger log = getLogger(InMemoryMealRepository.class);
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> this.save(1,
                new Meal(meal.getDateTime(), meal.getDescription() + " юзера", meal.getCalories())));
        MealsUtil.meals.forEach(meal -> this.save(2,
                new Meal(meal.getDateTime(), meal.getDescription() + " админа", meal.getCalories())));
    }

    @Override
    public Meal save(int userId, Meal meal) {
        log.info("save {}, userId {}", meal, userId);
        Meal tempMeal = new Meal(meal.getId(), meal.getDateTime(), meal.getDescription(), meal.getCalories());
        if (tempMeal.isNew()) {
            tempMeal.setId(counter.incrementAndGet());
            repository.computeIfAbsent(userId, (newMap) -> new ConcurrentHashMap<>()).put(tempMeal.getId(), tempMeal);
            return tempMeal;
        }
        // handle case: update, but not present in storage
        return repository.computeIfPresent(userId, (mapId, oldMap) -> {
            oldMap.put(tempMeal.getId(), tempMeal);
            return oldMap;
        }) == null ? null : tempMeal;
    }

    @Override
    public boolean delete(int userId, int id) {
        log.info("delete {}, userId {}", id, userId);
        return repository.get(userId) != null && repository.get(userId).remove(id) != null;
    }

    @Override
    public Meal get(int userId, int id) {
        log.info("get {}, userId {}", id, userId);
        return repository.get(userId) != null ? repository.get(userId).get(id) : null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        log.info("getAll, userId {}", userId);
        if (repository.get(userId) != null) {
            return filterByPredicate(repository.get(userId).values(), meal -> true);
        }
        return new ArrayList<>();
    }

    @Override
    public List<Meal> getAllInInterval(int userId, LocalDate startDate, LocalDate endDate) {
        log.info("getAll, userId {}, from {} to {}", userId, startDate, endDate);
        if (repository.get(userId) != null) {
            return filterByPredicate(repository.get(userId).values(),
                    meal -> DateTimeUtil.isBetweenClosed(meal.getDate(), startDate, endDate));
        }
        return new ArrayList<>();
    }

    private List<Meal> filterByPredicate(Collection<Meal> meals, Predicate<Meal> filter) {
        return meals.stream()
                .filter(filter)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}

