
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
import acme.entities.customers.Passenger;
import acme.entities.customers.TravelClass;
import acme.entities.flights.Flight;
import acme.realms.Customer;

@GuiService
public class CustomerBookingPublishService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		boolean status;
		Booking booking;

		int bookingId = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(bookingId);

		boolean hasFlightId = super.getRequest().hasData("flight", int.class);
		boolean isFlightAccessible = false;
		Date currentDate = MomentHelper.getCurrentMoment();

		if (hasFlightId) {
			int flightId = super.getRequest().getData("flight", int.class);

			if (flightId != 0)
				isFlightAccessible = this.repository.isFlightPublished(flightId, currentDate);
			else

				isFlightAccessible = true;
		} else {
			Flight assignedFlight = booking != null ? booking.getFlight() : null;
			if (assignedFlight != null)
				isFlightAccessible = this.repository.isFlightPublished(assignedFlight.getId(), currentDate);
			else
				isFlightAccessible = true;
		}

		Customer current = (Customer) super.getRequest().getPrincipal().getActiveRealm();
		status = booking != null && booking.getCustomer().equals(current) && booking.isDraftMode() && isFlightAccessible;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Booking booking;
		int id;

		id = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(id);

		super.getBuffer().addData(booking);

	}

	@Override
	public void bind(final Booking booking) {
		int flightId;
		Flight flight;

		flightId = super.getRequest().getData("flight", int.class);
		flight = this.repository.findFlightById(flightId);

		super.bindObject(booking, "locatorCode", "travelClass", "creditCard");

		booking.setFlight(flight);
	}

	@Override
	public void validate(final Booking booking) {
		assert booking != null;

		if (!super.getBuffer().getErrors().hasErrors("creditCard")) {
			String card = booking.getCreditCard();

			super.state(!card.isBlank(), "creditCard", "javax.validation.constraints.NotNull.message");
		}

		if (!super.getBuffer().getErrors().hasErrors("passenger")) {
			Collection<Passenger> passengers;
			passengers = this.repository.findAllPassengerBooking(booking.getId());
			boolean allPassengerPublished = passengers.stream().allMatch(p -> !p.isDraftMode());
			super.state(!passengers.isEmpty(), "*", "acme.validation.customer.booking.no-associated-passenger");
			super.state(allPassengerPublished, "*", "acme.validation.customer.booking.no-publicated-passenger");
		}

		if (!super.getBuffer().getErrors().hasErrors("locatorCode")) {
			String locatorCode = booking.getLocatorCode();
			int id = booking.getId();
			boolean exists = this.repository.existsByLocatorCodeAndIdNot(locatorCode, id);

			super.state(!exists, "locatorCode", "acme.validation.customer.booking.locatorCode-already-exits");
		}
	}

	@Override
	public void perform(final Booking booking) {
		booking.setDraftMode(false);
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		Dataset dataset;
		SelectChoices choices;
		Collection<Flight> flights;
		SelectChoices choices2;
		Date currentDate = MomentHelper.getCurrentMoment();

		flights = this.repository.findAllFlights(currentDate);

		choices = SelectChoices.from(flights, "tag", booking.getFlight());
		choices2 = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "creditCard", "customer", "flight", "draftMode");
		dataset.put("flight", choices.getSelected().getKey());
		dataset.put("flights", choices);
		dataset.put("price", booking.getPrice());
		dataset.put("travelClass", choices2.getSelected().getKey());
		dataset.put("travelClasss", choices2);

		super.getResponse().addData(dataset);

	}

}
