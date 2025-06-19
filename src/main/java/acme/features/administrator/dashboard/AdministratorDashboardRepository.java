/*
 * AdministratorDashboardRepository.java
 *
 * Copyright (C) 2012-2025 Rafael Corchuelo.
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.features.administrator.dashboard;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface AdministratorDashboardRepository extends AbstractRepository {

	@Query("select 1.0 * count(a) / (select count(b) from Aircraft b) from Aircraft a where a.status = acme.entities.aircraft.AircraftStatus.ACTIVE")
	Double RatioActiveAircrafts();

	@Query("select 1.0 * count(a) / (select count(b) from Aircraft b) from Aircraft a where a.status = acme.entities.aircraft.AircraftStatus.UNDER_MAINTENANCE")
	Double ratioNonActiveAircrafts();

}
