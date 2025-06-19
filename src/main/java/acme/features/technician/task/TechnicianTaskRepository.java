
package acme.features.technician.task;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.technicians.Involves;
import acme.entities.technicians.MaintenanceRecord;
import acme.entities.technicians.Task;

@Repository
public interface TechnicianTaskRepository extends AbstractRepository {

	@Query("select t from Task t where t.id = :id")
	Task findTaskById(int id);

	@Query("select mr from MaintenanceRecord mr where mr.id = :id")
	MaintenanceRecord findMaintenanceRecordById(int id);

	@Query("select inv.task from Involves inv where inv.maintenanceRecord.id = :masterId")
	Collection<Task> findTasksByMasterId(int masterId);

	@Query("select inv.maintenanceRecord from Involves inv where inv.task.id = :taskId")
	Collection<MaintenanceRecord> findMaintenanceRecordsByTaskId(int taskId);

	@Query("select t from Task t where t.technician.id = :technicianId")
	Collection<Task> findTasksByTechnicianId(int technicianId);

	@Query("select t from Task t where t.draftMode = false")
	Collection<Task> findPublishedTasks();

	@Query("select i from Involves i where i.task.id = :taskId")
	Collection<Involves> findInvolvesByTaskId(int taskId);

	@Query("""
		select t from Task t
		where (t.draftMode = false or t.technician.id = :technicianId)
		and t.id not in (
			select inv.task.id from Involves inv where inv.maintenanceRecord.id = :maintenanceRecordId
		)
		""")
	Collection<Task> findTasksAvailableToInvolve(int maintenanceRecordId, int technicianId);

}
