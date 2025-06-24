
package acme.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PassengerBookingDateValidator.class)
public @interface ValidPassengerBookingDate {

	String message() default "La fecha de nacimiento debe ser anterior al momento de compra de la reserva";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
