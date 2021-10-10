package ru.javawebinar.topjava.controller;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.MealDao;
import ru.javawebinar.topjava.dao.MealDaoImplByMemory;
import ru.javawebinar.topjava.model.Meal;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import static org.slf4j.LoggerFactory.getLogger;

public class MealController extends HttpServlet {
    private static final Logger log = getLogger(MealController.class);
    private static final String INSERT_OR_EDIT = "/mealEditor.jsp";
    private static final String LIST_MEAL = "/meals";
    private MealDao mealDao;

    public MealController() {
        super();
        mealDao = new MealDaoImplByMemory();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("GET: " + req.getQueryString());

        String action = req.getParameter("action").toLowerCase(Locale.ROOT);
        switch (action) {
            case "delete":
                long mealId = Long.parseLong(req.getParameter("mealId"));
                mealDao.deleteMeal(mealId);
                resp.sendRedirect(req.getContextPath() + LIST_MEAL);
                return;
            case "edit":
                mealId = Long.parseLong(req.getParameter("mealId"));
                Meal meal = mealDao.getMealById(mealId);
                req.setAttribute("action", action);
                req.setAttribute("meal", meal);
                req.getRequestDispatcher(INSERT_OR_EDIT).forward(req, resp);
                return;
            case "insert":
                meal = new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 0);
                req.setAttribute("action", action);
                req.setAttribute("meal", meal);
                req.getRequestDispatcher(INSERT_OR_EDIT).forward(req, resp);
                return;
            default:
                resp.sendRedirect(req.getContextPath() + LIST_MEAL);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        Meal meal = new Meal(LocalDateTime.parse(req.getParameter("datetime-local")),
                req.getParameter("description"),
                Integer.parseInt(req.getParameter("calories")));
        if (req.getParameter("mealId").isEmpty()) {
            mealDao.addMeal(meal);
        } else {
            meal.setId(Long.parseLong(req.getParameter("mealId")));
            mealDao.updateMeal(meal);
        }

        log.debug("POST: " + meal);
        resp.sendRedirect(req.getContextPath() + LIST_MEAL);
    }
}
