package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
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
        Meal tempMeal = new Meal(meal.getId(), userId, meal.getDateTime(), meal.getDescription(), meal.getCalories());
        if (tempMeal.isNew()) {
            tempMeal.setId(counter.incrementAndGet());
            repository.putIfAbsent(userId, new ConcurrentHashMap<>());
            repository.get(userId).put(tempMeal.getId(), tempMeal);
            return tempMeal;
        }
        // handle case: update, but not present in storage
        return repository.getOrDefault(userId, new ConcurrentHashMap<>())
                .computeIfPresent(tempMeal.getId(), (id, oldMeal) -> tempMeal);
    }

    @Override
    public boolean delete(int userId, int id) {
        log.info("delete {}, userId {}", id, userId);
        AtomicReference<Boolean> result = new AtomicReference<>(false);
        repository.getOrDefault(userId, new ConcurrentHashMap<>()).computeIfPresent(id, (mapId, oldMeal) -> {
            result.set(true);
            return null;
        });
        return result.get();
    }

    @Override
    public Meal get(int userId, int id) {
        log.info("get {}, userId {}", id, userId);
        return repository.getOrDefault(userId, new ConcurrentHashMap<>()).get(id);
    }

    @Override
    public List<Meal> getAll(int userId) {
        log.info("getAll, userId {}", userId);
        return filterByPredicate(repository.getOrDefault(userId, new ConcurrentHashMap<>()).values(), meal -> true);
    }

    @Override
    public List<Meal> getAll(int userId, LocalDate startDate, LocalDate endDate) {
        log.info("getAll, userId {}, from {} to {}", userId, startDate, endDate);
        return filterByPredicate(repository.getOrDefault(userId, new ConcurrentHashMap<>()).values()
                , meal -> DateTimeUtil.isBetweenClosed(meal.getDate(), startDate, endDate));
    }

    private List<Meal> filterByPredicate(Collection<Meal> meals, Predicate<Meal> filter) {
        return meals.stream()
                .filter(filter)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}

