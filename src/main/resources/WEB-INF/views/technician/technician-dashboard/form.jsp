<%--
- form.jsp
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
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<h2>
    <acme:print code="technician.dashboard.form.title.general-indicators" />
</h2>

<table class="table table-sm">

    <tr>
        <th scope="row"><acme:print code="technician.dashboard.form.label.number-of-maintenance-records-pending" /></th>
        <td><acme:print value="${numberOfMaintenanceRecordsPending}" /></td>
    </tr>
    
    <tr>
        <th scope="row"><acme:print code="technician.dashboard.form.label.number-of-maintenance-records-in-progress" /></th>
        <td><acme:print value="${numberOfMaintenanceRecordsInProgress}" /></td>
    </tr>
    
    <tr>
        <th scope="row"><acme:print code="technician.dashboard.form.label.number-of-maintenance-records-completed" /></th>
        <td><acme:print value="${numberOfMaintenanceRecordsCompleted}" /></td>
    </tr>
    
    <tr>
        <th scope="row"><acme:print code="technician.dashboard.form.label.nearest-maintenance-record-by-inspection-due-date" /></th>
        <td>
            <jstl:if test="${not empty nearestMaintenanceRecordByInspectionDueDate}">
                <acme:print value="${nearestMaintenanceRecordByInspectionDueDate.aircraft.registrationNumber}" />
            </jstl:if>
        </td>
    </tr>
    
 <tr>
	<th scope="row"><acme:print
			code="technician.dashboard.form.label.top-five-aircrafts-with-most-tasks" />
	</th>
	<td>
		<jstl:forEach var="aircraft" items="${topFiveAircraftsWithMostTasks}">
			<acme:print value="${aircraft.registrationNumber}" /><br />
		</jstl:forEach>
	</td>
</tr>

	<tr>
		<th scope="row"><acme:print
				code="technician.dashboard.form.label.maintenance-record-stats-estimated-cost-per-currency-last-year" />
		</th>
		<td><jstl:forEach var="stat"
				items="${maintenanceRecordEstimatedCostLastYearStats}">
				<acme:print code="technician.dashboard.form.label.currency" /> ${stat[0]}<br />
				<acme:print code="technician.dashboard.form.label.min" />
				<acme:print value="${stat[2]}" />
				<br />
				<acme:print code="technician.dashboard.form.label.max" />
				<acme:print value="${stat[3]}" />
				<br />
				<acme:print code="technician.dashboard.form.label.avg" />
				<acme:print value="${stat[1]}" />
				<br />
				<acme:print code="technician.dashboard.form.label.stddev" />
				<acme:print value="${stat[4]}" />
				<br />
			</jstl:forEach></td>
	</tr>


	<tr>
        <th scope="row"><acme:print code="technician.dashboard.form.label.average-task-duration" /></th>
        <td><acme:print value="${averageTaskDuration}" /></td>
    </tr>

    <tr>
        <th scope="row"><acme:print code="technician.dashboard.form.label.minimum-task-duration" /></th>
        <td><acme:print value="${minimumTaskDuration}" /></td>
    </tr>

    <tr>
        <th scope="row"><acme:print code="technician.dashboard.form.label.maximum-task-duration" /></th>
        <td><acme:print value="${maximumTaskDuration}" /></td>
    </tr>

    <tr>
        <th scope="row"><acme:print code="technician.dashboard.form.label.deviation-task-duration" /></th>
        <td><acme:print value="${deviationTaskDuration}" /></td>
    </tr>

</table>

<acme:return />
