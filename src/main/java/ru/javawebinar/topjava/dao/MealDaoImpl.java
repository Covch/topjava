package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MealDaoImpl implements MealDao {
    private static Map<Long, Meal> mealStorage = new ConcurrentHashMap<>();
    private static final AtomicLong idCounter = new AtomicLong(-1);

    static {
        List<Meal> meals = Arrays.asList(
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );
        meals.forEach(meal -> {
            long mealId = idCounter.incrementAndGet();
            meal.setId(mealId);
            mealStorage.put(mealId, meal);
        });
    }

    @Override
    public void addMeal(Meal meal) {
        long mealId = idCounter.incrementAndGet();
        meal.setId(mealId);
        mealStorage.putIfAbsent(mealId, meal);
    }

    @Override
    public void deleteMeal(long id) {
        mealStorage.remove(id);
    }

    @Override
    public void updateMeal(Meal meal) {
        Meal tempMeal = new Meal(meal.getDateTime(), meal.getDescription(), meal.getCalories());
        tempMeal.setId(meal.getId());
        mealStorage.replace(tempMeal.getId(), tempMeal);
    }

    @Override
    public List<Meal> getAllMeals() {
        return new ArrayList<>(mealStorage.values());
    }

    @Override
    public Meal getMealById(long id) {
        return mealStorage.get(id);
    }
}
