
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
import acme.realms.Technician;

@GuiService
public class TechnicianMaintenanceRecordShowService extends AbstractGuiService<Technician, MaintenanceRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;

	// AbstractGuiService interface -------------------------------------------

	//	@Override
	//	public void authorise() {
	//
	//		boolean status;
	//		int masterId;
	//		MaintenanceRecord maintenanceRecord;
	//
	//		status = super.getRequest().hasData("id", int.class);
	//
	//		if (status) {
	//			masterId = super.getRequest().getData("id", int.class);
	//			maintenanceRecord = this.repository.findMaintenanceRecordById(masterId);
	//			status = maintenanceRecord != null && (super.getRequest().getPrincipal().hasRealm(maintenanceRecord.getTechnician()) || !maintenanceRecord.isDraftMode());
	//		}
	//		super.getResponse().setAuthorised(status);
	//	}

	//	@Override
	//	public void authorise() {
	//		boolean status = false;
	//
	//		if (super.getRequest().hasData("id", int.class)) {
	//			int recordId = super.getRequest().getData("id", int.class);
	//			MaintenanceRecord record = this.repository.findMaintenanceRecordById(recordId);
	//
	//			if (record != null) {
	//				boolean isOwner = super.getRequest().getPrincipal().hasRealm(record.getTechnician());
	//				boolean isPublished = !record.isDraftMode();
	//
	//				status = isOwner || isPublished;
	//			}
	//		}
	//
	//		super.getResponse().setAuthorised(status);
	//	}


	@Override
	public void authorise() {
		boolean status = false;

		if (super.getRequest().hasData("id", int.class)) {
			int recordId = super.getRequest().getData("id", int.class);
			MaintenanceRecord record = this.repository.findMaintenanceRecordById(recordId);

			if (record != null) {
				boolean isOwner = super.getRequest().getPrincipal().hasRealm(record.getTechnician());
				boolean isPublished = !record.isDraftMode();

				// Solo permitir si está publicado o si está en borrador y eres el dueño
				status = isPublished || isOwner && record.isDraftMode();
			}
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		MaintenanceRecord maintenanceRecord;
		int id;

		id = super.getRequest().getData("id", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(id);

		super.getBuffer().addData(maintenanceRecord);
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
