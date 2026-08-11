import { useEffect, useState } from 'react';
import { LockIcon } from '../icons/Icons';
import { useAuth } from '../../context/AuthContext';
import { useBooking } from '../../context/BookingContext';
import { getCityById, getStateById } from '../../api/locationService';
import * as bookingService from '../../api/bookingService';
import { formatDateShort, formatCurrency } from '../../utils/format';

function TripCard({ trip, onUseRoute }) {
  const pickupCity = getCityById(trip.pickupHub?.cityId);
  const dropCity = getCityById(trip.dropHub?.cityId);
  const sameLocation = trip.pickupHubId === trip.dropHubId;

  return (
    <article className="relative rounded-[22px] border border-white/80 bg-white/65 p-5.5 shadow-[0_10px_30px_rgba(18,21,28,0.06)] backdrop-blur-xl backdrop-saturate-150 transition-all hover:-translate-y-0.75 hover:shadow-[0_16px_36px_rgba(18,21,28,0.1)]">
      <p className="font-mono text-[11.5px] tracking-[0.1em] text-ink/40 uppercase">
        {formatDateShort(trip.bookingDate)}
      </p>
      <div className="my-3.5 flex items-center gap-2.5">
        <div className="flex flex-col">
          <span className="text-[11px] tracking-[0.08em] text-ink/40 uppercase">Pickup</span>
          <span className="mt-0.5 font-display text-[15px] font-semibold">{pickupCity?.cityName || 'Pickup hub'}</span>
        </div>
        <div className="flex flex-1 items-center gap-1 text-ink/25">
          <span
            className="h-px flex-1"
            style={{
              backgroundImage:
                'repeating-linear-gradient(to right, rgba(18,21,28,0.3) 0 4px, transparent 4px 8px)',
            }}
          />
          →
        </div>
        <div className="flex flex-col">
          <span className="text-[11px] tracking-[0.08em] text-ink/40 uppercase">
            Return {sameLocation ? '(same)' : ''}
          </span>
          <span className="mt-0.5 font-display text-[15px] font-semibold">{dropCity?.cityName || 'Drop hub'}</span>
        </div>
      </div>
      <div className="mt-4.5 flex items-center justify-between">
        <span className="font-display text-sm font-semibold text-ink/70">
          {formatCurrency(trip.carType?.dailyRate)}/day
        </span>
        <button
          type="button"
          onClick={() => onUseRoute(trip)}
          className="inline-flex items-center gap-1.5 rounded-full bg-ink px-4 py-2 text-[13px] font-medium text-white transition-colors hover:bg-steel-dark"
        >
          Use this route
        </button>
      </div>
    </article>
  );
}

export default function TripHistory() {
  const { customer, openAuth } = useAuth();
  const { applyPrefill } = useBooking();
  const [lastTrip, setLastTrip] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!customer) {
      setLastTrip(null);
      return;
    }
    setLoading(true);
    bookingService
      .getLastBookingForCustomer(customer.customerId)
      .then(setLastTrip)
      .finally(() => setLoading(false));
  }, [customer]);

  function handleUseRoute(trip) {
    const pickupCity = getCityById(trip.pickupHub?.cityId);
    const pickupState = getStateById(trip.pickupHub?.stateId);
    const dropCity = getCityById(trip.dropHub?.cityId);
    const dropState = getStateById(trip.dropHub?.stateId);
    const sameLocation = trip.pickupHubId === trip.dropHubId;

    applyPrefill({
      pickupMode: 'city',
      pickupState: pickupState?.stateId || '',
      pickupCity: pickupCity?.cityName || '',
      differentDropoff: !sameLocation,
      dropoffMode: 'city',
      dropoffState: dropState?.stateId || '',
      dropoffCity: dropCity?.cityName || '',
    });
    document.getElementById('home')?.scrollIntoView({ behavior: 'smooth' });
  }

  return (
    <section id="trips" className="mx-auto max-w-[1200px] bg-paper px-6 py-16 md:px-8 md:py-22">
      <div className="mb-11 flex flex-wrap items-end justify-between gap-5">
        <div>
          <p className="mb-3 font-mono text-xs tracking-[0.2em] text-steel uppercase">Pick up where you left off</p>
          <h2 className="font-display text-[28px] font-semibold tracking-[-0.01em] md:text-[34px]">Your last ride</h2>
          <p className="mt-3 max-w-115 text-[15.5px] text-ink/55">
            Same route as last time? Reuse it in one tap and it'll fill in the booking card above.
          </p>
        </div>
      </div>

      {!customer && (
        <div className="relative max-w-155 overflow-hidden rounded-[22px]">
          <div className="pointer-events-none opacity-75 blur-[7px] select-none">
            <article className="rounded-[22px] border border-white/80 bg-white/65 p-5.5 shadow-[0_10px_30px_rgba(18,21,28,0.06)]">
              <p className="font-mono text-[11.5px] tracking-[0.1em] text-ink/40 uppercase">Jul 18</p>
              <div className="my-3.5 flex items-center gap-2.5">
                <div className="flex flex-col">
                  <span className="text-[11px] tracking-[0.08em] text-ink/40 uppercase">Pickup</span>
                  <span className="mt-0.5 font-display text-[15px] font-semibold">Mumbai</span>
                </div>
                <div className="flex-1 text-ink/25">→</div>
                <div className="flex flex-col">
                  <span className="text-[11px] tracking-[0.08em] text-ink/40 uppercase">Return (same)</span>
                  <span className="mt-0.5 font-display text-[15px] font-semibold">Mumbai</span>
                </div>
              </div>
              <div className="mt-4.5 flex items-center justify-between">
                <span className="font-display text-sm font-semibold text-ink/70">₹2,200/day</span>
                <span className="inline-flex items-center gap-1.5 rounded-full bg-ink px-4 py-2 text-[13px] font-medium text-white">
                  Use this route
                </span>
              </div>
            </article>
          </div>
          <div className="absolute inset-y-0 right-0 left-auto flex w-[58%] flex-col items-center justify-center gap-3 border-l border-white/70 bg-white/55 p-6 text-center shadow-[-12px_0_30px_rgba(18,21,28,0.06)] backdrop-blur-lg backdrop-saturate-150 max-[640px]:w-full max-[640px]:border-l-0">
            <span className="grid h-10.5 w-10.5 place-items-center rounded-full bg-ink/8 text-ink">
              <LockIcon width="18" height="18" />
            </span>
            <p className="max-w-65 text-sm leading-[1.5] text-ink/70">Log in to see your last ride and rebook it in one tap.</p>
            <button
              type="button"
              onClick={() => openAuth('login')}
              className="inline-flex items-center gap-1.5 rounded-full bg-primary px-4 py-2 text-[13px] font-medium text-white transition-colors hover:bg-primary-dark"
            >
              Log in
            </button>
          </div>
        </div>
      )}

      {customer && lastTrip && (
        <div className="max-w-95">
          <TripCard trip={lastTrip} onUseRoute={handleUseRoute} />
        </div>
      )}

      {customer && !lastTrip && !loading && (
        <p className="max-w-105 text-[14.5px] text-ink/50">
          No trips yet — book your first car above and it'll show up here next time.
        </p>
      )}
    </section>
  );
}
