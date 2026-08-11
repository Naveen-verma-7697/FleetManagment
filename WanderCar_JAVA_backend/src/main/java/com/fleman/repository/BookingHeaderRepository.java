package com.fleman.repository;

import com.fleman.entity.BookingHeader;
import com.fleman.entity.BookingHeader.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingHeaderRepository extends JpaRepository<BookingHeader, Long> {

    Optional<BookingHeader> findByConfirmationNoIgnoreCase(String confirmationNo);

    List<BookingHeader> findByCustomerIdAndBookingStatusNotOrderByBookingDateDesc(
            Long customerId, BookingStatus excludedStatus);


    boolean existsByConfirmationNoIgnoreCase(String confirmationNo);

   
    /**
     * How many CONFIRMED/ONGOING bookings already reserve this (hub,
     * carType) for a date range overlapping the one requested — the demand
     * side of the category-availability check. Paired with
     * CarRepository.countByHubIdAndCarTypeIdAndStatusNot (the capacity
     * side): a category is bookable for these dates only while capacity
     * exceeds this count.
     *
     * excludeBookingId lets modifyBooking re-check a booking's own new
     * dates/type without that same booking counting as competition against
     * itself; pass null from createBooking, where there's no existing
     * booking to exclude yet.
     */
    @Query("""
        SELECT COUNT(b) FROM BookingHeader b
        WHERE b.pickupHubId = :hubId
          AND b.carTypeId = :carTypeId
          AND b.bookingStatus IN :activeStatuses
          AND b.pickupDatetime < :returnDatetime
          AND b.returnDatetime > :pickupDatetime
          AND (:excludeBookingId IS NULL OR b.bookingId <> :excludeBookingId)
        """)
    long countOverlapping(
            @Param("hubId") Long hubId,
            @Param("carTypeId") Long carTypeId,
            @Param("pickupDatetime") LocalDateTime pickupDatetime,
            @Param("returnDatetime") LocalDateTime returnDatetime,
            @Param("activeStatuses") List<BookingStatus> activeStatuses,
            @Param("excludeBookingId") Long excludeBookingId
    );
    
    	
}
