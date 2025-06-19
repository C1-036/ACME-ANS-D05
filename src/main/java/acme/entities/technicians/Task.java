
package acme.entities.technicians;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidNumber;
import acme.constraints.ValidLongText;
import acme.realms.Technician;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "draftMode"),              // 
	@Index(columnList = "draftMode, technician_id")
})
public class Task extends AbstractEntity {

	// Serialization version ---------------------------------------------------
	private static final long	serialVersionUID	= 1L;

	// Attributes --------------------------------------------------------------------
	@Mandatory
	@Valid
	@Automapped
	private TaskType			type;

	@Mandatory
	@ValidLongText
	@Automapped
	private String				description;

	@Mandatory
	@ValidNumber(min = 0, max = 10)
	@Automapped
	private Integer				priority;

	@Mandatory
	@ValidNumber(min = 0, max = 1000)
	@Automapped
	private Integer				estimatedDurationHours;

	@Mandatory
	// HINT: @valid by default
	@Automapped
	private boolean				draftMode;

	// Relations ------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Technician			technician;

}
