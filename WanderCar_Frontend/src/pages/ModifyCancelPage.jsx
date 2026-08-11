import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Overlay from '../components/Overlay';
import * as bookingService from '../api/bookingService';
import * as locationService from '../api/locationService';
import { useBooking } from '../context/BookingContext';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { formatCurrency, formatDateTime } from '../utils/format';
import { MapPinIcon, CalendarIcon, ClockIcon } from '../components/icons/Icons';

const selectClass =
  'w-full rounded-[11px] border border-ink/10 bg-white/70 px-[11px] py-2.5 text-[13.5px] outline-none focus:border-steel disabled:opacity-60';
const selectIconClass = `${selectClass} pl-[30px] appearance-none`;
const fieldIconClass =
  'pointer-events-none absolute left-[9px] top-1/2 -translate-y-1/2 opacity-45';

// Pickup fields lock the instant a real car is handed over (actualHandoverDatetime
// set) — the return leg (drop hub / return datetime) stays editable until the car
// actually comes back. Mirrors the same guard enforced server-side in
// BookingServiceImpl.modifyBooking.
function ChangeLocationModal({ open, booking, onClose, onSaved }) {
  const toast = useToast();
  const locked = Boolean(booking?.actualHandoverDatetime);

  const [states, setStates] = useState([]);
  const [saving, setSaving] = useState(false);
  const [dirty, setDirty] = useState(false);
  const [activeTab, setActiveTab] = useState('pickup');

  const [pickupState, setPickupState] = useState('');
  const [pickupCityId, setPickupCityId] = useState('');
  const [pickupCities, setPickupCities] = useState([]);
  const [pickupHubId, setPickupHubId] = useState('');
  const [pickupDate, setPickupDate] = useState('');
  const [pickupTime, setPickupTime] = useState('10:00');

  const [dropoffState, setDropoffState] = useState('');
  const [dropoffCityId, setDropoffCityId] = useState('');
  const [dropoffCities, setDropoffCities] = useState([]);
  const [dropoffHubId, setDropoffHubId] = useState('');
  const [returnDate, setReturnDate] = useState('');
  const [returnTime, setReturnTime] = useState('10:00');

  useEffect(() => {
    if (!open || !booking) return;
    locationService.getStates().then(setStates);

    const pHub = booking.pickupHub;
    const dHub = booking.dropHub;
    const isLocked = Boolean(booking.actualHandoverDatetime);

    setActiveTab(isLocked ? 'return' : 'pickup');
    setDirty(false);

    setPickupState(pHub?.stateId != null ? String(pHub.stateId) : '');
    setPickupCityId(pHub?.cityId != null ? String(pHub.cityId) : '');
    setPickupHubId(pHub?.hubId != null ? String(pHub.hubId) : '');
    setPickupDate(booking.pickupDatetime?.slice(0, 10) || '');
    setPickupTime(booking.pickupDatetime?.slice(11, 16) || '10:00');

    setDropoffState(dHub?.stateId != null ? String(dHub.stateId) : '');
    setDropoffCityId(dHub?.cityId != null ? String(dHub.cityId) : '');
    setDropoffHubId(dHub?.hubId != null ? String(dHub.hubId) : '');
    setReturnDate(booking.returnDatetime?.slice(0, 10) || '');
    setReturnTime(booking.returnDatetime?.slice(11, 16) || '10:00');
  }, [open, booking]);

  useEffect(() => {
    if (!pickupState) {
      setPickupCities([]);
      return;
    }
    locationService.getCitiesByState(pickupState).then(setPickupCities);
  }, [pickupState]);

  useEffect(() => {
    if (!dropoffState) {
      setDropoffCities([]);
      return;
    }
    locationService.getCitiesByState(dropoffState).then(setDropoffCities);
  }, [dropoffState]);

  const pickupHubOptions = useMemo(
    () => (pickupCityId ? locationService.findHubForCity(pickupCityId) : []),
    [pickupCityId]
  );
  const dropoffHubOptions = useMemo(
    () => (dropoffCityId ? locationService.findHubForCity(dropoffCityId) : []),
    [dropoffCityId]
  );

  if (!open || !booking) return null;

  function handleClose() {
    if (dirty && !window.confirm('Discard your unsaved changes?')) return;
    onClose();
  }

  async function handleSave(e) {
    e.preventDefault();

    const finalDropHubId = dropoffHubId;
    if (!locked && !pickupHubId) {
      toast('Pick a pickup hub');
      return;
    }
    if (!finalDropHubId) {
      toast('Pick a return hub');
      return;
    }
    if (!locked && !pickupDate) {
      toast('Pick a pickup date');
      return;
    }
    if (!returnDate) {
      toast('Pick a return date');
      return;
    }

    const pickupDatetime = `${pickupDate}T${pickupTime}:00`;
    const returnDatetime = `${returnDate}T${returnTime}:00`;
    const effectivePickupDatetime = locked ? booking.pickupDatetime : pickupDatetime;

    if (new Date(returnDatetime) <= new Date(effectivePickupDatetime)) {
      toast('Return must be after pickup');
      return;
    }

    const patch = {
      dropHubId: Number(finalDropHubId),
      returnDatetime,
    };
    if (!locked) {
      patch.pickupHubId = Number(pickupHubId);
      patch.pickupDatetime = pickupDatetime;
    }

    setSaving(true);
    try {
      const updated = await bookingService.modifyBooking(booking.confirmationNo, patch);
      onSaved(updated);
      toast(`Booking ${updated.confirmationNo} updated`);
    } catch (err) {
      toast(err.message || 'Could not update booking');
    } finally {
      setSaving(false);
    }
  }

  return (
    <Overlay open={open} onClose={handleClose} maxWidth={560}>
      <h3 className="mb-2.5 font-display text-[19px] font-semibold">Change location or dates</h3>
      <p className="mb-4.5 text-[13.5px] leading-[1.55] text-ink/55">
        {locked
          ? 'This vehicle has already been handed over, so pickup location and time are locked. You can still change the return location and time.'
          : `Update the pickup and return details for ${booking.confirmationNo}.`}
      </p>

      <form onSubmit={handleSave} className="flex flex-col gap-4.5">
        {!locked && (
          <div className="flex rounded-full bg-ink/6 p-[3px]">
            <button
              type="button"
              onClick={() => setActiveTab('pickup')}
              className={`flex-1 rounded-full py-1.5 text-[13px] font-medium transition-colors ${
                activeTab === 'pickup' ? 'bg-ink text-white' : 'text-ink/55'
              }`}
            >
              Pickup
            </button>
            <button
              type="button"
              onClick={() => setActiveTab('return')}
              className={`flex-1 rounded-full py-1.5 text-[13px] font-medium transition-colors ${
                activeTab === 'return' ? 'bg-ink text-white' : 'text-ink/55'
              }`}
            >
              Return
            </button>
          </div>
        )}

        {!locked && activeTab === 'pickup' && (
          <div>
            <div className="grid grid-cols-2 gap-2.5">
              <label className="field block">
                <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">State</span>
                <select
                  className={selectClass}
                  value={pickupState}
                  onChange={(e) => {
                    setDirty(true);
                    setPickupState(e.target.value);
                    setPickupCityId('');
                    setPickupHubId('');
                  }}
                >
                  <option value="">Select state</option>
                  {states.map((s) => (
                    <option key={s.stateId} value={s.stateId}>
                      {s.stateName}
                    </option>
                  ))}
                </select>
              </label>
              <label className="field block">
                <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">City</span>
                <select
                  disabled={!pickupState}
                  className={selectClass}
                  value={pickupCityId}
                  onChange={(e) => {
                    setDirty(true);
                    setPickupCityId(e.target.value);
                    setPickupHubId('');
                  }}
                >
                  <option value="">Select city</option>
                  {pickupCities.map((c) => (
                    <option key={c.cityId} value={c.cityId}>
                      {c.cityName}
                    </option>
                  ))}
                </select>
              </label>
            </div>
            <label className="field relative mt-2.5 block">
              <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">Hub</span>
              <div className="relative">
                <MapPinIcon className={fieldIconClass} width="13" height="13" />
                <select
                  disabled={!pickupCityId}
                  className={selectIconClass}
                  value={pickupHubId}
                  onChange={(e) => {
                    setDirty(true);
                    setPickupHubId(e.target.value);
                  }}
                >
                  <option value="">Select hub</option>
                  {pickupHubOptions.map((h) => (
                    <option key={h.hubId} value={h.hubId}>
                      {h.hubName}
                    </option>
                  ))}
                </select>
              </div>
              {pickupCityId && pickupHubOptions.length === 0 && (
                <p className="mt-1.5 text-[11.5px] text-danger/75">No hubs available in this city.</p>
              )}
            </label>
            <div className="mt-2.5 grid grid-cols-2 gap-2.5">
              <label className="field block">
                <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">Pickup date</span>
                <div className="relative">
                  <CalendarIcon className={fieldIconClass} width="13" height="13" />
                  <input
                    type="date"
                    className={selectIconClass}
                    value={pickupDate}
                    onChange={(e) => {
                      setDirty(true);
                      setPickupDate(e.target.value);
                    }}
                  />
                </div>
              </label>
              <label className="field block">
                <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">Pickup time</span>
                <div className="relative">
                  <ClockIcon className={fieldIconClass} width="13" height="13" />
                  <input
                    type="time"
                    className={selectIconClass}
                    value={pickupTime}
                    onChange={(e) => {
                      setDirty(true);
                      setPickupTime(e.target.value);
                    }}
                  />
                </div>
              </label>
            </div>
          </div>
        )}

        {(locked || activeTab === 'return') && (
          <div>
            {locked && <p className="mb-2 text-[12px] font-semibold tracking-[0.04em] text-ink/45 uppercase">Return</p>}
            <div className="grid grid-cols-2 gap-2.5">
              <label className="field block">
                <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">State</span>
                <select
                  className={selectClass}
                  value={dropoffState}
                  onChange={(e) => {
                    setDirty(true);
                    setDropoffState(e.target.value);
                    setDropoffCityId('');
                    setDropoffHubId('');
                  }}
                >
                  <option value="">Select state</option>
                  {states.map((s) => (
                    <option key={s.stateId} value={s.stateId}>
                      {s.stateName}
                    </option>
                  ))}
                </select>
              </label>
              <label className="field block">
                <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">City</span>
                <select
                  disabled={!dropoffState}
                  className={selectClass}
                  value={dropoffCityId}
                  onChange={(e) => {
                    setDirty(true);
                    setDropoffCityId(e.target.value);
                    setDropoffHubId('');
                  }}
                >
                  <option value="">Select city</option>
                  {dropoffCities.map((c) => (
                    <option key={c.cityId} value={c.cityId}>
                      {c.cityName}
                    </option>
                  ))}
                </select>
              </label>
            </div>
            <label className="field relative mt-2.5 block">
              <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">Hub</span>
              <div className="relative">
                <MapPinIcon className={fieldIconClass} width="13" height="13" />
                <select
                  disabled={!dropoffCityId}
                  className={selectIconClass}
                  value={dropoffHubId}
                  onChange={(e) => {
                    setDirty(true);
                    setDropoffHubId(e.target.value);
                  }}
                >
                  <option value="">Select hub</option>
                  {dropoffHubOptions.map((h) => (
                    <option key={h.hubId} value={h.hubId}>
                      {h.hubName}
                    </option>
                  ))}
                </select>
              </div>
              {dropoffCityId && dropoffHubOptions.length === 0 && (
                <p className="mt-1.5 text-[11.5px] text-danger/75">No hubs available in this city.</p>
              )}
            </label>
            <div className="mt-2.5 grid grid-cols-2 gap-2.5">
              <label className="field block">
                <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">Return date</span>
                <div className="relative">
                  <CalendarIcon className={fieldIconClass} width="13" height="13" />
                  <input
                    type="date"
                    className={selectIconClass}
                    value={returnDate}
                    min={pickupDate || undefined}
                    onChange={(e) => {
                      setDirty(true);
                      setReturnDate(e.target.value);
                    }}
                  />
                </div>
              </label>
              <label className="field block">
                <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">Return time</span>
                <div className="relative">
                  <ClockIcon className={fieldIconClass} width="13" height="13" />
                  <input
                    type="time"
                    className={selectIconClass}
                    value={returnTime}
                    onChange={(e) => {
                      setDirty(true);
                      setReturnTime(e.target.value);
                    }}
                  />
                </div>
              </label>
            </div>
          </div>
        )}

        <div className="flex flex-wrap gap-3 pt-1">
          <button
            type="submit"
            disabled={saving}
            className="rounded-full bg-primary px-6.5 py-3 text-[14.5px] font-semibold text-white transition-[filter] hover:brightness-95 disabled:opacity-60"
          >
            {saving ? 'Saving…' : 'Save changes'}
          </button>
          <button
            type="button"
            onClick={handleClose}
            className="rounded-full border border-line bg-white px-5.5 py-2.5 text-sm font-medium text-ink transition-colors hover:border-ink/30 hover:bg-paper"
          >
            Cancel
          </button>
        </div>
      </form>
    </Overlay>
  );
}

const statusStyles = {
  CONFIRMED: 'bg-success/12 text-success',
  ONGOING: 'bg-primary/12 text-primary',
  COMPLETED: 'bg-ink/10 text-ink/60',
  CANCELLED: 'bg-danger/12 text-danger',
  PENDING: 'bg-warn/15 text-warn',
};

export default function ModifyCancelPage() {
  const { customer,staff, openAuth }= useAuth(); 
 
  const { beginEditBooking } = useBooking();
  const navigate = useNavigate();
  const toast = useToast();

  // Logged-in customers pick from their own bookings; a guest (or anyone
  // who'd rather not browse a list) can still look one up by confirmation
  // number - both paths land on the same detail view below.
  const [myBookings, setMyBookings] = useState([]);
  const [myBookingsLoading, setMyBookingsLoading] = useState(false);

  const [confirmationNo, setConfirmationNo] = useState(customer ? '' : 'WDR-2894');
  const [booking, setBooking] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [cancelOpen, setCancelOpen] = useState(false);
  const [locationModalOpen, setLocationModalOpen] = useState(false);

  useEffect(() => {
    if (!customer) {
      setMyBookings([]);
      return;
    }
    setMyBookingsLoading(true);
    bookingService
      .getBookingsForCustomer(customer.customerId)
      .then(setMyBookings)
      .finally(() => setMyBookingsLoading(false));
  }, [customer]);

  async function fetchBooking(no) {
    // Allow customer OR staff
    if ((no || confirmationNo) && !customer && !staff) {
      openAuth('login');
      return;
    }

    const target = no ?? confirmationNo;

    setLoading(true);
    setError('');

    try {
      const result = await bookingService.getBookingByConfirmation(target);
      setBooking(result);
      setConfirmationNo(result.confirmationNo);
    } catch (err) {
      setBooking(null);
      setError(err.message || 'Booking not found');
    } finally {
      setLoading(false);
    }
  }

  // "Change vehicle" / "Change add-ons": rather than starting a whole new
  // booking, this hands the vehicle/add-ons page the existing booking's
  // hub, dates, car, and current add-on quantities, and flags it as an
  // edit - see BookingContext.beginEditBooking. Those pages then save the
  // one thing that changed straight back onto this booking and return here.
  function changeVehicle() {
    if (!booking) return;
    beginEditBooking(booking);
    navigate('/vehicles');
  }

  function changeAddons() {
    if (!booking) return;
    const quantities = {};
    (booking.addonLines || []).forEach((line) => {
      quantities[line.addonId] = line.quantity;
    });
    beginEditBooking(booking, quantities);
    navigate('/booking/add-ons');
  }

  async function doCancel() {
    try {
      const result = await bookingService.cancelBooking(confirmationNo);
      setBooking(result);
      setMyBookings((list) => list.map((b) => (b.confirmationNo === result.confirmationNo ? result : b)));
      setCancelOpen(false);
      toast(`Booking ${result.confirmationNo} cancelled — vehicle released back into the fleet`);
    } catch (err) {
      toast(err.message || 'Could not cancel booking');
    }
  }

  return (
    <div className="anim-screen-fade mx-auto max-w-[900px] px-6 pt-14 pb-28 md:px-8">
      <div className="mb-9">
        <h1 className="mb-2 font-display text-[26px] font-semibold tracking-[-0.01em] md:text-[30px]">Modify or cancel a booking</h1>
        <p className="max-w-135 text-[15px] text-ink/55">
          {customer
            ? 'Pick one of your bookings below, or look one up by confirmation number.'
            : "Enter your confirmation number and we'll pull up the record."}
        </p>
      </div>

      {customer && (
        <div className="mb-7">
          {myBookingsLoading ? (
            <p className="text-sm text-ink/50">Loading your bookings…</p>
          ) : myBookings.length ? (
            <div className="flex flex-col gap-2.5">
              {myBookings.map((b) => (
                <button
                  type="button"
                  key={b.confirmationNo}
                  onClick={() => fetchBooking(b.confirmationNo)}
                  className={`flex items-center justify-between gap-4 rounded-2xl border bg-white/70 p-4 text-left transition-colors ${
                    booking?.confirmationNo === b.confirmationNo ? 'border-primary bg-primary/5' : 'border-line hover:border-ink/25'
                  }`}
                   >
                  <div>
                    <p className="font-mono text-[13px] font-semibold text-ink">{b.confirmationNo}</p>
                    <p className="text-[12.5px] text-ink/50">
                      {b.car ? `${b.car.brand} ${b.car.model}` : b.carType?.carTypeName} · {formatDateTime(b.pickupDatetime)}
                    </p>
                  </div>
                  <span className={`rounded-full px-3 py-1.5 text-[11px] font-bold tracking-[0.06em] uppercase ${statusStyles[b.bookingStatus] || ''}`}>
                    {b.bookingStatus}
                  </span>
                </button>
              ))}
            </div>
          ) : (
            <p className="text-sm text-ink/50">You don't have any bookings yet.</p>
          )}
        </div>
      )}
      <div className="max-w-130 rounded-[22px] border border-line bg-white/70 p-6.5 shadow-[0_6px_20px_rgba(18,21,28,0.05)]">
        <label className="field block">
          <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">
            {customer ? 'Or look up by confirmation no' : 'Booking confirmation no'}
          </span>
          <div className="mt-1.5 flex gap-2.5">
            <input
              type="text"
              placeholder="WDR-2894"
              value={confirmationNo}
              onChange={(e) => setConfirmationNo(e.target.value)}
              className="flex-1 rounded-[11px] border border-ink/10 bg-white/70 px-[11px] py-2.5 text-[13.5px] outline-none focus:border-steel"
            />
            <button
              type="button"
              onClick={() => fetchBooking()}
              disabled={loading}
              className="flex items-center gap-1.5 rounded-full bg-primary px-6.5 py-3 text-[14.5px] font-semibold text-white shadow-[0_4px_14px_rgba(47,111,237,0.3)] transition-[filter] hover:brightness-95 disabled:opacity-60"
            >
              {loading ? 'Fetching…' : 'Fetch record'}
            </button>
          </div>
        </label>
      </div>

      {error && <p className="mt-4 max-w-130 rounded-lg bg-danger/10 px-3 py-2 text-[12.5px] text-danger">{error}</p>}

      {booking && (
        <div className="mt-5.5">
          <div className="rounded-[22px] border border-line bg-white/70 p-6.5 shadow-[0_6px_20px_rgba(18,21,28,0.05)]">
            <div className="mb-4 flex items-center justify-between">
              <span className="font-mono text-lg font-semibold">{booking.confirmationNo}</span>
              <span className={`rounded-full px-3 py-1.5 text-[11.5px] font-bold tracking-[0.06em] uppercase ${statusStyles[booking.bookingStatus] || ''}`}>
                {booking.bookingStatus}
              </span>
            </div>
            <table className="w-full border-collapse text-[13.5px]">
              <tbody>
                <tr>
                  <td className="w-40 py-2.5 text-ink/50">Vehicle</td>
                  <td className="py-2.5">
                    {booking.car
                      ? `${booking.carType?.carTypeName} · ${booking.car.brand} ${booking.car.model} (${booking.car.vehicleNumber})`
                      : `${booking.carType?.carTypeName} — assigned at pickup`}
                  </td>
                </tr>
                <tr>
                  <td className="py-2.5 text-ink/50">Pickup</td>
                  <td className="py-2.5">
                    {booking.pickupHub?.hubName} — {formatDateTime(booking.pickupDatetime)}
                  </td>
                </tr>
                <tr>
                  <td className="py-2.5 text-ink/50">Return</td>
                  <td className="py-2.5">
                    {booking.dropHub?.hubName} — {formatDateTime(booking.returnDatetime)}
                  </td>
                </tr>
                <tr>
                  <td className="py-2.5 text-ink/50">Renter</td>
                  <td className="py-2.5">
                    {booking.customer?.fullName} — {booking.customer?.email}
                  </td>
                </tr>
                <tr>
                  <td className="py-2.5 text-ink/50">Add-ons</td>
                  <td className="py-2.5">
                    {booking.addonLines?.length
                      ? booking.addonLines.map((l) => `${l.addon?.addonName} ×${l.quantity}`).join(', ')
                      : 'None'}
                  </td>
                </tr>
                <tr>
                  <td className="py-2.5 text-ink/50">Estimated total</td>
                  <td className="py-2.5 font-semibold">{formatCurrency(booking.estimatedAmount)}</td>
                </tr>
              </tbody>
            </table>

            {booking.bookingStatus !== 'CANCELLED' && booking.bookingStatus !== 'COMPLETED' && (
              <div className="mt-6 flex flex-wrap gap-3">
                {!booking.car && (
                  <button
                    type="button"
                    onClick={changeVehicle}
                    className="rounded-full border border-line bg-white px-5.5 py-2.5 text-sm font-medium text-ink transition-colors hover:border-ink/30 hover:bg-paper"
                  >
                    Change category
                  </button>
                )}
                {!booking.car && (
                  <button
                    type="button"
                    onClick={changeAddons}
                    className="rounded-full border border-line bg-white px-5.5 py-2.5 text-sm font-medium text-ink transition-colors hover:border-ink/30 hover:bg-paper"
                  >
                    Change add-ons
                  </button>
                )}
                <button
                  type="button"
                  onClick={() => setLocationModalOpen(true)}
                  className="rounded-full border border-line bg-white px-5.5 py-2.5 text-sm font-medium text-ink transition-colors hover:border-ink/30 hover:bg-paper"
                >
                  Change location or dates
                </button>
                {booking.bookingStatus !== 'ONGOING' && (
 			 <button
    			type="button"
   		 onClick={() => setCancelOpen(true)}
   		 className="flex items-center gap-1.5 rounded-full border border-danger/30 bg-white px-5.5 py-2.5 text-sm font-medium text-		danger transition-colors hover:bg-danger/6"
  >
    Cancel booking
  </button>
)}
              </div>
            )}
          </div>
        </div>
      )}

      <Overlay open={cancelOpen} onClose={() => setCancelOpen(false)}>
        <h3 className="mb-2.5 font-display text-[19px] font-semibold">Cancel this booking?</h3>
        <p className="mb-4.5 text-[13.5px] leading-[1.55] text-ink/55">
          This releases the vehicle back into the fleet and can't be undone.
        </p>
        <div className="flex flex-wrap gap-3">
          <button
            type="button"
            onClick={doCancel}
            className="rounded-full bg-danger px-6.5 py-3 text-[14.5px] font-semibold text-white transition-[filter] hover:brightness-95"
          >
            Yes, cancel booking
          </button>
          <button
            type="button"
            onClick={() => setCancelOpen(false)}
            className="rounded-full border border-line bg-white px-5.5 py-2.5 text-sm font-medium text-ink transition-colors hover:border-ink/30 hover:bg-paper"
          >
            Keep booking
          </button>
        </div>
      </Overlay>

      <ChangeLocationModal
        open={locationModalOpen}
        booking={booking}
        onClose={() => setLocationModalOpen(false)}
        onSaved={(updated) => {
          setBooking(updated);
          setMyBookings((list) => list.map((b) => (b.confirmationNo === updated.confirmationNo ? updated : b)));
          setLocationModalOpen(false);
        }}
      />
    </div>
  );
}
