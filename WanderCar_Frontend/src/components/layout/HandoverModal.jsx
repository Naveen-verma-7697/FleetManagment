import { useEffect, useState } from 'react';
import Overlay from '../Overlay';
import * as staffService from '../../api/staffService';
import * as bookingService from '../../api/bookingService';
import { useToast } from '../../context/ToastContext';

const fuelLevels = [
  { label: '¼', value: 25 },
  { label: '½', value: 50 },
  { label: '¾', value: 75 },
  { label: 'Full', value: 100 },
];

// Statuses that should block a hand-over lookup entirely.
const BLOCKED_STATUSES = ['CANCELLED', 'COMPLETED', 'EXPIRED'];

export default function HandoverModal({ open, onClose }) {
  const toast = useToast();
  const [confirmationNo, setConfirmationNo] = useState('WDR-2894');
  const [notes, setNotes] = useState('Clean, no visible damage, documents in glovebox');
  const [fuel, setFuel] = useState(50);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');

  // Looked up as the staff member types a confirmation number - shows the
  // booking's reserved category, plus every car of that category at the
  // pickup hub that's physically free right now. Staff pick one of those
  // as the actual vehicle to hand over - the booking never pinned a
  // specific car until this moment.
  const [booking, setBooking] = useState(null);
  const [availableCars, setAvailableCars] = useState([]);
  const [selectedCarId, setSelectedCarId] = useState(null);
  const [lookingUp, setLookingUp] = useState(false);

  useEffect(() => {
    if (!open) {
      setBooking(null);
      setAvailableCars([]);
      setSelectedCarId(null);
      setError('');
    }
  }, [open]);

  async function handleLookup() {
    if (!confirmationNo.trim()) return;
    setLookingUp(true);
    setError('');
    setSelectedCarId(null);
    try {
      const found = await bookingService.getBookingByConfirmation(confirmationNo);

      // TEMP DEBUG - remove once status field/value is confirmed
      console.log('booking response:', found);

      const status = (found.status || found.bookingStatus || '').toString().toUpperCase();

      if (BLOCKED_STATUSES.includes(status)) {
        setBooking(found);
        setAvailableCars([]);
        setError(`${found.confirmationNo} is ${status.toLowerCase()} — cannot hand over a vehicle for it.`);
        return;
      }

      if (found.car) {
        setBooking(found);
        setAvailableCars([]);
        setError(`${found.confirmationNo} already has a vehicle handed over (${found.car.vehicleNumber}).`);
        return;
      }
      const cars = await staffService.getAvailableCarsForHandover(confirmationNo);
      setBooking(found);
      setAvailableCars(cars);
      if (cars.length > 0) setSelectedCarId(cars[0].carId);
    } catch (err) {
      setBooking(null);
      setAvailableCars([]);
      setError(err.message || 'Could not find that booking');
    } finally {
      setLookingUp(false);
    }
  }

  async function handleDone() {
    if (!selectedCarId) {
      setError('Pick a vehicle to hand over');
      return;
    }
    setBusy(true);
    setError('');
    try {
      await staffService.handoverVehicle(confirmationNo, { carId: selectedCarId, fuelStatus: fuel, notes });
      onClose();
      toast(`Vehicle allotted for ${confirmationNo} — pickup set to today, Happy journey!`);
    } catch (err) {
      setError(err.message || 'Could not process hand-over');
    } finally {
      setBusy(false);
    }
  }

  const bookingBlocked =
    booking && BLOCKED_STATUSES.includes((booking.status || booking.bookingStatus || '').toString().toUpperCase());

  return (
   <Overlay open={open} onClose={onClose}>
  <div className="max-h-[80vh] overflow-y-auto pr-2">
      <h3 className="mb-2.5 font-display text-[19px] font-semibold">Hand over vehicle</h3>

      {error && <p className="mb-3 rounded-lg bg-danger/10 px-3 py-2 text-[12.5px] text-danger">{error}</p>}

      <label className="field mb-3.5 block">
        <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">Booking confirmation no</span>
        <div className="flex gap-2">
          <input
            className="flex-1 rounded-[11px] border border-ink/10 bg-white/70 px-[11px] py-2.5 text-[13.5px] outline-none focus:border-steel"
            value={confirmationNo}
            onChange={(e) => setConfirmationNo(e.target.value)}
          />
          <button
            type="button"
            onClick={handleLookup}
            disabled={lookingUp}
            className="shrink-0 rounded-[11px] border border-ink/10 bg-white px-3.5 text-[12.5px] font-medium text-ink hover:border-ink/25 disabled:opacity-60"
          >
            {lookingUp ? 'Looking up…' : 'Look up'}
          </button>
        </div>
      </label>

      {booking && !booking.car && !bookingBlocked && (
        <div className="mb-3.5 rounded-2xl border border-line bg-paper p-3.5">
          <p className="text-[13px] text-ink/60">
            Reserved category — <b className="text-ink">{booking.carType?.carTypeName}</b> for{' '}
            <b className="text-ink">{booking.customer?.fullName}</b>
          </p>
          <p className="mt-1.5 text-[12.5px] text-ink/50">
            {availableCars.length} {booking.carType?.carTypeName} car{availableCars.length === 1 ? '' : 's'} available at this hub right now — pick one to hand over
          </p>

          {availableCars.length === 0 && (
            <p className="mt-2 rounded-lg bg-danger/10 px-3 py-2 text-[12.5px] text-danger">
              No {booking.carType?.carTypeName} cars are free at this hub right now — check the fleet dashboard or try again shortly.
            </p>
          )}

          {availableCars.length > 0 && (
            <ul className="mt-2.5 flex flex-col gap-1.5">
              {availableCars.map((c) => (
                <li key={c.carId}>
                  <label
                    className={`flex cursor-pointer items-center justify-between gap-3 rounded-xl border px-3 py-2 text-[13px] transition-colors ${
                      selectedCarId === c.carId ? 'border-primary bg-primary/6' : 'border-line bg-white/70 hover:border-ink/25'
                    }`}
                  >
                    <span className="flex items-center gap-2.5">
                      <input
                        type="radio"
                        name="handover-car"
                        className="accent-primary"
                        checked={selectedCarId === c.carId}
                        onChange={() => setSelectedCarId(c.carId)}
                      />
                      <span className="font-mono font-semibold text-ink">{c.vehicleNumber}</span>
                      <span className="text-ink/55">{c.brand} {c.model}</span>
                    </span>
                    <span className="text-[11.5px] text-ink/40">{c.color} · {c.fuelType?.toLowerCase()}</span>
                  </label>
                </li>
              ))}
            </ul>
          )}
        </div>
      )}

      <label className="field mb-3.5 block">
        <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">Car status</span>
        <textarea
          rows={2}
          className="w-full rounded-[11px] border border-ink/10 bg-white/70 px-[11px] py-2.5 text-[13.5px] outline-none focus:border-steel"
          placeholder="Clean, no visible damage, documents in glovebox"
          value={notes}
          onChange={(e) => setNotes(e.target.value)}
        />
      </label>

      <span className="mb-1.5 mt-3.5 block text-[11.5px] font-medium text-ink/50">Fuel status</span>
      <div className="mb-1 flex gap-2">
        {fuelLevels.map((f) => (
          <button
            type="button"
            key={f.value}
            onClick={() => setFuel(f.value)}
            className={`rounded-[10px] border-[1.5px] px-4 py-2 text-[13px] font-medium transition-colors ${
              fuel === f.value ? 'border-ink bg-ink text-white' : 'border-line text-ink'
            }`}
          >
            {f.label}
          </button>
        ))}
      </div>

      <p className="mt-3 text-[13.5px] leading-[1.55] text-ink/55">
        Available = physically at hub, not allotted, no inspection due in the rental window.
      </p>

      <div className="mt-2 flex flex-wrap gap-3">
        <button
          type="button"
          disabled={busy || !selectedCarId || bookingBlocked}
          onClick={handleDone}
          className="flex items-center gap-1.5 rounded-full bg-success px-6.5 py-3 text-[14.5px] font-semibold text-white shadow-[0_4px_14px_rgba(31,157,99,0.3)] transition-[filter] hover:brightness-95 disabled:opacity-60"
        >
          {busy ? 'Processing…' : 'Done'}
        </button>
        <button
          type="button"
          onClick={onClose}
          className="rounded-full border border-line bg-white px-5.5 py-2.5 text-sm font-medium text-ink transition-colors hover:border-ink/30 hover:bg-paper"
        >
          Cancel
        </button>
      </div>
     </div>
</Overlay>
  );
}