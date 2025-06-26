
package acme.features.any.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.service.Service;

@GuiService
public class AnyServiceShowRandomService extends AbstractGuiService<Any, Service> {

	@Autowired
	private AnyServiceRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		List<Service> services = this.repository.findRandomServiceList();
		Service service = services.isEmpty() ? null : services.get(0);
		super.getBuffer().addData(service);
	}

	@Override
	public void unbind(final Service service) {
		String picture = service == null ? null : service.getPicture();
		super.getResponse().addGlobal("randomServicePicture", picture);

		Dataset dataset = super.unbindObject(service, "picture"); // <-- AÑADIR ESTO
		super.getResponse().addData(dataset);                     // <-- Y ESTO
	}

}
