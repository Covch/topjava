package ru.javawebinar.topjava.controller;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.MealDao;
import ru.javawebinar.topjava.dao.MealDaoImpl;
import ru.javawebinar.topjava.model.Meal;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Locale;

import static org.slf4j.LoggerFactory.getLogger;

public class MealController extends HttpServlet {
    private static final Logger log = getLogger(MealController.class);
    private static final String INSERT_OR_EDIT = "/mealEditor.jsp";
    private static final String LIST_MEAL = "/meals";
    private MealDao mealDao;

    public MealController() {
        super();
        mealDao = new MealDaoImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("GET: " + req.getQueryString());

        String forward;
        String action = req.getParameter("action").toLowerCase(Locale.ROOT);
        long mealId;
        Meal meal;
        switch (action) {
            case "delete":
                forward = LIST_MEAL;
                mealId = Long.parseLong(req.getParameter("mealId"));
                mealDao.deleteMeal(mealId);
                break;
            case "edit":
                forward = INSERT_OR_EDIT;
                mealId = Long.parseLong(req.getParameter("mealId"));
                meal = mealDao.getMealById(mealId);
                req.setAttribute("meal", meal);
                break;
            default:
                forward = INSERT_OR_EDIT;
                meal = new Meal(LocalDateTime.now(), "", 0);
                req.setAttribute("meal", meal);
        }
        req.getRequestDispatcher(forward).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        Meal meal = new Meal(LocalDateTime.parse(req.getParameter("datetime-local")),
                req.getParameter("description"),
                Integer.parseInt(req.getParameter("calories")));
        if ("-1".equals(req.getParameter("mealId"))) {
            mealDao.addMeal(meal);
        } else {
            meal.setId(Long.parseLong(req.getParameter("mealId")));
            mealDao.updateMeal(meal);
        }

        log.debug("POST: " + meal);
        resp.sendRedirect(req.getContextPath() + LIST_MEAL);
    }
}
