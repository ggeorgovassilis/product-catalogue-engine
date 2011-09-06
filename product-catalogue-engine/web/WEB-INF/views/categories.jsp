<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<div class="container">
	<h1>Categories</h1>
	<ul>
	<c:forEach items="${categories}" var="category" varStatus="rowCounter">
		<li><a href="/product-catalogue-engine/category/${category}">${category}</a></li>
	</c:forEach>
	</ul>
</div>

