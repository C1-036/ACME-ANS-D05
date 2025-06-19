/*
 * TechnicianDashboardShowService.java
 *
 * Copyright (C) 2012-2025 Rafael Corchuelo.
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.features.technician.dashboard;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.technicians.MaintenanceRecord;
import acme.forms.TechnicianDashboard;
import acme.realms.Technician;

@GuiService
public class TechnicianDashboardShowService extends AbstractGuiService<Technician, TechnicianDashboard> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianDashboardRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		TechnicianDashboard dashboard;
		int technicianId;

		technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Date currentMoment = MomentHelper.getCurrentMoment();
		Date lastYearDate = MomentHelper.deltaFromMoment(MomentHelper.getCurrentMoment(), -1, ChronoUnit.YEARS);

		Integer numberOfMaintenanceRecordsPending;
		Integer numberOfMaintenanceRecordsInProgress;
		Integer numberOfMaintenanceRecordsCompleted;

		MaintenanceRecord nearestMaintenanceRecordByInspectionDueDate;
		List<Aircraft> topFiveAircraftsWithMostTasks;

		List<Object[]> maintenanceRecordEstimatedCostLastYearStats;

		Double averageTaskDuration;
		Double minimumTaskDuration;
		Double maximumTaskDuration;
		Double deviationTaskDuration;

		numberOfMaintenanceRecordsPending = this.repository.numberOfMaintenanceRecordsPending(technicianId);
		numberOfMaintenanceRecordsInProgress = this.repository.numberOfMaintenanceRecordsInProgress(technicianId);
		numberOfMaintenanceRecordsCompleted = this.repository.numberOfMaintenanceRecordsCompleted(technicianId);

		nearestMaintenanceRecordByInspectionDueDate = this.repository.nearestMaintenanceRecordByInspectionDueDate(technicianId, currentMoment, PageRequest.of(0, 1));
		topFiveAircraftsWithMostTasks = this.repository.topFiveAircraftsWithMostTasks(technicianId, PageRequest.of(0, 5));

		maintenanceRecordEstimatedCostLastYearStats = this.repository.maintenanceRecordEstimatedCostLastYearStats(technicianId, lastYearDate);

		Map<String, Integer> currencyCountMap = new HashMap<>();
		List<Object[]> rawCounts = this.repository.countMaintenanceRecordsByCurrency(technicianId, lastYearDate);
		for (Object[] entry : rawCounts) {
			String currency = (String) entry[0];
			Long count = (Long) entry[1];
			currencyCountMap.put(currency, count.intValue());
		}

		for (Object[] stat : maintenanceRecordEstimatedCostLastYearStats) {
			String currency = (String) stat[0];
			Integer count = currencyCountMap.get(currency);

			if (count != null && count == 1)
				stat[4] = "N/D";
		}

		averageTaskDuration = this.repository.averageTaskDuration(technicianId);
		minimumTaskDuration = this.repository.minimumTaskDuration(technicianId);
		maximumTaskDuration = this.repository.maximumTaskDuration(technicianId);
		deviationTaskDuration = this.repository.deviationTaskDuration(technicianId);

		dashboard = new TechnicianDashboard();

		dashboard.setNumberOfMaintenanceRecordsPending(numberOfMaintenanceRecordsPending);
		dashboard.setNumberOfMaintenanceRecordsInProgress(numberOfMaintenanceRecordsInProgress);
		dashboard.setNumberOfMaintenanceRecordsCompleted(numberOfMaintenanceRecordsCompleted);

		dashboard.setNearestMaintenanceRecordByInspectionDueDate(nearestMaintenanceRecordByInspectionDueDate);
		dashboard.setTopFiveAircraftsWithMostTasks(topFiveAircraftsWithMostTasks);

		dashboard.setMaintenanceRecordEstimatedCostLastYearStats(maintenanceRecordEstimatedCostLastYearStats);

		dashboard.setAverageTaskDuration(averageTaskDuration);
		dashboard.setMinimumTaskDuration(minimumTaskDuration);
		dashboard.setMaximumTaskDuration(maximumTaskDuration);
		dashboard.setDeviationTaskDuration(deviationTaskDuration);

		super.getBuffer().addData(dashboard);
	}

	@Override
	public void unbind(final TechnicianDashboard dashboard) {
		Dataset dataset;

		dataset = super.unbindObject(dashboard, //
			"numberOfMaintenanceRecordsPending", "numberOfMaintenanceRecordsInProgress", //
			"numberOfMaintenanceRecordsCompleted", "nearestMaintenanceRecordByInspectionDueDate", // 
			"topFiveAircraftsWithMostTasks", "maintenanceRecordEstimatedCostLastYearStats", "averageTaskDuration", //
			"minimumTaskDuration", "maximumTaskDuration", "deviationTaskDuration");

		super.getResponse().addData(dataset);
	}

}
