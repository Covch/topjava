<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="https://topjava.com/customFunctions" prefix="f" %>
<jsp:useBean id="mealToList" scope="request" type="java.util.List"/>
<html lang="ru">
<head>
    <title>Meals</title>
    <style media="screen">
        table {
            font-family: "Lucida Sans Unicode", "Lucida Grande", Sans-Serif, serif;
            font-size: 14px;
            border-radius: 10px;
            border-spacing: 0;
            text-align: center;
        }

        th {
            background: #BCEBDD;
            color: white;
            text-shadow: 0 1px 1px #2D2020;
            padding: 10px 20px;
        }

        th, td {
            border-style: solid;
            border-width: 0 1px 1px 0;
            border-color: white;
        }

        th:first-child, td:first-child {
            text-align: left;
        }

        th:first-child {
            border-top-left-radius: 10px;
        }

        th:last-child {
            border-top-right-radius: 10px;
            border-right: none;
        }

        td {
            padding: 10px 20px;
        }

        tr:last-child td:first-child {
            border-radius: 0 0 0 10px;
        }

        tr:last-child td:last-child {
            border-radius: 0 0 10px 0;
        }

        tr td:last-child {
            border-right: none;
        }
    </style>
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

    <c:forEach var="mealTo" items="${mealToList}">
        <tr style="${mealTo.excess ? 'background-color: #ff648b' : 'background-color: #F8E391'}">
            <td>${f:formatLocalDateTime(mealTo.dateTime, 'yyyy-MM-dd HH:mm')}</td>
            <td>${mealTo.description}</td>
            <td>${mealTo.calories}</td>
            <td><a href="index.html">Update</a></td>
            <td><a href="index.html">Delete</a></td>
        </tr>
    </c:forEach>
    <tr style="background-color: #F8E391">
        <td colspan="5"><a href="index.html">Add new meal...</a></td>
    </tr>
</table>
</body>
</html>
