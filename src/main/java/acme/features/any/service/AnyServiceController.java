/*
 * AnyServiceController.java
 *
 * Copyright (C) 2012-2025 Rafael Corchuelo.
 */

package acme.features.any.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import acme.client.components.principals.Any;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.service.Service;

@GuiController
@RequestMapping("/any/service/")
public class AnyServiceController extends AbstractGuiController<Any, Service> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyServiceShowRandomService showRandomService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addCustomCommand("show-random", "show", this.showRandomService);
	}
}
