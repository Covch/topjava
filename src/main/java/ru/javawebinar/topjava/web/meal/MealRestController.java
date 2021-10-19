package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDateTime;
import java.util.Collection;

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

    public Collection<MealTo> getAll() {
        log.info("getAll by userId={}", authUserId());
        return MealsUtil.getTos(service.getAll(authUserId()), authUserCaloriesPerDay());
    }

    public Collection<MealTo> getAll(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        log.info("getAll by userId={} from {} to {}", authUserId(), startDateTime, endDateTime);
        return MealsUtil.getFilteredTos(service.getAll(authUserId(), startDateTime, endDateTime)
                , authUserCaloriesPerDay()
                , startDateTime.toLocalTime()
                , endDateTime.toLocalTime());
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