
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
import acme.entities.customers.Make;
import acme.entities.customers.TravelClass;
import acme.entities.flights.Flight;
import acme.realms.Customer;

@GuiService
public class CustomerBookingDeleteService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int customerId;
		Booking booking;

		customerId = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(customerId);

		Customer current = (Customer) super.getRequest().getPrincipal().getActiveRealm();
		status = booking != null && booking.getCustomer().equals(current) && booking.isDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Booking booking = this.repository.findBookingById(id);

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		super.bindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "price", "creditCard");

	}

	@Override
	public void validate(final Booking booking) {
		;
	}

	@Override
	public void perform(final Booking booking) {
		Collection<Make> make;

		make = this.repository.findAllMakeByBooking(booking.getId());
		this.repository.deleteAll(make);
		this.repository.delete(booking);
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

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "price", "creditCard", "customer", "flight", "draftMode");
		dataset.put("flight", choices.getSelected().getKey());
		dataset.put("flights", choices);
		dataset.put("travelClass", choices2.getSelected().getKey());
		dataset.put("travelClasss", choices2);

		super.getResponse().addData(dataset);
	}

}
