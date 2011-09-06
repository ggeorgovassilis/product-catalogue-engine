<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<ul class="vmenu">
	<li><a href="/product-catalogue-engine/welcome" shape="rect">Home</a></li>
	<li><a href="/product-catalogue-engine/categories" shape="rect">Categories</a>
		<c:if test="${fn:length(categories) > 0}">
			<ul class="submenu">
			<c:forEach items="${categories}" var="category" varStatus="rowCounter">
				<li><a href="/product-catalogue-engine/category/${category}">${category}</a></li>
			</c:forEach>
			</ul>
		</c:if>
	</li>
	<li><a href="/product-catalogue-engine/entities" shape="rect">Products</a></li>
</ul>
