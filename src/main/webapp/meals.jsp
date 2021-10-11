<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="https://topjava.com/customFunctions" %>

<html lang="ru">
<head>
    <title>Meals</title>
    <link rel="stylesheet" type="text/css" href="css/default.css" media="screen" />
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
<table>
    <tr>
        <th>Date</th>
        <th>Description</th>
        <th>Calories</th>
        <th></th>
        <th></th>
    </tr>
    <jsp:useBean id="mealToList" scope="request" type="java.util.List"/>
    <c:forEach var="mealTo" items="${mealToList}">
        <tr class="${mealTo.excess ? 'excess' : 'noExcess'}">
            <td>${f:formatLocalDateTime(mealTo.dateTime)}</td>
            <td>${mealTo.description}</td>
            <td>${mealTo.calories}</td>
            <td><a href="meals?action=edit&mealId=${mealTo.id}">Update</a></td>
            <td><a href="meals?action=delete&mealId=${mealTo.id}">Delete</a></td>
        </tr>
    </c:forEach>
    <tr class="addNew">
        <td colspan="5"><a href="meals?action=insert">Add new meal...</a></td>
    </tr>
</table>
</body>
</html>
