
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.technicians.TechnicianRepository;
import acme.realms.Technician;

@Validator
public class TechnicianValidator extends AbstractValidator<ValidTechnician, Technician> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianRepository repository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidTechnician technician) {
		assert technician != null;
	}

	@Override
	public boolean isValid(final Technician technician, final ConstraintValidatorContext context) {
		assert context != null;
		boolean result;

		// Validación de duplicado
		Technician existingTechnician = this.repository.findTechnicianByLicenseNumber(technician.getLicenseNumber());
		boolean uniqueTechnician = existingTechnician == null || existingTechnician.equals(technician);
		super.state(context, uniqueTechnician, "licenseNumber", "acme.validation.technician.duplicated-license-number.message");

		// Validación unificada de formato + iniciales
		boolean validStructure = false;
		boolean initialsMatch = false;

		String license = technician.getLicenseNumber();
		if (license != null) {
			validStructure = license.matches("^[A-Z]{2,3}\\d{6}$");

			if (license.length() >= 2 && technician.getIdentity() != null) {
				char expectedFirst = technician.getIdentity().getName().charAt(0);
				char expectedSecond = technician.getIdentity().getSurname().charAt(0);
				initialsMatch = license.charAt(0) == expectedFirst && license.charAt(1) == expectedSecond;
			}
		}

		boolean validLicense = validStructure && initialsMatch;
		super.state(context, validLicense, "licenseNumber", "acme.validation.technician.license-number.message");

		result = !super.hasErrors(context);
		return result;
	}

}
