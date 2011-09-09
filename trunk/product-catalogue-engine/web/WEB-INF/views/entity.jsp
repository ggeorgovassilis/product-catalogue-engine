<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="pce" %>

<div class="container">
	<pce:entity value="${entity}"/>
</div>

<p class="catnav">All entities of category <a href="/product-catalogue-engine/category/${category}">${category}</a>