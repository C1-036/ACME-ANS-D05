
package acme.features.technician.involves;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.technicians.Involves;
import acme.entities.technicians.MaintenanceRecord;
import acme.entities.technicians.Task;
import acme.realms.Technician;

@GuiService
public class TechnicianInvolvesCreateService extends AbstractGuiService<Technician, Involves> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianInvolvesRepository repository;

	// AbstractGuiService interface -------------------------------------------

	//	@Override
	//	public void authorise() {
	//
	//		boolean statusTask = true;
	//		boolean status = false;
	//		int taskId;
	//		Task task;
	//		int maintenanceRecordId;
	//		MaintenanceRecord maintenanceRecord;
	//		Technician technician;
	//		Collection<Task> tasks;
	//
	//		technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();
	//		maintenanceRecordId = super.getRequest().getData("maintenanceRecordId", int.class);
	//		maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);
	//
	//		tasks = this.repository.findValidTasksToLink(maintenanceRecord, technician);
	//
	//		if (super.getRequest().hasData("task", int.class)) {
	//			taskId = super.getRequest().getData("task", int.class);
	//			task = this.repository.findTaskById(taskId);
	//
	//			if (!tasks.contains(task) && taskId != 0)
	//				statusTask = false;
	//		}
	//
	//		status = maintenanceRecord != null && maintenanceRecord.isDraftMode() && super.getRequest().getPrincipal().hasRealm(maintenanceRecord.getTechnician());
	//
	//		super.getResponse().setAuthorised(status && statusTask);
	//	}


	@Override
	public void authorise() {
		boolean status = false;
		boolean statusTask = true;
		Task task = null;

		if (super.getRequest().hasData("maintenanceRecordId", int.class)) {
			int maintenanceRecordId = super.getRequest().getData("maintenanceRecordId", int.class);
			MaintenanceRecord maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);

			if (maintenanceRecord != null && maintenanceRecord.isDraftMode() && super.getRequest().getPrincipal().hasRealm(maintenanceRecord.getTechnician())) {

				status = true;

				// Sólo en POST, validar la task si se ha enviado
				if (super.getRequest().getMethod().equalsIgnoreCase("POST"))
					if (super.getRequest().hasData("task", Integer.class)) {
						Integer taskId = super.getRequest().getData("task", Integer.class);
						if (taskId != 0) {
							task = this.repository.findTaskById(taskId);
							Technician technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();
							statusTask = task != null && (!task.isDraftMode() || task.getTechnician().equals(technician));
						}
					}
			}
		}

		super.getResponse().setAuthorised(status && statusTask);
	}

	@Override
	public void load() {
		Involves object;
		Integer maintenanceRecordId;
		MaintenanceRecord maintenanceRecord;

		maintenanceRecordId = super.getRequest().getData("maintenanceRecordId", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);

		object = new Involves();
		object.setMaintenanceRecord(maintenanceRecord);
		super.getBuffer().addData(object);

	}

	@Override
	public void bind(final Involves involves) {

		super.bindObject(involves, "task");

	}

	@Override
	public void validate(final Involves involves) {
		if (involves.getTask() == null)
			super.state(false, "task", "acme.validation.technician.involves.must-select-task");
	}

	@Override
	public void perform(final Involves involves) {

		this.repository.save(involves);

	}

	@Override
	public void unbind(final Involves involves) {
		Technician technician;
		Collection<Task> tasks;
		int maintenanceRecordId;
		MaintenanceRecord maintenanceRecord;
		SelectChoices choices;
		Dataset dataset;

		technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();
		maintenanceRecordId = super.getRequest().getData("maintenanceRecordId", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);

		tasks = this.repository.findValidTasksToLink(maintenanceRecord, technician);
		choices = SelectChoices.from(tasks, "description", involves.getTask());

		dataset = super.unbindObject(involves, "maintenanceRecord");
		dataset.put("maintenanceRecordId", involves.getMaintenanceRecord().getId());
		dataset.put("task", choices.getSelected().getKey());
		dataset.put("tasks", choices);
		dataset.put("aircraftRegistrationNumber", involves.getMaintenanceRecord().getAircraft().getRegistrationNumber());

		super.getResponse().addData(dataset);

	}
}
