package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class MealsWithExcessCollector implements Collector<UserMeal, HashMap<LocalDate, Pair<List<UserMeal>, Integer>>, List<UserMealWithExcess>> {
    private Integer caloriesPerDay;
    private LocalTime startTime, endTime;

    public static MealsWithExcessCollector toMealsWithExcessList(LocalTime startTime, LocalTime endTime, Integer caloriesPerDay) {
        return new MealsWithExcessCollector(startTime, endTime, caloriesPerDay);
    }

    private MealsWithExcessCollector(LocalTime startTime, LocalTime endTime, Integer caloriesPerDay) {
        this.caloriesPerDay = caloriesPerDay;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public Supplier<HashMap<LocalDate, Pair<List<UserMeal>, Integer>>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<HashMap<LocalDate, Pair<List<UserMeal>, Integer>>, UserMeal> accumulator() {
        return (map, userMeal) -> map.merge(userMeal.getDateTime().toLocalDate(), new Pair<>(Collections.singletonList(userMeal), userMeal.getCalories()),
                (prev, one) -> {
                    List<UserMeal> newList = new ArrayList<>();
                    newList.addAll(prev.getFirst());
                    newList.addAll(one.getFirst());
                    return new Pair<>(newList, prev.getSecond() + one.getSecond());
                });
    }

    @Override
    public BinaryOperator<HashMap<LocalDate, Pair<List<UserMeal>, Integer>>> combiner() {
        return (map1, map2) -> {
            HashMap<LocalDate, Pair<List<UserMeal>, Integer>> map3 = new HashMap<>(map1);
            map2.forEach((key, value) -> map3.merge(key, value, (prev, one) -> {
                List<UserMeal> newList = new ArrayList<>();
                newList.addAll(prev.getFirst());
                newList.addAll(one.getFirst());
                return new Pair<>(newList, prev.getSecond() + one.getSecond());
            }));
            return map3;
        };
    }


    @Override
    public Function<HashMap<LocalDate, Pair<List<UserMeal>, Integer>>, List<UserMealWithExcess>> finisher() {
        return map -> map.values().stream()
                .flatMap(pair -> pair.getFirst().stream()
                        .filter(userMeal -> TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime))
                        .map(userMeal -> new Pair<>(userMeal, pair.getSecond())))
                .map(pair -> UserMealsUtil.convertUserMealToUserMealWithExcess(pair.getFirst(), pair.getSecond() > caloriesPerDay))
                .collect(Collectors.toList());
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.CONCURRENT);
    }
}
