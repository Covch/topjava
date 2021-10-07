package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
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

        List<UserMealWithExcess> mealsTo = filteredByStream(meals, LocalTime.of(12, 0), LocalTime.of(23, 0), 2000);
        mealsTo.forEach(System.out::println);
    }

    public static UserMealWithExcess convertUserMealToUserMealWithExcess(UserMeal userMeal, boolean excess) {
        return new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), excess);
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> dayVsCalories = new HashMap<>();
        for (UserMeal meal : meals) {
            dayVsCalories.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
        }
        List<UserMealWithExcess> userMealsWithExcess = new ArrayList<>();
        for (UserMeal meal : meals) {
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                userMealsWithExcess.add(convertUserMealToUserMealWithExcess(meal, dayVsCalories.get(meal.getDateTime().toLocalDate()) > caloriesPerDay));
            }
        }
        return userMealsWithExcess;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> dayVsCalories = meals.stream()
                .collect(Collectors.groupingBy(userMeal -> userMeal.getDateTime().toLocalDate(), Collectors.summingInt(UserMeal::getCalories)));
        return meals.stream().filter(userMeal -> TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime))
                .map(userMeal -> convertUserMealToUserMealWithExcess(userMeal, dayVsCalories.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByStream(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return meals.stream()
                .collect(new MealsWithExcessCollector(startTime, endTime, caloriesPerDay));
    }

    private static class MealsWithExcessCollector implements Collector<UserMeal, HashMap<LocalDate, Pair<List<UserMeal>, Integer>>, List<UserMealWithExcess>> {
        private Integer caloriesPerDay;
        private LocalTime startTime, endTime;

        public MealsWithExcessCollector(LocalTime startTime, LocalTime endTime, Integer caloriesPerDay) {
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
            return (map, userMeal) -> map.merge(userMeal.getDateTime().toLocalDate(), TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)
                            ? new Pair<>(Collections.singletonList(userMeal), userMeal.getCalories())
                            : new Pair<>(new ArrayList<>(), userMeal.getCalories()),
                    (prev, one) -> {
                        if (!one.getFirst().isEmpty()) {
                            prev.getFirst().add(one.getFirst().get(0));
                        }
                        return new Pair<>(prev.getFirst(), prev.getSecond() + one.getSecond());
                    });
        }

        @Override
        public BinaryOperator<HashMap<LocalDate, Pair<List<UserMeal>, Integer>>> combiner() {
            return (map1, map2) -> {
                HashMap<LocalDate, Pair<List<UserMeal>, Integer>> map3 = new HashMap<>(map1);
                map2.forEach((key, value) -> map3.merge(key, value, (prev, one) -> {
                    prev.getFirst().addAll(one.getFirst());
                    return new Pair<>(prev.getFirst(), prev.getSecond() + one.getSecond());
                }));
                return map3;
            };
        }


        @Override
        public Function<HashMap<LocalDate, Pair<List<UserMeal>, Integer>>, List<UserMealWithExcess>> finisher() {
            return map -> map.values().stream()
                    .flatMap(pair -> pair.getFirst().stream()
                            .map(userMeal -> UserMealsUtil.convertUserMealToUserMealWithExcess(userMeal, pair.getSecond() > caloriesPerDay)))
                    .collect(Collectors.toList());
        }

        @Override
        public Set<Characteristics> characteristics() {
            return EnumSet.of(Characteristics.CONCURRENT);
        }
    }

    private static class Pair<U, V> {

        /**
         * The first element of this <code>Pair</code>
         */
        private U first;

        /**
         * The second element of this <code>Pair</code>
         */
        private V second;

        /**
         * Constructs a new <code>Pair</code> with the given values.
         *
         * @param first  the first element
         * @param second the second element
         */
        public Pair(U first, V second) {

            this.first = first;
            this.second = second;
        }

        public U getFirst() {
            return first;
        }

        public V getSecond() {
            return second;
        }
    }
}
