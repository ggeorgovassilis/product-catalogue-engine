<%@ attribute name="value" required="true" type="org.pce.model.Entity" rtexprvalue="true"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ct" uri="/WEB-INF/tlds/CallTag.tld"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>  

<ct:call object="${entity}" method="getAttribute" debug="true" name="description" return="description"/>
<ct:call object="${entity}" method="getAttribute" debug="true" name="name" return="name"/>
<h1>Entity ${entity.ID}  <b>${name}</b> ${description}</h1>
<h3 class="entity">Attributes of this entity:</h3>
<div class="prodimg">
	<img alt="" src="/product-catalogue-engine/images/${productId}"> 
</div>
<table class="attributes" style="float:left;">
	<c:forEach items="${entity}" var="e">
		<c:if test="${e.value ne null and e.value ne ''}">
			<tr valign="top" class="attributeRow ${e.key}">
			<td align="right" class="attributeKey">${e.key}</td>
			<td class="attributeValue ${e.key}">
				<spring:message text="${e.value}" arguments="${e.value}" code="attribute.${e.key}"/>
			</td>
			</tr>
		</c:if>
	</c:forEach>
</table>