package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.MealDao;
import ru.javawebinar.topjava.dao.InMemoryMealDao;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final int CALORIES_PER_DAY = 2000;
    private static final String INSERT_OR_EDIT = "/mealEditor.jsp";
    private static final String LIST_MEAL = "/meals";
    private static final Logger log = getLogger(MealServlet.class);
    private MealDao mealDao;

    @Override
    public void init() {
        mealDao = new InMemoryMealDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("GET: {}", req.getQueryString());
        if (req.getParameter("action") != null) {
            String action = req.getParameter("action").toLowerCase(Locale.ROOT);
            switch (action) {
                case "delete":
                    long mealId = getMealId(req);
                    mealDao.delete(mealId);
                    resp.sendRedirect(req.getContextPath() + LIST_MEAL);
                    log.debug("GET: {} {}", action, mealId);
                    return;
                case "edit":
                    mealId = getMealId(req);
                    Meal meal = mealDao.getById(mealId);
                    setAttrAndForward(req, resp, action, meal);
                    return;
                case "insert":
                    meal = new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 0);
                    setAttrAndForward(req, resp, action, meal);
                    return;
            }
        }
        req.setAttribute("mealToList", MealsUtil.filteredByStreams(mealDao.getAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY));
        req.getRequestDispatcher("/meals.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        Meal meal = new Meal(LocalDateTime.parse(req.getParameter("datetime-local")),
                req.getParameter("description"),
                Integer.parseInt(req.getParameter("calories")));
        if (req.getParameter("mealId").isEmpty()) {
            log.debug("POST: insert {}", meal);
            mealDao.add(meal);
        } else {
            meal.setId(getMealId(req));
            log.debug("POST: edit {}", meal);
            mealDao.update(meal);
        }
        resp.sendRedirect(req.getContextPath() + LIST_MEAL);
    }

    private long getMealId(HttpServletRequest req) {
        return Long.parseLong(req.getParameter("mealId"));
    }

    private void setAttrAndForward(HttpServletRequest req, HttpServletResponse resp, String action, Meal meal) throws ServletException, IOException {
        req.setAttribute("meal", meal);
        log.debug("GET: {} {}", action, meal);
        req.getRequestDispatcher(INSERT_OR_EDIT).forward(req, resp);
    }
}
