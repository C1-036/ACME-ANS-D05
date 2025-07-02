
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.features.airlinemanager.AirlineManagerRepository;
import acme.realms.AirlineManager;

@Validator
public class AirlineManagerValidator extends AbstractValidator<ValidAirlineManager, AirlineManager> {

	@Autowired
	private AirlineManagerRepository repository;


	@Override
	protected void initialise(final ValidAirlineManager annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final AirlineManager airlineManager, final ConstraintValidatorContext context) {
		if (airlineManager == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			DefaultUserIdentity id = airlineManager.getIdentity();
			if (id != null && id.getName() != null && id.getSurname() != null) {

				String name = id.getName().trim();
				String surname = id.getSurname().trim().split(" ")[0];
				String expectedPrefix = "" + name.charAt(0) + surname.charAt(0);
				String identifier = airlineManager.getIdentifierNumber();

				// 1) Prefijo
				boolean startsWithPrefix = identifier != null && identifier.startsWith(expectedPrefix);
				super.state(context, startsWithPrefix, "identifierNumber", "acme.validation.airline-manager.identifier.prefix.message");

				// 2) Unicidad
				boolean exists = identifier != null && this.repository.existsByIdentifierNumber(identifier);
				AirlineManager existing = exists ? this.repository.findByIdentifierNumber(identifier) : null;
				boolean isUnique = !exists || existing != null && existing.getId() == airlineManager.getId();
				super.state(context, isUnique, "identifierNumber", "acme.validation.airline-manager.identifier.unique.message");
			}
		}
		return !super.hasErrors(context);
	}

}
