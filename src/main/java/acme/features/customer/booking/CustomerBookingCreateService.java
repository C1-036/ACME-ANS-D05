
package acme.features.customer.booking;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.customers.Booking;
import acme.entities.customers.TravelClass;
import acme.entities.flights.Flight;
import acme.realms.Customer;

@GuiService
public class CustomerBookingCreateService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		boolean status;
		Customer customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();
		Date currentDate = MomentHelper.getCurrentMoment();
		boolean hasFlightId = super.getRequest().hasData("flight", int.class);
		boolean isFlightAccessible = false;

		if (hasFlightId) {
			int flightId = super.getRequest().getData("flight", int.class);

			if (flightId != 0)
				isFlightAccessible = this.repository.isFlightPublished(flightId, currentDate);
			else

				isFlightAccessible = true;
		} else

			isFlightAccessible = true;

		status = super.getRequest().getPrincipal().hasRealm(customer) && isFlightAccessible;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Customer customer;
		Booking booking;

		customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();

		booking = new Booking();
		booking.setDraftMode(true);
		booking.setCustomer(customer);

		super.getBuffer().addData(booking);

	}

	@Override
	public void bind(final Booking booking) {
		int flightId;
		Flight flight;

		flightId = super.getRequest().getData("flight", int.class);
		flight = this.repository.findFlightById(flightId);
		Date purchaseMoment;
		purchaseMoment = MomentHelper.getCurrentMoment();

		super.bindObject(booking, "locatorCode", "travelClass", "creditCard");

		booking.setPurchaseMoment(purchaseMoment);

		booking.setFlight(flight);
	}

	@Override
	public void validate(final Booking booking) {
		assert booking != null;

		if (!super.getBuffer().getErrors().hasErrors("locatorCode")) {
			String locatorCode = booking.getLocatorCode();

			boolean exists = this.repository.existsByLocatorCode(locatorCode);

			super.state(!exists, "locatorCode", "acme.validation.customer.booking.locatorCode-already-exits");
		}
	}

	@Override
	public void perform(final Booking booking) {
		Date purchaseMoment;
		purchaseMoment = MomentHelper.getCurrentMoment();
		booking.setPurchaseMoment(purchaseMoment);
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {

		Collection<Flight> flights;
		SelectChoices choices;
		Dataset dataset;
		SelectChoices choices2;
		Date currentDate = MomentHelper.getCurrentMoment();
		Date purchaseMoment;
		purchaseMoment = MomentHelper.getCurrentMoment();
		flights = this.repository.findAllFlights(currentDate);

		choices = SelectChoices.from(flights, "tag", booking.getFlight());
		choices2 = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		dataset = super.unbindObject(booking, "locatorCode", "creditCard");
		dataset.put("purchaseMoment", purchaseMoment);
		dataset.put("flight", choices.getSelected().getKey());
		dataset.put("flights", choices);
		dataset.put("price", booking.getPrice());
		dataset.put("travelClass", choices2.getSelected().getKey());
		dataset.put("travelClasss", choices2);

		super.getResponse().addData(dataset);

	}

}
