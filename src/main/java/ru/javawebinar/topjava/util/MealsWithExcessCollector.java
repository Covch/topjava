package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class MealsWithExcessCollector implements Collector<Pair<UserMeal, Boolean>, HashMap<LocalDate, Pair<List<Pair<UserMeal, Boolean>>, Integer>>, List<UserMealWithExcess>> {
    private Integer caloriesPerDay;

    public static MealsWithExcessCollector toMealsWithExcessList(Integer caloriesPerDay) {
        return new MealsWithExcessCollector(caloriesPerDay);
    }

    private MealsWithExcessCollector(Integer caloriesPerDay) {
        this.caloriesPerDay = caloriesPerDay;
    }

    @Override
    public Supplier<HashMap<LocalDate, Pair<List<Pair<UserMeal, Boolean>>, Integer>>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<HashMap<LocalDate, Pair<List<Pair<UserMeal, Boolean>>, Integer>>, Pair<UserMeal, Boolean>> accumulator() {
        return (map, pair) -> map.merge(pair.getFirst().getDateTime().toLocalDate(), new Pair<>(Collections.singletonList(pair), pair.getFirst().getCalories()),
                (prev, one) -> {
                    List<Pair<UserMeal, Boolean>> newList = new ArrayList<>();
                    newList.addAll(prev.getFirst());
                    newList.addAll(one.getFirst());
                    return new Pair<>(newList, prev.getSecond() + one.getSecond());
                });
    }

    @Override
    public BinaryOperator<HashMap<LocalDate, Pair<List<Pair<UserMeal, Boolean>>, Integer>>> combiner() {
        return (map1, map2) -> {
            HashMap<LocalDate, Pair<List<Pair<UserMeal, Boolean>>, Integer>> map3 = new HashMap<>(map1);
            map2.forEach((key, value) -> map3.merge(key, value, (v1, v2) -> {
                List<Pair<UserMeal, Boolean>> newList = new ArrayList<>();
                newList.addAll(v1.getFirst());
                newList.addAll(v2.getFirst());
                return new Pair<>(newList, v1.getSecond() + v2.getSecond());
            }));
            return map3;
        };
    }


    @Override
    public Function<HashMap<LocalDate, Pair<List<Pair<UserMeal, Boolean>>, Integer>>, List<UserMealWithExcess>> finisher() {
        return map -> map.values().stream().flatMap(x -> x.getFirst().stream().filter(Pair::getSecond).map(y -> new Pair<>(y, x.getSecond())))
                .map(x -> new UserMealWithExcess(x.getFirst().getFirst(), x.getSecond() > caloriesPerDay)).collect(Collectors.toList());
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.CONCURRENT);
    }
}
