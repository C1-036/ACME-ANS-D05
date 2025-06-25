
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

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianInvolvesRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status = false;
		boolean statusTask = true;

		if (super.getRequest().hasData("maintenanceRecordId", int.class)) {
			int maintenanceRecordId = super.getRequest().getData("maintenanceRecordId", int.class);
			MaintenanceRecord maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);

			if (maintenanceRecord != null && maintenanceRecord.isDraftMode() && super.getRequest().getPrincipal().hasRealm(maintenanceRecord.getTechnician())) {
				status = true;

				if (super.getRequest().getMethod().equalsIgnoreCase("POST"))
					// Permitir continuar aunque no se haya seleccionado nada (task = 0)
					if (super.getRequest().hasData("task", int.class)) {
						int taskId = super.getRequest().getData("task", int.class);
						if (taskId != 0) {
							Task task = this.repository.findTaskById(taskId);
							Collection<Task> validTasks = this.repository.findValidTasksToUnlink(maintenanceRecord);
							statusTask = task != null && validTasks.contains(task);
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
		;
	}

	@Override
	public void validate(final Involves involves) {
		Task task = super.getRequest().getData("task", Task.class);
		Collection<Task> validTasks = this.repository.findValidTasksToUnlink(involves.getMaintenanceRecord());

		super.state(task != null && validTasks.contains(task), "task", "technician.involves.form.error.no-task-to-unlink");
	}

	@Override
	public void perform(final Involves involves) {
		Task task = super.getRequest().getData("task", Task.class);
		MaintenanceRecord maintenanceRecord = involves.getMaintenanceRecord();

		this.repository.delete(this.repository.findInvolvesByMaintenanceRecordAndTask(maintenanceRecord, task));

	}

	@Override
	public void unbind(final Involves involves) {
		Collection<Task> tasks;
		int maintenanceRecordId;
		MaintenanceRecord maintenanceRecord;
		SelectChoices choices;
		Dataset dataset;

		maintenanceRecordId = super.getRequest().getData("maintenanceRecordId", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);

		tasks = this.repository.findValidTasksToUnlink(maintenanceRecord);
		choices = SelectChoices.from(tasks, "description", involves.getTask());

		dataset = super.unbindObject(involves, "maintenanceRecord");
		dataset.put("maintenanceRecordId", involves.getMaintenanceRecord().getId());
		dataset.put("task", choices.getSelected().getKey());
		dataset.put("tasks", choices);
		dataset.put("aircraftRegistrationNumber", involves.getMaintenanceRecord().getAircraft().getRegistrationNumber());

		super.getResponse().addData(dataset);
	}
}
