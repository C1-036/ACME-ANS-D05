
package acme.features.airport;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airports.Airport;
import acme.entities.airports.Scope;

@GuiService
public class AdministratorAirportUpdateService extends AbstractGuiService<Administrator, Airport> {

	// Internal state ---------------------------------------------------------

	private final AdministratorAirportRepository repository;


	@Autowired
	public AdministratorAirportUpdateService(final AdministratorAirportRepository repository) {
		this.repository = repository;
	}

	// AbstractService<Administrator, Airport> -------------------------------------

	@Override
	public void authorise() {
		Boolean authorised;
		String rawId;
		int airportId;
		Airport airport;

		try {
			rawId = super.getRequest().getData("id", String.class);
			airportId = Integer.parseInt(rawId);
			airport = this.repository.findAirportById(airportId);
			authorised = airport != null;
		} catch (NumberFormatException | AssertionError e) {
			authorised = false;
		}
		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		Airport airport;
		int id;

		id = super.getRequest().getData("id", int.class);
		airport = this.repository.findAirportById(id);

		super.getBuffer().addData(airport);
	}

	@Override
	public void bind(final Airport airport) {
		super.bindObject(airport, "name", "iataCode", "operationalScope", "city", "country", "website", "emailAddress", "contactPhoneNumber");
	}

	@Override
	public void validate(final Airport airport) {
		boolean confirmation;
		Airport existing;

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.airport.confirmation.message");

		existing = this.repository.findAirportByIataCode(airport.getIataCode());
		super.state(existing == null, "iataCode", "acme.validation.airport.iataCode.duplicated");
	}

	@Override
	public void perform(final Airport airport) {
		this.repository.save(airport);
	}

	@Override
	public void unbind(final Airport airport) {
		Dataset dataset;
		SelectChoices choices;

		choices = SelectChoices.from(Scope.class, airport.getOperationalScope());

		dataset = super.unbindObject(airport, "name", "iataCode", "operationalScope", "city", "country", "website", "emailAddress", "contactPhoneNumber");
		dataset.put("confirmation", false);
		dataset.put("readonly", false);
		dataset.put("scopes", choices);

		super.getResponse().addData(dataset);
	}
}
