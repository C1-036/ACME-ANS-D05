
package acme.constraints;

import java.util.Collection;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.customers.Booking;
import acme.entities.flights.FlightRepository;
import acme.entities.flights.Leg;

@Validator
public class BookingPurchaseMomentValidator extends AbstractValidator<ValidBookingPurchaseMoment, Booking> {

	@Autowired
	private FlightRepository flightRepository;


	@Override
	protected void initialise(final ValidBookingPurchaseMoment annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Booking booking, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result = true;

		if (booking != null && booking.getPurchaseMoment() != null && booking.getFlight() != null) {
			Collection<Leg> legs = this.flightRepository.findLegsByFlight(booking.getFlight().getId());

			Leg firstLeg = legs.stream().filter(l -> l.getScheduledDeparture() != null).min((l1, l2) -> l1.getScheduledDeparture().compareTo(l2.getScheduledDeparture())).orElse(null);

			if (firstLeg != null && firstLeg.getScheduledDeparture().before(booking.getPurchaseMoment())) {
				super.state(context, false, "purchaseMoment", "acme.validation.booking.purchase-moment.invalid");
				result = false;
			}
		}

		return result && !super.hasErrors(context);
	}
}
