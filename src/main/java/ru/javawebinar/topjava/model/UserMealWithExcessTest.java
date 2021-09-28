package ru.javawebinar.topjava.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

public class UserMealWithExcessTest {
    private final LocalDateTime dateTime;

    private final String description;

    private final int calories;

    private final boolean excess = false;

    private static HashMap<LocalDate, Integer> dayVsCalories = new HashMap<>();

    private final int caloriesPerDay;

    public UserMealWithExcessTest(LocalDateTime dateTime, String description, int calories, int caloriesPerDay) {
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
        this.caloriesPerDay = caloriesPerDay;
        dayVsCalories.merge(dateTime.toLocalDate(), calories, Integer::sum);
    }

    public UserMealWithExcessTest(UserMeal userMeal, int caloriesPerDay) {
        this.dateTime = userMeal.getDateTime();
        this.description = userMeal.getDescription();
        this.calories = userMeal.getCalories();
        this.caloriesPerDay = caloriesPerDay;
        dayVsCalories.merge(userMeal.getDateTime().toLocalDate(), userMeal.getCalories(), Integer::sum);
    }

    public boolean isExcess() {
        return dayVsCalories.get(this.dateTime.toLocalDate()) > caloriesPerDay;
    }

    @Override
    public String toString() {
        return "UserMealWithExcess{" +
                "dateTime=" + dateTime +
                ", description='" + description + '\'' +
                ", calories=" + calories +
                ", excess=" + isExcess() +
                '}';
    }
}
