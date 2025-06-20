
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
public class CustomerMakeDeleteService extends AbstractGuiService<Customer, Make> {

	@Autowired
	private CustomerMakeRepository		repository;

	@Autowired
	private CustomerBookingRepository	bookingRepository;


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
				isPassengerAccessible = this.repository.isLinkedPassenger(passengerId, bookingId);
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
		Integer bookingId;
		Booking booking;

		bookingId = super.getRequest().getData("bookingId", int.class);
		booking = this.repository.findBookingById(bookingId);

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
		assert make != null;

		if (!super.getBuffer().getErrors().hasErrors("passenger")) {
			Passenger passenger = make.getPassenger();

			super.state(passenger != null, "passenger", "javax.validation.constraints.NotNull.message");
		}
	}

	@Override
	public void perform(final Make make) {
		int passengerId = super.getRequest().getData("passenger", int.class);
		int bookingId = super.getRequest().getData("bookingId", int.class);

		Booking booking = this.repository.findBookingById(bookingId);
		Passenger passenger = this.repository.findPassengerById(passengerId);

		Make makeToDelete = this.repository.findMakeByBookingAndPassenger(booking, passenger);
		this.repository.delete(makeToDelete);
	}

	@Override
	public void unbind(final Make make) {
		Collection<Passenger> passenger;
		Dataset dataset;
		int bookingId;
		Booking booking;
		SelectChoices choices;

		bookingId = super.getRequest().getData("bookingId", int.class);
		booking = this.repository.findBookingById(bookingId);

		passenger = this.repository.findValidPassengersToUnlink(booking);
		choices = SelectChoices.from(passenger, "fullName", make.getPassenger());

		dataset = super.unbindObject(make, "booking");
		dataset.put("bookingId", make.getBooking().getId());
		dataset.put("locatorCode", booking.getLocatorCode());
		dataset.put("passenger", choices.getSelected().getKey());
		dataset.put("passengers", choices);
		dataset.put("tag", make.getBooking().getFlight().getTag());

		super.getResponse().addData(dataset);

	}

}
