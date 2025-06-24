
package acme.features.customer.make;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.customers.Booking;
import acme.entities.customers.Make;
import acme.entities.customers.Passenger;
import acme.realms.Customer;

@Repository
public interface CustomerMakeRepository extends AbstractRepository {

	@Query("select b from Booking b where b.id = :id")
	Booking findBookingById(int id);

	@Query("select p from Passenger p where p.id = :passengerId")
	Passenger findPassengerById(int passengerId);

	@Query("select p from Passenger p where p not in (select m.passenger from Make m where m.booking = :booking) and p.customer = :customer and p.dateBirth < :#{#booking.purchaseMoment}")
	Collection<Passenger> findValidPassengersToLink(Booking booking, Customer customer);

	@Query("select p from Passenger p where p in (select m.passenger from Make m where m.booking = :booking)")
	Collection<Passenger> findValidPassengersToUnlink(Booking booking);

	@Query("select m from Make m where m.booking = :booking")
	Collection<Make> findMakeByBooking(Booking booking);

	@Query("select m from Make m where m.booking = :booking and m.passenger = :passenger")
	Make findMakeByBookingAndPassenger(Booking booking, Passenger passenger);

	@Query("SELECT COUNT(p) > 0 FROM Passenger p WHERE p.id = :passengerId AND p.customer.id = :customerId")
	boolean isAccessiblePassenger(int passengerId, int customerId);

	@Query("SELECT COUNT(m) > 0 FROM Make m WHERE m.passenger.id = :passengerId AND m.booking.id = :bookingId")
	boolean isLinkedPassenger(int passengerId, int bookingId);

	@Query("select count(p) > 0 from Passenger p where p.id = :passengerId and p.dateBirth < :#{#booking.purchaseMoment}")
	boolean isBirthDateValid(int passengerId, Booking booking);

	/*
	 * @Query("Select p from Make m where m.passenger.id = :passengerId and m.booking.id = :bookingId")
	 * boolean isPassengerAlreadyLinked(int passengerId, int bookingId);
	 */

}
