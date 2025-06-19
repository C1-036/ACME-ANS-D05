<%--
- menu.jsp
-
- Copyright (C) 2012-2025 Rafael Corchuelo.
-
- In keeping with the traditional purpose of furthering education and research, it is
- the policy of the copyright owner to permit non-commercial use and redistribution of
- this software. It has been tested carefully, but it is not guaranteed for any particular
- purposes.  The copyright owner does not offer any warranties or representations, nor do
- they accept any liabilities with respect to them.
--%>

<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:menu-bar>
	<acme:menu-left>
		<acme:menu-option code="master.menu.anonymous" access="isAnonymous()">
			<acme:menu-suboption code="master.menu.anonymous.favourite-link" action="http://www.example.com/"/>
			<acme:menu-suboption code="master.menu.anonymous.favourite-link.Alejandro-Sevillano" action="http://www.youtube.com/"/>
			<acme:menu-suboption code="master.menu.anonymous.favourite-link.Ignacio-Naredo" action="http://www.twitter.com/"/>
			<acme:menu-suboption code="master.menu.anonymous.favourite-link.Adrian-Robles" action="https://www.instagram.com/"/>
      		<acme:menu-suboption code="master.menu.anonymous.favourite-link.FranciscoManuel-Sabido" action="https://open.spotify.com/"/>
      		<acme:menu-suboption code="master.menu.anonymous.favourite-link.David-Escudero" action="https://www.netflix.com/es/"/>
      		<acme:menu-separator/>
      		<acme:menu-suboption code="master.menu.anonymous.all-flights" action="/any/flight/list"/>
		</acme:menu-option>
		
		
		<acme:menu-option code="master.menu.authenticated" access="isAuthenticated()">
			<acme:menu-suboption code="master.menu.anonymous.all-flights" action="/any/flight/list"/>	
		</acme:menu-option>

		<acme:menu-option code="master.menu.administrator" access="hasRealm('Administrator')">
			<acme:menu-suboption code="master.menu.administrator.list-airlines" action="/administrator/airline/list"/>
			<acme:menu-suboption code="master.menu.administrator.list-user-accounts" action="/administrator/user-account/list"/>
			<acme:menu-suboption code="master.menu.administrator.aircrafts" action="/administrator/aircraft/list"/>
			<acme:menu-suboption code="master.menu.administrator.airports" action="/administrator/airport/list"/>
			<acme:menu-suboption code="master.menu.administrator.list-maintenance-records" action="/administrator/maintenance-record/list"/>
			<acme:menu-suboption code="master.menu.administrator.show-dashboard" action="/administrator/administrator-dashboard/show"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.populate-db-initial" action="/administrator/system/populate-initial"/>
			<acme:menu-suboption code="master.menu.administrator.populate-db-sample" action="/administrator/system/populate-sample"/>			
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.shut-system-down" action="/administrator/system/shut-down"/>
			
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.technician" access="hasRealm('Technician')">
			<acme:menu-suboption code="master.menu.technician.list-my-maintenance-records" action="/technician/maintenance-record/list?mine=true" />			
			<acme:menu-suboption code="master.menu.technician.list-my-tasks" action="/technician/task/list?mine=true" />
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.technician.list-maintenance-record-catalogue" action="/technician/maintenance-record/list" />
			<acme:menu-suboption code="master.menu.technician.list-task-catalogue" action="/technician/task/list" />
			
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.technician.show-dashboard" action="/technician/technician-dashboard/show" />
			
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.provider" access="hasRealm('Provider')">
			<acme:menu-suboption code="master.menu.provider.favourite-link" action="http://www.example.com/"/>
		</acme:menu-option>

		<acme:menu-option code="master.menu.consumer" access="hasRealm('Consumer')">
			<acme:menu-suboption code="master.menu.consumer.favourite-link" action="http://www.example.com/"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.airline-manager" access="hasRealm('AirlineManager')">
			<acme:menu-suboption code="master.menu.airline-manager.my-flights" action="/airline-manager/flight/list"/>
		</acme:menu-option>

		<acme:menu-option code="master.menu.assistance-agent" access="hasRealm('AssistanceAgent')">
			<acme:menu-suboption code="master.menu.assistance-agent.my-completed-claim-list" action="/assistance-agent/claim/listCompleted"/>
			<acme:menu-suboption code="master.menu.assistance-agent.my-undergoing-claim-list" action="/assistance-agent/claim/listUndergoing"/>			
			<acme:menu-suboption code="master.menu.assistance-agent.my-tracking-logs" action="/assistance-agent/tracking/list"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.customer" access="hasRealm('Customer')">
			<acme:menu-suboption code="master.menu.customer.list.my-bookings" action="/customer/booking/list"/>
			<acme:menu-suboption code="master.menu.customer.list.my-passengers" action="/customer/passenger/list"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.flight-crew-members" access="hasRealm('FlightCrewMembers')">
			<acme:menu-suboption code="master.menu.flight-crew-members.list-flight-assignments-planned" action="/flight-crew-members/flight-assignment/list-planned" />
			<acme:menu-suboption code="master.menu.flight-crew-members.list-flight-assignments-completed" action="/flight-crew-members/flight-assignment/list-completed" />
		</acme:menu-option>
	</acme:menu-left> 

	<acme:menu-right>		
		<acme:menu-option code="master.menu.user-account" access="isAuthenticated()">
			<acme:menu-suboption code="master.menu.user-account.general-profile" action="/authenticated/user-account/update"/>
			<acme:menu-suboption code="master.menu.user-account.become-provider" action="/authenticated/provider/create" access="!hasRealm('Provider')"/>
			<acme:menu-suboption code="master.menu.user-account.provider-profile" action="/authenticated/provider/update" access="hasRealm('Provider')"/>
			<acme:menu-suboption code="master.menu.user-account.become-consumer" action="/authenticated/consumer/create" access="!hasRealm('Consumer')"/>
			<acme:menu-suboption code="master.menu.user-account.consumer-profile" action="/authenticated/consumer/update" access="hasRealm('Consumer')"/>
			<acme:menu-suboption code="master.menu.user-account.become-manager" action="/authenticated/airline-manager/create" access="!hasRealm('AirlineManager')"/>
			<acme:menu-suboption code="master.menu.user-account.update-manager" action="/authenticated/airline-manager/update" access="hasRealm('AirlineManager')"/>
			<acme:menu-suboption code="master.menu.user-account.become-technician" action="/authenticated/technician/create" access="!hasRealm('Technician')" />
			<acme:menu-suboption code="master.menu.user-account.technician-profile" action="/authenticated/technician/update" access="hasRealm('Technician')" />
		</acme:menu-option>
	</acme:menu-right>
</acme:menu-bar>
