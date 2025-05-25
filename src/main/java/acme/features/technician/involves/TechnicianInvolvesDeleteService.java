/*
 * package acme.features.technician.involves;
 * 
 * import java.util.Collection;
 * 
 * import org.springframework.beans.factory.annotation.Autowired;
 * 
 * import acme.client.components.models.Dataset;
 * import acme.client.components.views.SelectChoices;
 * import acme.client.services.AbstractGuiService;
 * import acme.client.services.GuiService;
 * import acme.entities.technicians.Involves;
 * import acme.entities.technicians.MaintenanceRecord;
 * import acme.entities.technicians.Task;
 * import acme.realms.Technician;
 * 
 * @GuiService
 * public class TechnicianInvolvesDeleteService extends AbstractGuiService<Technician, Involves> {
 * 
 * // Internal state ---------------------------------------------------------
 * 
 * @Autowired
 * private TechnicianInvolvesRepository repository;
 * 
 * // AbstractGuiService interface -------------------------------------------
 * 
 * 
 * @Override
 * public void authorise() {
 * boolean statusTask = true;
 * boolean status = false;
 * int taskId;
 * Task task;
 * int maintenanceRecordId;
 * MaintenanceRecord maintenanceRecord;
 * Collection<Task> tasks;
 * 
 * maintenanceRecordId = super.getRequest().getData("maintenanceRecordId", int.class);
 * maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);
 * 
 * tasks = this.repository.findValidTasksToUnlink(maintenanceRecord);
 * 
 * if (super.getRequest().hasData("task", int.class)) {
 * taskId = super.getRequest().getData("task", int.class);
 * task = this.repository.findTaskById(taskId);
 * 
 * if (!tasks.contains(task) && taskId != 0)
 * statusTask = false;
 * }
 * 
 * status = maintenanceRecord != null && maintenanceRecord.isDraftMode() && super.getRequest().getPrincipal().hasRealm(maintenanceRecord.getTechnician());
 * 
 * super.getResponse().setAuthorised(status && statusTask);
 * }
 * 
 * @Override
 * public void load() {
 * Involves object;
 * Integer maintenanceRecordId;
 * MaintenanceRecord maintenanceRecord;
 * 
 * maintenanceRecordId = super.getRequest().getData("maintenanceRecordId", int.class);
 * maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);
 * 
 * object = new Involves();
 * object.setMaintenanceRecord(maintenanceRecord);
 * super.getBuffer().addData(object);
 * }
 * 
 * @Override
 * public void bind(final Involves involves) {
 * ;
 * }
 * 
 * @Override
 * public void validate(final Involves involves) {
 * 
 * Task task = super.getRequest().getData("task", Task.class);
 * super.state(task != null, "task", "technician.involves.form.error.no-task-to-unlink");
 * }
 * 
 * @Override
 * public void perform(final Involves involves) {
 * Task task = super.getRequest().getData("task", Task.class);
 * MaintenanceRecord maintenanceRecord = involves.getMaintenanceRecord();
 * 
 * this.repository.delete(this.repository.findInvolvesByMaintenanceRecordAndTask(maintenanceRecord, task));
 * 
 * }
 * 
 * @Override
 * public void unbind(final Involves involves) {
 * Collection<Task> tasks;
 * int maintenanceRecordId;
 * MaintenanceRecord maintenanceRecord;
 * SelectChoices choices;
 * Dataset dataset;
 * 
 * maintenanceRecordId = super.getRequest().getData("maintenanceRecordId", int.class);
 * maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);
 * 
 * tasks = this.repository.findValidTasksToUnlink(maintenanceRecord);
 * choices = SelectChoices.from(tasks, "description", involves.getTask());
 * 
 * dataset = super.unbindObject(involves, "maintenanceRecord");
 * dataset.put("maintenanceRecordId", involves.getMaintenanceRecord().getId());
 * dataset.put("task", choices.getSelected().getKey());
 * dataset.put("tasks", choices);
 * dataset.put("aircraftRegistrationNumber", involves.getMaintenanceRecord().getAircraft().getRegistrationNumber());
 * 
 * super.getResponse().addData(dataset);
 * }
 * }
 */

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
public class TechnicianInvolvesDeleteService extends AbstractGuiService<Technician, Involves> {

	@Autowired
	private TechnicianInvolvesRepository repository;


	@Override
	public void authorise() {
		boolean statusTask = true;
		boolean status = false;
		int taskId;
		Task task;
		int maintenanceRecordId;
		MaintenanceRecord maintenanceRecord;
		Collection<Task> tasks;

		maintenanceRecordId = super.getRequest().getData("maintenanceRecordId", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);

		tasks = this.repository.findValidTasksToUnlink(maintenanceRecord);

		if (super.getRequest().hasData("task", int.class)) {
			taskId = super.getRequest().getData("task", int.class);

			// Si taskId es distinto de 0, validamos que exista y pertenezca
			if (taskId != 0) {
				task = this.repository.findTaskById(taskId);
				if (task == null || !tasks.contains(task))
					statusTask = false;
			}
		}

		status = maintenanceRecord != null && maintenanceRecord.isDraftMode() && super.getRequest().getPrincipal().hasRealm(maintenanceRecord.getTechnician());

		super.getResponse().setAuthorised(status && statusTask);
	}

	@Override
	public void load() {
		int maintenanceRecordId = super.getRequest().getData("maintenanceRecordId", int.class);
		MaintenanceRecord maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);

		Involves object = new Involves();
		object.setMaintenanceRecord(maintenanceRecord);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Involves involves) {
		// Nada que bindear
	}

	@Override
	public void validate(final Involves involves) {
		Task task = super.getRequest().getData("task", Task.class);
		super.state(task != null, "task", "technician.involves.form.error.no-task-to-unlink");
	}

	@Override
	public void perform(final Involves involves) {
		Task task = super.getRequest().getData("task", Task.class);
		MaintenanceRecord record = involves.getMaintenanceRecord();
		Involves target = this.repository.findInvolvesByMaintenanceRecordAndTask(record, task);

		this.repository.delete(target);
	}

	@Override
	public void unbind(final Involves involves) {
		int maintenanceRecordId = super.getRequest().getData("maintenanceRecordId", int.class);
		MaintenanceRecord maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);

		Collection<Task> tasks = this.repository.findValidTasksToUnlink(maintenanceRecord);
		SelectChoices choices = SelectChoices.from(tasks, "description", involves.getTask());

		Dataset dataset = super.unbindObject(involves, "maintenanceRecord");
		dataset.put("maintenanceRecordId", involves.getMaintenanceRecord().getId());
		dataset.put("task", choices.getSelected().getKey());
		dataset.put("tasks", choices);
		dataset.put("aircraftRegistrationNumber", involves.getMaintenanceRecord().getAircraft().getRegistrationNumber());

		super.getResponse().addData(dataset);
	}
}
