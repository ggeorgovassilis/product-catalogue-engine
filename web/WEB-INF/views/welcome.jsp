<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>  


<div class="container">  
	<h1>
		<fmt:message key="welcome.title"/>
	</h1>
	<!-- 
	<p>
		Locale = ${pageContext.response.locale}
	</p>
	<hr>	
	<ul>
		<li> <a href="?locale=en_us">us</a> |  <a href="?locale=el">el</a> </li>
	</ul>
	 -->
	<ul>
		<li><a href="entities">Entity Browser</a></li>
	</ul>
</div>
