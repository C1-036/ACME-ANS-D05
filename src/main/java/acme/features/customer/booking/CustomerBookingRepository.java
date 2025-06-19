
package acme.features.customer.booking;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.components.datatypes.Money;
import acme.client.repositories.AbstractRepository;
import acme.entities.customers.Booking;
import acme.entities.customers.Make;
import acme.entities.customers.Passenger;
import acme.entities.flights.Flight;

@Repository
public interface CustomerBookingRepository extends AbstractRepository {

	@Query("SELECT b FROM Booking b WHERE b.customer.id = :id")
	Collection<Booking> findAllBookingByCustomer(int id);

	@Query("select b FROM Booking b where b.id = :id")
	Booking findBookingById(int id);

	@Query("select m.passenger from Make m where m.booking.id = :bookingId")
	Collection<Passenger> findAllPassengerBooking(int bookingId);

	@Query("SELECT b FROM Booking b WHERE b.customer.id = :id")
	Collection<Booking> findABookingByCustomer(int id);

	@Query("SELECT DISTINCT f FROM Flight f JOIN Leg l ON l.flight = f " + "WHERE f.draftMode = false " + "AND l.scheduledDeparture = (SELECT MIN(l2.scheduledDeparture) FROM Leg l2 WHERE l2.flight = f) " + "AND l.scheduledDeparture > :currentDate")
	Collection<Flight> findAllFlights(Date currentDate);

	@Query("SELECT f FROM Flight f WHERE f.id = :flightId and f.draftMode = false")
	Flight findFlightById(int flightId);

	@Query("SELECT f.cost FROM Flight f WHERE f.id = :flightId")
	Money findCostByFlightBooking(int flightId);

	@Query("Select m from Make m where m.booking.id = :bookingId")
	Collection<Make> findAllMakeByBooking(int bookingId);

	@Query("Select COUNT(b) > 0 from Booking b WHERE b.locatorCode = :locatorCode")
	boolean existsByLocatorCode(String locatorCode);

	@Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.flight.id = :flightId AND b.customer.id = :customerId")
	boolean isFlightBookedByCustomer(int flightId, int customerId);

	@Query("SELECT COUNT(f) > 0 FROM Flight f JOIN Leg l ON l.flight = f " + "WHERE f.id = :flightId AND f.draftMode = false " + "AND l.scheduledDeparture = (SELECT MIN(l2.scheduledDeparture) FROM Leg l2 WHERE l2.flight = f) "
		+ "AND l.scheduledDeparture > :currentDate")
	boolean isFlightPublished(int flightId, Date currentDate);

	@Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.locatorCode = :locatorCode AND b.id != :id")
	boolean existsByLocatorCodeAndIdNot(String locatorCode, int id);

}
