using FlemanApi.Models;

namespace FlemanApi.Repository;

public record CarTypeCountProjection(long CarTypeId, long TotalCars, long AvailableCars, long BookedCars, long MaintenanceCars);

// Mirrors the custom @Query methods on CarRepository/BookingHeaderRepository
// that don't fit the generic CRUD shape — category-level capacity/overlap
// checks shared by VehicleService, BookingService and StaffService's
// dashboard. (CarRepository.findAvailableCars was defined in Java but never
// actually called by any service — not ported, since it's dead code there too.)
public interface IAvailabilityRepository
{
    // Physical fleet size for a (hub, carType) combo, minus anything pulled
    // out of service for maintenance.
    Task<long> CountCapacityAsync(long hubId, long carTypeId);

    // How many CONFIRMED/ONGOING bookings already reserve this (hub,
    // carType) for a date range overlapping the one requested.
    Task<long> CountOverlappingAsync(long hubId, long carTypeId, DateTime pickupDatetime, DateTime returnDatetime, long? excludeBookingId);

    // Per-car-type fleet counts, optionally scoped to one hub — backs the
    // staff dashboard's available/booked/maintenance breakdown.
    Task<IReadOnlyList<CarTypeCountProjection>> CountByCarTypeAsync(long? hubId);
}
