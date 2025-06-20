
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.customers.Booking;
import acme.entities.customers.Passenger;
import acme.features.customer.booking.CustomerBookingRepository;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerListBooking extends AbstractGuiService<Customer, Passenger> {

	@Autowired
	private CustomerPassengerRepository	repository;

	@Autowired
	private CustomerBookingRepository	bookingRepository;


	@Override
	public void authorise() {
		Customer currentCustomer = (Customer) super.getRequest().getPrincipal().getActiveRealm();
		boolean status;
		int bookingId;
		Booking booking;

		bookingId = super.getRequest().getData("bookingId", int.class);
		booking = this.bookingRepository.findBookingById(bookingId);

		status = super.getRequest().getPrincipal().hasRealm(currentCustomer) && currentCustomer.getId() == booking.getCustomer().getId();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Passenger> passenger;
		int bookingId;
		Booking booking;
		bookingId = super.getRequest().getData("bookingId", int.class);

		passenger = this.repository.findAllPassengerByBooking(bookingId);
		booking = this.bookingRepository.findBookingById(bookingId);
		super.getBuffer().addData(passenger);
		super.getResponse().addGlobal("bookingId", bookingId);
		super.getResponse().addGlobal("booking", booking);

	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;

		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "dateBirth");
		super.addPayload(dataset, passenger, "specialNeeds");
		super.getResponse().addData(dataset);

	}
}
