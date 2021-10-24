package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void get() {
        Meal meal = service.get(USER_MEAL_ID, USER_ID);
        assertMatch(meal, userMeal);
    }

    @Test
    public void getOtherUser() {
        assertThrows(NotFoundException.class, () -> service.get(USER_MEAL_ID, ADMIN_ID));
    }

    @Test
    public void getNotFoundMeal() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND, USER_ID));
    }

    @Test
    public void getNotFoundUser() {
        assertThrows(NotFoundException.class, () -> service.get(USER_MEAL_ID, NOT_FOUND));
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND, NOT_FOUND));
    }

    @Test
    public void getForeign() {
        assertThrows(NotFoundException.class, () -> service.get(USER_MEAL_ID, ADMIN_ID));
    }

    @Test
    public void delete() {
        service.delete(ADMIN_MEAL_ID, ADMIN_ID);
        assertThrows(NotFoundException.class, () -> service.get(ADMIN_MEAL_ID, ADMIN_ID));
    }

    @Test
    public void deleteNotFoundMeal() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND, USER_ID));
    }

    @Test
    public void deleteNotFoundUser() {
        assertThrows(NotFoundException.class, () -> service.delete(USER_MEAL_ID, NOT_FOUND));
    }

    @Test
    public void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND, NOT_FOUND));
    }

    @Test
    public void deleteForeign() {
        assertThrows(NotFoundException.class, () -> service.delete(USER_MEAL_ID, ADMIN_ID));
    }

    @Test
    public void getBetweenInclusive() {
        List<Meal> before2021 = service.getBetweenInclusive(null, LocalDate.of(2021, 1, 1), USER_ID);
        assertMatch(before2021, userMeal);
    }

    @Test
    public void getAll() {
        List<Meal> all = service.getAll(USER_ID);
        assertMatch(all, secondUserMeal, userMeal);
    }

    @Test
    public void update() {
        Meal updated = getUpdated();
        service.update(updated, USER_ID);
        assertMatch(service.get(USER_MEAL_ID, USER_ID), getUpdated());
    }

    @Test
    public void updateNotFoundMeal() {
        Meal notFoundMeal = getUpdated();
        notFoundMeal.setId(NOT_FOUND);
        assertThrows(NotFoundException.class, () -> service.update(notFoundMeal, USER_MEAL_ID));
    }

    @Test
    public void updateNotFoundUser() {
        Meal updated = getUpdated();
        assertThrows(NotFoundException.class, () -> service.update(updated, NOT_FOUND));
    }

    @Test
    public void updateNotFound() {
        Meal notFoundMeal = getUpdated();
        notFoundMeal.setId(NOT_FOUND);
        assertThrows(NotFoundException.class, () -> service.update(notFoundMeal, NOT_FOUND));
    }

    @Test
    public void updateForeign() {
        assertThrows(NotFoundException.class, () -> service.update(getUpdated(), ADMIN_ID));
    }

    @Test
    public void create() {
        Meal created = service.create(getNew(), USER_ID);
        Integer newId = created.getId();
        Meal newMeal = getNew();
        newMeal.setId(newId);
        assertMatch(created, newMeal);
        assertMatch(service.get(newId, USER_ID), newMeal);
    }

    @Test
    public void createNotFound() {
        assertThrows(DataIntegrityViolationException.class, () -> service.create(getNew(), NOT_FOUND));
    }

    @Test
    public void createDuplicateDateTime() {
        assertThrows(DuplicateKeyException.class, () -> service.create(new Meal(null, LocalDateTime.parse("2020-12-19T10:07:04"),
                "Some Meal with not unique DateTime", 416), USER_ID));
    }
}