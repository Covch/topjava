package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Logger log = getLogger(InMemoryMealRepository.class);
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> this.save(1, new Meal(meal.getDateTime(), meal.getDescription() + " юзера", meal.getCalories())));
        MealsUtil.meals.forEach(meal -> this.save(2, new Meal(meal.getDateTime(), meal.getDescription() + " админа", meal.getCalories())));
    }

    @Override
    public Meal save(int userId, Meal meal) {
        log.info("save {}, userId {}", meal, userId);
        Meal tempMeal = new Meal(meal.getId(), userId, meal.getDateTime(), meal.getDescription(), meal.getCalories());
        if (tempMeal.isNew()) {
            tempMeal.setId(counter.incrementAndGet());
            repository.put(tempMeal.getId(), tempMeal);
            return tempMeal;
        }
        // handle case: update, but not present in storage
        return repository.computeIfPresent(tempMeal.getId(), (id, oldMeal) -> oldMeal.getUserId().equals(userId) ? tempMeal : oldMeal) == tempMeal ? tempMeal : null;
    }

    @Override
    public boolean delete(int userId, int id) {
        log.info("delete {}, userId {}", id, userId);
        return repository.containsKey(id) && repository.computeIfPresent(id, (mapId, oldMeal) -> oldMeal.getUserId().equals(userId) ? null : oldMeal) == null;
    }

    @Override
    public Meal get(int userId, int id) {
        log.info("get {}, userId {}", id, userId);
        Meal meal = repository.get(id);
        return meal == null ? null : meal.getUserId().equals(userId) ? meal : null;
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        log.info("getAll, userId {}", userId);
        return repository.values().stream()
                .filter(meal -> meal.getUserId().equals(userId))
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Meal> getAll(int userId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        log.info("getAll, userId {}, from {} to {}", userId, startDateTime.toLocalDate(), endDateTime.toLocalDate());
        return getAll(userId).stream()
                .filter(meal -> DateTimeUtil.isBetweenClosed(meal.getDate(), startDateTime.toLocalDate(), endDateTime.toLocalDate()))
                .collect(Collectors.toList());
    }
}

