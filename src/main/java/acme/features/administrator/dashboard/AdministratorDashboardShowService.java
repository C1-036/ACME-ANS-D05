/*
 * AdministratorDashboardShowService.java
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

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.forms.AdministratorDashboard;

@GuiService
public class AdministratorDashboardShowService extends AbstractGuiService<Administrator, AdministratorDashboard> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorDashboardRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		AdministratorDashboard dashboard;
		Double ratioActiveAircrafts;
		Double ratioNonActiveAircrafts;

		ratioActiveAircrafts = this.repository.RatioActiveAircrafts();
		ratioNonActiveAircrafts = this.repository.ratioNonActiveAircrafts();

		dashboard = new AdministratorDashboard();
		dashboard.setRatioActiveAircrafts(ratioActiveAircrafts);
		dashboard.setRatioNonActiveAircrafts(ratioNonActiveAircrafts);

		super.getBuffer().addData(dashboard);
	}

	@Override
	public void unbind(final AdministratorDashboard dashboard) {
		Dataset dataset;

		dataset = super.unbindObject(dashboard, //
			"ratioActiveAircrafts", "ratioNonActiveAircrafts");

		super.getResponse().addData(dataset);
	}

}
