<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="meal" scope="request" type="ru.javawebinar.topjava.model.Meal"/>
<c:set var="title" scope="session" value="${meal.id == null ? 'Add' : 'Edit'}"/>

<html lang="ru">
<head>
    <title>${title} meal</title>
    <link rel="stylesheet" type="text/css" href="css/default.css" media="screen"/>
</head>
<body>
<h3>${title} meal</h3>
<div>
    <form method="POST" action='meals' name="frmAddOrEditMeal">
        <input type="hidden" readonly="readonly" name="mealId"
               value="${meal.id}"/>
        <p>
            <label for="datetime-local">DateTime : </label>
            <input type="datetime-local" id="datetime-local"
                   name="datetime-local" value="${meal.dateTime}">
        </p>
        <p>
        <label for="description">Description : </label>
        <input type="text" id="description"
               name="description" value="${meal.description}"/>
        </p>
        <p>
        <label for="calories">Calories : </label>
        <input type="number" id="calories"
               name="calories" value="${meal.calories}"/>
        </p>
        <input type="submit" value="Submit"/>
    </form>
    <button onclick="location.href='meals'" type="button">
        Go Back
    </button>
</div>
</body>
</html>
