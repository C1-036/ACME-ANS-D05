
package acme.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BookingPurchaseMomentValidator.class)
@ReportAsSingleViolation
public @interface ValidBookingPurchaseMoment {

	String message() default "acme.validation.booking.purchase-moment.invalid";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
