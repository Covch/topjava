package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByStreams(meals, LocalTime.of(0, 0), LocalTime.of(23, 0), 2000);
        mealsTo.forEach(System.out::println);

//        System.out.println(filteredByStreams(meals, LocalTime.of(0, 0), LocalTime.of(23, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO return filtered list with excess. Implement by cycles
        List<UserMealWithExcess> userMealsWithExcess = new ArrayList<>();
        Map<String, Integer> dayVsCalories = new HashMap<>();
        Map<String, List<UserMeal>> dayVsMeals = new HashMap<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        for (UserMeal meal : meals
        ) {
            String day = meal.getDateTime().format(dateTimeFormatter);
            dayVsCalories.put(day, dayVsCalories.containsKey(day) ? dayVsCalories.get(day) + meal.getCalories() : meal.getCalories());
            if (dayVsMeals.containsKey(day)) {
                dayVsMeals.get(day).add(meal);
            } else {
                List<UserMeal> list = new ArrayList<>();
                list.add(meal);
                dayVsMeals.put(day, list);
            }
        }
        for (Map.Entry<String, Integer> entry : dayVsCalories.entrySet()
        ) {
            if (entry.getValue() > caloriesPerDay) {
                for (UserMeal meal : dayVsMeals.get(entry.getKey())
                ) {
                    userMealsWithExcess.add(new UserMealWithExcess(meal, true));
                }
            } else {
                for (UserMeal meal : dayVsMeals.get(entry.getKey())
                ) {
                    userMealsWithExcess.add(new UserMealWithExcess(meal, false));
                }
            }
        }
        return userMealsWithExcess;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO Implement by streams
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        Map<String, Integer> dayVsCalories = meals.stream().collect(Collectors.groupingBy(x -> x.getDateTime().format(dateTimeFormatter), Collectors.summingInt(UserMeal::getCalories)));
        Map<String, List<UserMeal>> dayVsMeals = meals.stream().collect(Collectors.groupingBy(x -> x.getDateTime().format(dateTimeFormatter)));
        return dayVsMeals.entrySet().stream().flatMap(x -> dayVsCalories.get(x.getKey()) > caloriesPerDay ? x.getValue().stream().map(y -> new UserMealWithExcess(y, true)) : x.getValue().stream().map(y -> new UserMealWithExcess(y, false))).collect(Collectors.toList());
    }
}
