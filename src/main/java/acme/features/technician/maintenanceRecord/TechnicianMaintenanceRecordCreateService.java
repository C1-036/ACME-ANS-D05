
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
public class TechnicianMaintenanceRecordCreateService extends AbstractGuiService<Technician, MaintenanceRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;

	// AbstractGuiService interface -------------------------------------------

	//	@Override
	//	public void authorise() {
	//
	//		boolean status = true;
	//
	//		if (super.getRequest().hasData("id") && super.getRequest().getData("aircraft", int.class) != 0) {
	//			int aircraftId = super.getRequest().getData("aircraft", int.class);
	//			Aircraft a = this.repository.findAircraftById(aircraftId);
	//			status = a != null;
	//			@SuppressWarnings("unused")
	//			MaintenanceStatus maintenanceRecordStatus = super.getRequest().getData("status", MaintenanceStatus.class);
	//		}
	//
	//		super.getResponse().setAuthorised(status);
	//	}


	@Override
	public void authorise() {
		boolean status = true;

		if (super.getRequest().getMethod().equalsIgnoreCase("POST"))
			if (super.getRequest().hasData("aircraft", int.class)) {
				int aircraftId = super.getRequest().getData("aircraft", int.class);
				if (aircraftId > 0) {
					Aircraft aircraft = this.repository.findAircraftById(aircraftId);
					status = aircraft != null;
				}
			}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		MaintenanceRecord maintenanceRecord;
		Technician technician;

		technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();

		maintenanceRecord = new MaintenanceRecord();
		maintenanceRecord.setMoment(MomentHelper.getCurrentMoment());
		maintenanceRecord.setDraftMode(true);
		maintenanceRecord.setTechnician(technician);

		super.getBuffer().addData(maintenanceRecord);
	}

	@Override
	public void bind(final MaintenanceRecord maintenanceRecord) {

		super.bindObject(maintenanceRecord, "status", "inspectionDueDate", "estimatedCost", "notes");
		maintenanceRecord.setAircraft(super.getRequest().getData("aircraft", Aircraft.class));

	}

	@Override
	public void validate(final MaintenanceRecord maintenanceRecord) {
		if (!this.getBuffer().getErrors().hasErrors("inspectionDueDate") && maintenanceRecord.getInspectionDueDate() != null)
			super.state(maintenanceRecord.getInspectionDueDate().after(maintenanceRecord.getMoment()), "inspectionDueDate", "acme.validation.technician.maintenance-record.moment-before-inspection", maintenanceRecord);

		if (!this.getBuffer().getErrors().hasErrors("status") && maintenanceRecord.getStatus() != null)
			super.state(maintenanceRecord.getStatus() == MaintenanceStatus.PENDING, "status", "acme.validation.technician.maintenance-record.only-pending-on-create", maintenanceRecord);

	}

	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {
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

		dataset = super.unbindObject(maintenanceRecord, "moment", "status", "inspectionDueDate", "estimatedCost", "notes", "draftMode");

		dataset.put("technician", maintenanceRecord.getTechnician().getIdentity().getFullName());
		dataset.put("aircraft", choicesAircrafts.getSelected().getKey());
		dataset.put("aircrafts", choicesAircrafts);
		dataset.put("status", choicesStatus.getSelected().getKey());
		dataset.put("statuses", choicesStatus);

		super.getResponse().addData(dataset);
	}

}
