package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.Arrays;

import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;
import static org.assertj.core.api.Assertions.assertThat;

public class MealTestData {
    public static final int USER_ID = START_SEQ;
    public static final int ADMIN_ID = START_SEQ + 1;
    public static final int USER_MEAL_ID = START_SEQ + 2;
    public static final int ADMIN_MEAL_ID = START_SEQ + 3;
    public static final int SECOND_USER_MEAL_ID = START_SEQ + 4;
    public static final int NOT_FOUND = 10;

    public static final Meal userMeal = new Meal(USER_MEAL_ID, LocalDateTime.parse("2020-12-19T10:07:04"),
            "Breakfast user", 416);
    public static final Meal secondUserMeal = new Meal(SECOND_USER_MEAL_ID, LocalDateTime.parse("2021-12-19T10:07:04"),
            "Second breakfast user", 1231);
    public static final Meal adminMeal = new Meal(ADMIN_MEAL_ID, LocalDateTime.parse("2020-11-12T05:31:33"),
            "Afternoon snack admin", 1084);

    public static Meal getNew() {
        return new Meal(null, LocalDateTime.of(2020, 2, 2, 2, 2), "New meal", 1234);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(userMeal);
        updated.setDateTime(LocalDateTime.of(2021, 1, 1, 1, 1));
        updated.setDescription("UpdatedDescription");
        updated.setCalories(4321);
        return updated;
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... meals) {
        assertMatch(actual, Arrays.asList(meals));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveFieldByFieldElementComparator().isEqualTo(expected);
    }

}
