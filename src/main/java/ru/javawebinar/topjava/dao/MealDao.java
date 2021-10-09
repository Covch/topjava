package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealDao {
    void addMeal(Meal meal);

    void deleteMeal(long id);

    void updateMeal(Meal meal);

    List<Meal> getAllMeals();

    Meal getMealById(long id);
}
