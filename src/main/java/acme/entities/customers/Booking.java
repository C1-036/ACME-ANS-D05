
package acme.entities.customers;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidBookingPurchaseMoment;
import acme.entities.flights.Flight;
import acme.features.customer.booking.CustomerBookingRepository;
import acme.realms.Customer;
import lombok.Getter;
import lombok.Setter;

@ValidBookingPurchaseMoment
@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "locatorCode"), @Index(columnList = "flight_id, customer_id")
})
public class Booking extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	//Atributos ------------------------------------

	@Mandatory
	@ValidString(pattern = "^[A-Z0-9]{6,8}$")
	@Column(unique = true)
	private String				locatorCode;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				purchaseMoment;

	@Mandatory
	@Valid
	@Automapped
	private TravelClass			travelClass;

	@Optional
	@ValidString(min = 4, max = 4, pattern = "^\\d+$")
	@Automapped
	private String				creditCard;

	@Mandatory
	//@Valid by default
	@Automapped
	private boolean				draftMode;

	// Relationships ----------------------------------------------------------
	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Customer			customer;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Flight				flight;


	@Transient
	public Money getPrice() {
		if (this.flight == null) {
			Money porDefecto = new Money();
			porDefecto.setAmount(0.0);
			porDefecto.setCurrency("EUR");
			return porDefecto;
		}
		CustomerBookingRepository bookingRepository = SpringHelper.getBean(CustomerBookingRepository.class);
		Money result = bookingRepository.findCostByFlightBooking(this.flight.getId());

		Collection<Passenger> passengers = bookingRepository.findAllPassengerBooking(this.getId());
		double amount = result.getAmount() * passengers.size();
		result.setAmount(amount);

		return result;
	}

}
