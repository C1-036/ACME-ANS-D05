
package acme.entities.aircraft;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import acme.client.components.basis.AbstractRole;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.entities.airline.Airline;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Aircraft extends AbstractRole {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Automapped
	@Mandatory
	@ValidString(max = 50)
	private String				model;

	@Automapped
	@Mandatory
	@ValidString(max = 50)
	private String				registrationNumber;

	@Automapped
	@Mandatory
	@ValidNumber(min = 1, max = 1000)
	private int					passengerCapacity;

	@Automapped
	@Mandatory
	@ValidNumber(min = 2000, max = 50000)
	private int					cargoWeight;

	@Automapped
	@Mandatory
	@Enumerated(EnumType.STRING)
	private AircraftStatus		status;

	@Automapped
	@Optional
	@ValidString(max = 255)
	private String				details;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

	@Automapped
	@Mandatory
	@ManyToOne(optional = false)
	private Airline				airline;
}
