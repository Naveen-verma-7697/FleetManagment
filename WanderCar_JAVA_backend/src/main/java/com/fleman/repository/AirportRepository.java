package com.fleman.repository;

import com.fleman.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AirportRepository extends JpaRepository<Airport, Long> {

    // GET /api/airports/search?q= — matches on code or name, case-insensitive.
    List<Airport> findByAirportCodeContainingIgnoreCaseOrAirportNameContainingIgnoreCase(
            String code, String name);

    // Uniqueness checks for the Master API's create/update — airport_code is
    // UNIQUE per the DB design, so both need to be enforced at the service
    // layer before a DB constraint violation would (a friendlier 409 instead
    // of a raw SQL exception).
    boolean existsByAirportCodeIgnoreCase(String airportCode);

    boolean existsByAirportCodeIgnoreCaseAndAirportIdNot(String airportCode, Long airportId);
}
