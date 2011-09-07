<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="pce" %>


<div class="container">
	<h1>Categories</h1>
	<ul>
	<c:forEach items="${categories}" var="category" varStatus="rowCounter">
		<li><pce:categorylink value="${category}"><c:out value="${category}"/></pce:categorylink></li>
	</c:forEach>
	</ul>
</div>

