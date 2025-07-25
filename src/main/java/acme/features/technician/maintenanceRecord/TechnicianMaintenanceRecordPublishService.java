
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.technicians.MaintenanceRecord;
import acme.entities.technicians.MaintenanceStatus;
import acme.entities.technicians.Task;
import acme.realms.Technician;

@GuiService
public class TechnicianMaintenanceRecordPublishService extends AbstractGuiService<Technician, MaintenanceRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;

	// AbstractGuiService interface -------------------------------------------

	//	@Override
	//	public void authorise() {
	//		boolean status = false;
	//		boolean statusAircraft = true;
	//		int maintenanceRecordId;
	//		MaintenanceRecord maintenanceRecord;
	//		boolean isDraft;
	//		boolean isTechnician;
	//		int aircraftId;
	//		Aircraft aircraft;
	//
	//		if (super.getRequest().hasData("id", Integer.class)) {
	//			maintenanceRecordId = super.getRequest().getData("id", Integer.class);
	//			maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);
	//
	//			if (maintenanceRecord != null) {
	//				Technician technician = maintenanceRecord.getTechnician();
	//				isDraft = maintenanceRecord.isDraftMode();
	//				isTechnician = super.getRequest().getPrincipal().hasRealm(technician);
	//
	//				status = isDraft && isTechnician;
	//			}
	//		}
	//
	//		if (super.getRequest().hasData("aircraft", Integer.class)) {
	//			aircraftId = super.getRequest().getData("aircraft", Integer.class);
	//			aircraft = this.repository.findAircraftById(aircraftId);
	//
	//			if (aircraft == null && aircraftId != 0)
	//				statusAircraft = false;
	//		}
	//
	//		super.getResponse().setAuthorised(status && statusAircraft);
	//	}

	//		@Override
	//		public void authorise() {
	//			boolean status = false;
	//			boolean statusAircraft = true;
	//			MaintenanceRecord maintenanceRecord;
	//			Aircraft aircraft;
	//	
	//			if (super.getRequest().hasData("id", int.class)) {
	//				int maintenanceRecordId = super.getRequest().getData("id", int.class);
	//				maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);
	//	
	//				if (maintenanceRecord != null) {
	//					Technician technician = maintenanceRecord.getTechnician();
	//					boolean isDraft = maintenanceRecord.isDraftMode();
	//					boolean isTechnician = super.getRequest().getPrincipal().hasRealm(technician);
	//					status = isDraft && isTechnician;
	//				}
	//			}
	//	
	//			if (super.getRequest().hasData("aircraft", int.class)) {
	//				int aircraftId = super.getRequest().getData("aircraft", int.class);
	//				if (aircraftId != 0) {
	//					aircraft = this.repository.findAircraftById(aircraftId);
	//					statusAircraft = aircraft != null;
	//				}
	//			}
	//	
	//			super.getResponse().setAuthorised(status && statusAircraft);
	//		}


	@Override
	public void authorise() {
		boolean status = false;
		boolean aircraftValid = true;
		int id;
		MaintenanceRecord maintenanceRecord;
		Aircraft aircraft;

		if (super.getRequest().hasData("id", int.class)) {
			id = super.getRequest().getData("id", int.class);
			maintenanceRecord = this.repository.findMaintenanceRecordById(id);

			if (maintenanceRecord != null) {
				boolean isOwner = super.getRequest().getPrincipal().hasRealm(maintenanceRecord.getTechnician());
				status = isOwner;
			}
		}

		if (super.getRequest().hasData("aircraft", int.class)) {
			int aircraftId = super.getRequest().getData("aircraft", int.class);
			if (aircraftId != 0) {
				aircraft = this.repository.findAircraftById(aircraftId);
				aircraftValid = aircraft != null;
			}
		}

		super.getResponse().setAuthorised(status && aircraftValid);
	}

	@Override
	public void load() {
		MaintenanceRecord maintenanceRecord;
		int maintenanceRecordId;

		maintenanceRecordId = super.getRequest().getData("id", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);

		super.getBuffer().addData(maintenanceRecord);
	}

	@Override
	public void bind(final MaintenanceRecord maintenanceRecord) {

		int aircraftId;
		Aircraft aircraft;

		aircraftId = super.getRequest().getData("aircraft", int.class);
		aircraft = this.repository.findAircraftById(aircraftId);

		super.bindObject(maintenanceRecord, "moment", "status", "inspectionDueDate", "estimatedCost", "notes");
		maintenanceRecord.setAircraft(aircraft);
	}

	@Override
	public void validate(final MaintenanceRecord maintenanceRecord) {

		Collection<Task> tasks = this.repository.findTasksByMaintenanceRecordId(maintenanceRecord.getId());

		//super.state(!tasks.isEmpty(), "*", "technician.maintenance-record.form.error.zero-tasks");

		boolean hasUnpublishedTask = tasks.stream().anyMatch(Task::isDraftMode);
		super.state(!hasUnpublishedTask, "*", "technician.maintenance-record.form.error.not-all-tasks-published");
		if (maintenanceRecord.getStatus() != null)
			super.state(maintenanceRecord.getStatus().equals(MaintenanceStatus.COMPLETED), "status", "technician.maintenance-record.form.error.not-completed-status");

	}

	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {

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
