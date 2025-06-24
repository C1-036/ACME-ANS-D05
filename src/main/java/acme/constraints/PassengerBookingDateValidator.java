
package acme.constraints;

import java.util.Collection;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.entities.customers.CustomerRepository;
import acme.entities.customers.Make;
import acme.entities.customers.Passenger;

public class PassengerBookingDateValidator extends AbstractValidator<ValidPassengerBookingDate, Passenger> {

	@Autowired
	private CustomerRepository customerRepository;


	@Override
	public boolean isValid(final Passenger passenger, final ConstraintValidatorContext context) {
		assert context != null;

		if (passenger == null)
			return false;

		if (passenger.getDateBirth() == null)
			return true;

		if (passenger.getId() == 0)
			return true;

		Collection<Make> makes = this.customerRepository.findMakesByPassenger(passenger);

		if (makes.isEmpty())
			return true;

		boolean result = true;

		for (Make make : makes) {
			boolean validBirthDate = passenger.getDateBirth().before(make.getBooking().getPurchaseMoment());

			if (!validBirthDate)
				result = false;
		}

		super.state(context, result, "dateBirth", "acme.validation.customer.passenger.dateBirth.beforePurchaseMoment");

		return result;
	}

}
