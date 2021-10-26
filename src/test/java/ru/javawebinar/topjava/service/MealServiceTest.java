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
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.assertMatch;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.*;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml",
        "classpath:spring/spring-jdbc-repository.xml"
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
    public void getNotFoundMeal() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND_MEAL, USER_ID));
    }

    @Test
    public void getNotFoundUser() {
        assertThrows(NotFoundException.class, () -> service.get(USER_MEAL_ID, NOT_FOUND_USER));
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND_MEAL, NOT_FOUND_USER));
    }

    @Test
    public void getForeign() {
        assertThrows(NotFoundException.class, () -> service.get(USER_MEAL_ID, ADMIN_ID));
    }

    @Test
    public void delete() {
        service.delete(ADMIN_DELETE_MEAL_ID, ADMIN_ID);
        assertThrows(NotFoundException.class, () -> service.get(ADMIN_DELETE_MEAL_ID, ADMIN_ID));
    }

    @Test
    public void deleteNotFoundMeal() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND_MEAL, USER_ID));
    }

    @Test
    public void deleteNotFoundUser() {
        assertThrows(NotFoundException.class, () -> service.delete(USER_MEAL_ID, NOT_FOUND_USER));
    }

    @Test
    public void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND_MEAL, NOT_FOUND_USER));
    }

    @Test
    public void deleteForeign() {
        assertThrows(NotFoundException.class, () -> service.delete(USER_MEAL_ID, ADMIN_ID));
    }

    @Test
    public void getBetweenInclusive() {
        LocalDate startDate = LocalDate.of(2020, 12, 22);
        LocalDate stopDate = LocalDate.of(2021, 12, 15);
        List<Meal> actualMeals = service.getBetweenInclusive(startDate, stopDate, USER_ID);
        assertMatch(actualMeals, userMealListStartAndStop);
    }

    @Test
    public void getBetweenInclusiveStop() {
        LocalDate stopDate = LocalDate.of(2021, 12, 14);
        List<Meal> actualMeals = service.getBetweenInclusive(null, stopDate, USER_ID);
        assertMatch(actualMeals, userMealListStop);
    }

    @Test
    public void getBetweenInclusiveStart() {
        LocalDate startDate = LocalDate.of(2021, 12, 15);
        List<Meal> actualMeals = service.getBetweenInclusive(startDate, null, USER_ID);
        assertMatch(actualMeals, userMealListStart);
    }

    @Test
    public void getBetweenInclusiveAll() {
        List<Meal> actualMeals = service.getBetweenInclusive(null, null, USER_ID);
        assertMatch(actualMeals, userMealListAll);
    }

    @Test
    public void getBetweenInclusiveNotFound() {
        assertThrows(NotFoundException.class, () -> service.getBetweenInclusive(null, null, NOT_FOUND_USER));
    }

    @Test
    public void getAll() {
        List<Meal> all = service.getAll(USER_ID);
        assertMatch(all, userMealListAll);
    }

    @Test
    public void getAllNotFound() {
        assertThrows(NotFoundException.class, () -> service.getAll(NOT_FOUND_USER));
    }

    @Test
    public void update() {
        Meal updated = getUpdatedMeal();
        service.update(updated, ADMIN_ID);
        assertMatch(service.get(ADMIN_UPDATE_MEAL_ID, ADMIN_ID), getUpdatedMeal());
    }

    @Test
    public void updateNotFoundMeal() {
        Meal notFoundMeal = getUpdatedMeal();
        notFoundMeal.setId(NOT_FOUND_MEAL);
        assertThrows(NotFoundException.class, () -> service.update(notFoundMeal, USER_MEAL_ID));
    }

    @Test
    public void updateNotFoundUser() {
        Meal updated = getUpdatedMeal();
        assertThrows(NotFoundException.class, () -> service.update(updated, NOT_FOUND_USER));
    }

    @Test
    public void updateNotFound() {
        Meal notFoundMeal = getUpdatedMeal();
        notFoundMeal.setId(NOT_FOUND_MEAL);
        assertThrows(NotFoundException.class, () -> service.update(notFoundMeal, NOT_FOUND_USER));
    }

    @Test
    public void updateForeign() {
        assertThrows(NotFoundException.class, () -> service.update(getUpdatedMeal(), USER_ID));
    }

    @Test
    public void updateDuplicateDatetime() {
        Meal updated = getUpdatedMeal();
        updated.setDateTime(adminMealUpdDuplicateDateTime.getDateTime());
        assertThrows(DuplicateKeyException.class, () -> service.update(updated, ADMIN_ID));
    }

    @Test
    public void create() {
        Meal created = service.create(MealTestData.getNewMeal(), ADMIN_ID);
        Integer newId = created.getId();
        Meal newMeal = getNewMeal();
        newMeal.setId(newId);
        assertMatch(created, newMeal);
        assertMatch(service.get(newId, ADMIN_ID), newMeal);
    }

    @Test
    public void createNotFound() {
        assertThrows(DataIntegrityViolationException.class, () -> service.create(getNewMeal(), NOT_FOUND_USER));
    }

    @Test
    public void createDuplicateDateTime() {
        assertThrows(DuplicateKeyException.class, () -> service.create(new Meal(null, userMeal.getDateTime(),
                "duplicate datetime meal", 123), USER_ID));
    }
}