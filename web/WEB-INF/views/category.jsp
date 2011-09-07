<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ct" uri="/WEB-INF/tlds/CallTag.tld"%>


<div class="container">
	<h1>Category <c:out value="${category}"/></h1>
	<h3>Entities in this category:</h3>
	<ul>
	<c:forEach items="${entities}" var="entity" varStatus="rowCounter">
		<ct:call object="${entity}" method="getAttribute" debug="true" name="Description" return="description"/>
		<ct:call object="${entity}" method="getAttribute" debug="true" name="name" return="name"/>
		<li><a href="/product-catalogue-engine/entity/${entity.ID}">${entity.ID} - ${description}${name}</a></li>
	</c:forEach>
	</ul>
</div>
