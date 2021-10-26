package ru.javawebinar.topjava;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    private static final Logger log = getLogger(MealTestData.class);
    public static final int USER_MEAL_ID = START_SEQ + 2;
    public static final int ADMIN_DELETE_MEAL_ID = START_SEQ + 7;
    public static final int ADMIN_UPDATE_MEAL_ID = START_SEQ + 8;
    public static final int NOT_FOUND_MEAL = 15;

    public static final Meal userMeal = new Meal(USER_MEAL_ID, LocalDateTime.of(2020, 12, 21, 10, 0),
            "User`s meal 1", 100);
    public static final Meal userMeal2 = new Meal(START_SEQ + 3, LocalDateTime.of(2021, 12, 25, 11, 0),
            "User`s meal 2", 200);
    public static final Meal userMeal3 = new Meal(START_SEQ + 4, LocalDateTime.of(2021, 12, 15, 0, 1),
            "User`s meal 3", 1100);
    public static final Meal userMeal4 = new Meal(START_SEQ + 5, LocalDateTime.of(2021, 12, 15, 0, 0),
            "User`s meal 4", 1000);
    public static final Meal userMeal5 = new Meal(START_SEQ + 6, LocalDateTime.of(2021, 12, 14, 23, 59),
            "User`s meal 5", 500);
    public static final Meal adminMealDel = new Meal(ADMIN_DELETE_MEAL_ID, LocalDateTime.of(2020, 11, 12, 5, 31),
            "Admin`s meal 1 for deleting", 100);
    public static final Meal adminMealUpd = new Meal(ADMIN_UPDATE_MEAL_ID, LocalDateTime.of(1000, 1, 1, 1, 1),
            "Admin`s meal 2 for updating", 100);

    public static final List<Meal> userMealListAll = Arrays.asList(userMeal2, userMeal3, userMeal4, userMeal5, userMeal);

    public static final List<Meal> userMealListStop = Arrays.asList(userMeal5, userMeal);

    public static final List<Meal> userMealListStart = Arrays.asList(userMeal2, userMeal3, userMeal4);

    public static final List<Meal> userMealListStartAndStop = Arrays.asList(userMeal3, userMeal4, userMeal5);

    public static Meal getNewMeal() {
        return new Meal(null, LocalDateTime.of(2020, 2, 2, 2, 2), "New meal", 1234);
    }

    public static Meal getUpdatedMeal() {
        Meal updated = new Meal(adminMealUpd);
        updated.setDateTime(LocalDateTime.of(2021, 1, 1, 1, 1));
        updated.setDescription("UpdatedDescription");
        updated.setCalories(4321);
        return updated;
    }

    public static void assertMatch(Meal actual, Meal expected) {
        log.debug("actual: {}", actual);
        log.debug("expected: {}", expected);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... meals) {
        assertMatch(actual, Arrays.asList(meals));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        log.debug("actual: {}", actual);
        log.debug("expected: {}", expected);
        assertThat(actual).usingRecursiveFieldByFieldElementComparator().isEqualTo(expected);
    }
}
