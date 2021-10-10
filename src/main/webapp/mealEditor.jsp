<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="action" scope="request" type="java.lang.String"/>
<c:set var="title" scope="session" value="${'insert'.equals(action) ? 'Add' : 'edit'.equals(action) ? 'Edit' : ''}"/>

<html lang="ru">
<head>
    <title>${title} meal</title>
</head>
<body>
<h3>${title} meal</h3>
<jsp:useBean id="meal" scope="request" type="ru.javawebinar.topjava.model.Meal"/>
<form method="POST" action='meals' name="frmAddOrEditMeal">
    <input type="hidden" readonly="readonly" name="mealId"
           value="<c:out value="${meal.id}" />"/>
    <br/>
    DateTime :
    <label>
        <input
                type="datetime-local" name="datetime-local"
                value="<c:out value="${meal.dateTime}" />">
    </label>
    <br/>
    Description :
    <label>
        <input
                type="text" name="description"
                value="<c:out value="${meal.description}" />"/>
    </label>
    <br/>
    Calories :
    <label>
        <input
                type="number" name="calories"
                value="<c:out value="${meal.calories}" />"/>
    </label>
    <br/>
    <input
            type="submit" value="Submit"/>
</form>
<button onclick="location.href='meals'" type="button">
    Go Back
</button>
</body>
</html>
