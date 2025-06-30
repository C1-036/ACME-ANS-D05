
package acme.features.customer.make;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.customers.Booking;
import acme.entities.customers.Make;
import acme.entities.customers.Passenger;
import acme.features.customer.booking.CustomerBookingRepository;
import acme.realms.Customer;

@GuiService
public class CustomerMakeCreateService extends AbstractGuiService<Customer, Make> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerMakeRepository		repository;

	@Autowired
	private CustomerBookingRepository	bookingRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int bookingId;
		Booking booking;
		Customer bookingCustomer;
		Customer currentCustomer;

		bookingId = super.getRequest().getData("bookingId", int.class);
		booking = this.bookingRepository.findBookingById(bookingId);

		currentCustomer = (Customer) super.getRequest().getPrincipal().getActiveRealm();

		boolean hasPassengerId = super.getRequest().hasData("passenger", int.class);
		boolean isPassengerAccessible = false;

		if (hasPassengerId) {
			int passengerId = super.getRequest().getData("passenger", int.class);

			if (passengerId != 0)
				isPassengerAccessible = this.repository.isAccessiblePassenger(passengerId, currentCustomer.getId()) && !this.repository.isLinkedPassenger(passengerId, bookingId);
			else
				isPassengerAccessible = true;
		} else
			isPassengerAccessible = true;

		bookingCustomer = booking == null ? null : booking.getCustomer();

		status = super.getRequest().getPrincipal().hasRealm(bookingCustomer) && isPassengerAccessible && bookingCustomer.getId() == currentCustomer.getId() && booking.isDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Make make;
		int bookingId;
		Booking booking;

		bookingId = super.getRequest().getData("bookingId", int.class);
		booking = this.bookingRepository.findBookingById(bookingId);

		make = new Make();
		make.setBooking(booking);
		super.getBuffer().addData(make);
		super.getResponse().addGlobal("bookingId", bookingId);
	}

	@Override
	public void bind(final Make make) {
		int bookingId;
		Booking booking;

		bookingId = super.getRequest().getData("bookingId", int.class);
		booking = this.bookingRepository.findBookingById(bookingId);

		super.bindObject(make, "passenger");
		make.setBooking(booking);
		super.getResponse().addGlobal("bookingId", bookingId);
	}

	@Override
	public void validate(final Make make) {
		// Aquí puedes agregar validaciones si es necesario
	}

	@Override
	public void perform(final Make make) {
		this.repository.save(make);
	}

	@Override
	public void unbind(final Make make) {
		Collection<Passenger> passengers;
		SelectChoices choices;
		Dataset dataset;
		Booking booking = make.getBooking();
		Customer customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();

		passengers = this.repository.findValidPassengersToLink(booking, customer);
		choices = SelectChoices.from(passengers, "fullName", make.getPassenger());

		dataset = super.unbindObject(make, "booking");
		dataset.put("bookingId", super.getRequest().getData("bookingId", int.class));
		dataset.put("locatorCode", make.getBooking().getLocatorCode());
		dataset.put("passenger", choices.getSelected().getKey());
		dataset.put("passengers", choices);
		dataset.put("tag", make.getBooking().getFlight().getTag());

		super.getResponse().addData(dataset);
	}
}
