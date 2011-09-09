<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="pce" %>
<!-- 
<ul id="sidebarmenu1" class="vmenu">
 -->
<ul id="sidebarmenu1" >
	<li><a href="/product-catalogue-engine/welcome" shape="rect">Home</a></li>
	<li><a href="/product-catalogue-engine/categories" shape="rect">Categories</a>
		<c:if test="${fn:length(categories) > 0}">
			<!-- 
			<ul class="submenu">
			-->
			<ul>
			<c:forEach items="${categories}" var="category" varStatus="rowCounter">
				<li><pce:categorylink value="${category}"><c:out value="${category}"/></pce:categorylink></li>
			</c:forEach>
			</ul>
		</c:if>
	</li>
</ul>
