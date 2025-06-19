
package acme.forms;

import java.util.List;

import acme.client.components.basis.AbstractForm;
import acme.entities.aircraft.Aircraft;
import acme.entities.technicians.MaintenanceRecord;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TechnicianDashboard extends AbstractForm {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	// 1. Number of maintenance records grouped by their status
	Integer						numberOfMaintenanceRecordsPending;
	Integer						numberOfMaintenanceRecordsInProgress;
	Integer						numberOfMaintenanceRecordsCompleted;

	// 2. Maintenance record with the nearest inspection due date (where technician is involved)
	MaintenanceRecord			nearestMaintenanceRecordByInspectionDueDate;

	// 3. Top 5 aircrafts with higher number of tasks in their maintenance records
	List<Aircraft>				topFiveAircraftsWithMostTasks;

	// 4. Stats on estimated cost of maintenance records in the last year
	List<Object[]>				maintenanceRecordEstimatedCostLastYearStats;

	// 5. Stats on estimated duration of tasks technician is involved in
	Double						averageTaskDuration;
	Double						minimumTaskDuration;
	Double						maximumTaskDuration;
	Double						deviationTaskDuration;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
