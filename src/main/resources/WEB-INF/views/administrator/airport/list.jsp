<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="administrator.airport.list.label.name" path="name" width="15%"/>
	<acme:list-column code="administrator.airport.list.label.iataCode" path="iataCode" width="10%"/>
	<acme:list-column code="administrator.airport.list.label.operationalScope" path="operationalScope" width="15%"/>
	<acme:list-column code="administrator.airport.list.label.city" path="city" width="15%"/>
	<acme:list-column code="administrator.airport.list.label.country" path="country" width="15%"/>
	<acme:list-payload path="payload"/>
</acme:list>
