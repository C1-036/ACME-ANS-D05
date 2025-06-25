
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircraft.Aircraft;
import acme.entities.technicians.Involves;
import acme.entities.technicians.MaintenanceRecord;
import acme.entities.technicians.Task;
import acme.realms.Technician;

@Repository
public interface TechnicianMaintenanceRecordRepository extends AbstractRepository {

	@Query("select mr from MaintenanceRecord mr where mr.technician.id = :technicianId")
	Collection<MaintenanceRecord> findMaintenanceRecordsByTechnicianId(int technicianId);

	@Query("select mr from MaintenanceRecord mr where mr.id = :id")
	MaintenanceRecord findMaintenanceRecordById(int id);

	@Query("select mr from MaintenanceRecord mr where mr.draftMode = false")
	Collection<MaintenanceRecord> findPublishedMaintenanceRecords();

	@Query("select a from Aircraft a")
	Collection<Aircraft> findAircrafts();

	@Query("select i.task from Involves i where i.maintenanceRecord.id = :maintenanceRecordId")
	Collection<Task> findTasksByMaintenanceRecordId(int maintenanceRecordId);

	@Query("select i from Involves i where i.maintenanceRecord.id = :maintenanceRecordId")
	Collection<Involves> findInvolvesByMaintenanceRecordId(int maintenanceRecordId);

	@Query("select a from Aircraft a where a.id = :id")
	Aircraft findAircraftById(int id);

	@Query("select count(i) from Involves i where i.maintenanceRecord.id = :maintenanceRecordId")
	int countTasksByMaintenanceRecordId(int maintenanceRecordId);

	@Query("select count(t) = sum(case when t.draftMode = false then 1 else 0 end) from Involves i join i.task t where i.maintenanceRecord.id = :maintenanceRecordId")
	boolean areAllTasksPublished(int maintenanceRecordId);

	@Query("select t from Technician t where t.userAccount.id = :userAccountId")
	Technician findOneTechnicianByUserAccountId(int userAccountId);

}
