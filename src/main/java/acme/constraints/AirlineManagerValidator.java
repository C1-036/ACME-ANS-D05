
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.features.airlinemanager.AirlineManagerRepository;
import acme.realms.AirlineManager;

@Validator
public class AirlineManagerValidator extends AbstractValidator<ValidIdentifierAirlineManager, AirlineManager> {

	@Autowired
	private AirlineManagerRepository repository;


	@Override
	protected void initialise(final ValidIdentifierAirlineManager annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final AirlineManager airlineManager, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;

		if (airlineManager == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			DefaultUserIdentity identity = airlineManager.getIdentity();

			if (identity != null && identity.getName() != null && identity.getSurname() != null) {
				String name = identity.getName().trim();
				String surname = identity.getSurname().trim();

				char nameInitial = name.charAt(0);
				char surnameInitial = surname.split(" ")[0].charAt(0);

				String expectedPrefix = "" + nameInitial + surnameInitial;
				String identifier = airlineManager.getIdentifierNumber();

				boolean matchesPattern = identifier != null && identifier.matches("^[A-Z]{2,3}\\d{6}$");
				boolean startsWithPrefix = identifier != null && identifier.startsWith(expectedPrefix);

				// Verificamos si ya existe otro manager con el mismo identifier
				boolean alreadyExists = this.repository.existsByIdentifierNumber(identifier);
				AirlineManager existing = this.repository.findByIdentifierNumber(identifier);

				// Es válido si no existe o si existe pero es el mismo
				boolean isUnique = !alreadyExists || existing != null && existing.getId() == airlineManager.getId();

				boolean isValidIdentifier = matchesPattern && startsWithPrefix && isUnique;

				super.state(context, isValidIdentifier, "identifierNumber", "acme.validation.airline-manager.flight.identifier-number.invalid.message");
			}
		}

		result = !super.hasErrors(context);
		return result;
	}
}
