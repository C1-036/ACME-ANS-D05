
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.technicians.MaintenanceRecord;
import acme.entities.technicians.MaintenanceStatus;
import acme.realms.Technician;

@GuiService
public class TechnicianMaintenanceRecordUpdateService extends AbstractGuiService<Technician, MaintenanceRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status = false;
		boolean statusAircraft = true;
		int maintenanceRecordId;
		MaintenanceRecord maintenanceRecord;
		boolean isDraft;
		boolean isTechnician;
		int aircraftId;
		Aircraft aircraft;

		if (super.getRequest().hasData("id", Integer.class)) {
			maintenanceRecordId = super.getRequest().getData("id", Integer.class);
			maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);

			if (maintenanceRecord != null) {
				Technician technician = maintenanceRecord.getTechnician();
				isDraft = maintenanceRecord.isDraftMode();
				isTechnician = super.getRequest().getPrincipal().hasRealm(technician);

				status = isDraft && isTechnician;
			}
		}

		if (super.getRequest().hasData("aircraft", Integer.class)) {
			aircraftId = super.getRequest().getData("aircraft", Integer.class);
			aircraft = this.repository.findAircraftById(aircraftId);

			if (aircraft == null && aircraftId != 0)
				statusAircraft = false;
		}

		super.getResponse().setAuthorised(status && statusAircraft);
	}

	@Override
	public void load() {
		MaintenanceRecord maintenanceRecord;
		int maintenanceRecordId;

		maintenanceRecordId = super.getRequest().getData("id", Integer.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);

		super.getBuffer().addData(maintenanceRecord);
	}

	@Override
	public void bind(final MaintenanceRecord maintenanceRecord) {
		int aircraftId = super.getRequest().getData("aircraft", Integer.class);
		Aircraft aircraft = this.repository.findAircraftById(aircraftId);

		super.bindObject(maintenanceRecord, "moment", "status", "inspectionDueDate", "estimatedCost", "notes");
		maintenanceRecord.setAircraft(aircraft);
	}

	@Override
	public void validate(final MaintenanceRecord maintenanceRecord) {
		MaintenanceRecord original = this.repository.findMaintenanceRecordById(maintenanceRecord.getId());

		if (!this.getBuffer().getErrors().hasErrors("status") && original != null) {
			boolean validTransition = original.getStatus() == MaintenanceStatus.PENDING && maintenanceRecord.getStatus() == MaintenanceStatus.IN_PROGRESS
				|| original.getStatus() == MaintenanceStatus.IN_PROGRESS && maintenanceRecord.getStatus() == MaintenanceStatus.COMPLETED || original.getStatus() == maintenanceRecord.getStatus(); // permitir dejar igual

			super.state(validTransition, "status", "acme.validation.technician.maintenance-record.invalid-status-transition", maintenanceRecord);

			if (original.getStatus() == MaintenanceStatus.PENDING && maintenanceRecord.getStatus() == MaintenanceStatus.IN_PROGRESS) {
				int taskCount = this.repository.countTasksByMaintenanceRecordId(maintenanceRecord.getId());
				super.state(taskCount > 0, "status", "acme.validation.technician.maintenance-record.zero-tasks", maintenanceRecord);
			}

			if (original.getStatus() == MaintenanceStatus.IN_PROGRESS && maintenanceRecord.getStatus() == MaintenanceStatus.COMPLETED) {
				boolean allPublished = this.repository.areAllTasksPublished(maintenanceRecord.getId());
				super.state(allPublished, "status", "acme.validation.technician.maintenance-record.unpublished-tasks", maintenanceRecord);
			}
		}
	}

	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {
		maintenanceRecord.setMoment(MomentHelper.getCurrentMoment());

		if (maintenanceRecord.getStatus() == MaintenanceStatus.COMPLETED)
			maintenanceRecord.setDraftMode(false);

		this.repository.save(maintenanceRecord);
	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		Collection<Aircraft> aircrafts;
		SelectChoices choicesAircrafts;
		SelectChoices choicesStatus;
		Dataset dataset;

		aircrafts = this.repository.findAircrafts();

		choicesStatus = SelectChoices.from(MaintenanceStatus.class, maintenanceRecord.getStatus());
		choicesAircrafts = SelectChoices.from(aircrafts, "registrationNumber", maintenanceRecord.getAircraft());

		dataset = super.unbindObject(maintenanceRecord, "moment", "inspectionDueDate", "estimatedCost", "notes", "draftMode");
		dataset.put("technician", maintenanceRecord.getTechnician().getIdentity().getFullName());
		dataset.put("aircraft", choicesAircrafts.getSelected().getKey());
		dataset.put("aircrafts", choicesAircrafts);
		dataset.put("status", choicesStatus.getSelected().getKey());
		dataset.put("statuses", choicesStatus);

		super.getResponse().addData(dataset);
	}
}
