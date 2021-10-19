package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserCaloriesPerDay;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Controller
public class MealRestController {
    private static final Logger log = getLogger(MealRestController.class);

    private MealService service;

    public MealRestController(MealService service) {
        this.service = service;
    }

    public List<MealTo> getAll() {
        log.info("getAll by userId={}", authUserId());
        return MealsUtil.getTos(service.getAll(authUserId()), authUserCaloriesPerDay());
    }

    public List<MealTo> getAll(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        log.info("getAll by userId={} from {} {} to {} {}", authUserId(), startDate, startTime, endDate, endTime);
        startDate = startDate == null ? LocalDate.MIN : startDate;
        endDate = endDate == null ? LocalDate.MAX : endDate;
        startTime = startTime == null ? LocalTime.MIN : startTime;
        endTime = endTime == null ? LocalTime.MAX : endTime;
        return MealsUtil.getFilteredTos(service.getAll(authUserId(), startDate, endDate), authUserCaloriesPerDay()
                , startTime, endTime);
    }

    public Meal get(int id) {
        log.info("get {} by userId={}", id, authUserId());
        return service.get(authUserId(), id);
    }

    public Meal create(Meal meal) {
        log.info("create {} by userId={}", meal, authUserId());
        checkNew(meal);
        return service.create(authUserId(), meal);
    }

    public void delete(int id) {
        log.info("delete {} by userId={}", id, authUserId());
        service.delete(authUserId(), id);
    }

    public void update(Meal meal, int id) {
        log.info("update {} with id={} by userId={}", meal, id, authUserId());
        assureIdConsistent(meal, id);
        service.update(authUserId(), meal);
    }
}