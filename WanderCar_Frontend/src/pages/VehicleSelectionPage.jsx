import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import StepBar from '../components/StepBar';
import VehicleArt from '../components/VehicleArt';
import * as vehicleService from '../api/vehicleService';
import * as bookingService from '../api/bookingService';
import { useBooking } from '../context/BookingContext';
import { useToast } from '../context/ToastContext';
import { formatCurrency } from '../utils/format';

export default function VehicleSelectionPage() {
  const { draft, selectCarType, pickupDatetime, returnDatetime, editingBooking, clearEditingBooking } = useBooking();
  const navigate = useNavigate();
  const toast = useToast();
  const [carTypes, setCarTypes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (!draft.search.pickupHubId) {
      navigate('/');
      return;
    }
    setLoading(true);
    // Every category comes back, always - even one with zero cars left for
    // these dates at this hub. A type with availableCount 0 is shown
    // disabled with an "Unavailable" badge instead of quietly vanishing
    // from the list, so the customer can see it exists and why it can't
    // be picked right now.
    vehicleService
      .getCarTypes({ hubId: draft.search.pickupHubId, pickupDatetime, returnDatetime })
      .then(setCarTypes)
      .finally(() => setLoading(false));
  }, [draft.search.pickupHubId, pickupDatetime, returnDatetime, navigate]);

  async function handleContinue() {
    if (!draft.carType) {
      toast('Pick a car category to continue');
      return;
    }
    if (editingBooking) {
      setSaving(true);
      try {
        // Carries whatever hub/date changes were made upstream (the
        // "change location or dates" flow re-picks these on the search
        // and hub-selection pages before landing here) along with the
        // category, in one patch. For the "change category only" flow
        // these are just the booking's existing values passed back
        // unchanged, so it's a no-op for them.
        await bookingService.modifyBooking(editingBooking.confirmationNo, {
          pickupHubId: draft.search.pickupHubId,
          dropHubId: draft.search.dropoffHubId || draft.search.pickupHubId,
          pickupDatetime,
          returnDatetime,
          carTypeId: draft.carType.carTypeId,
        });
        toast(`Booking ${editingBooking.confirmationNo} updated`);
        clearEditingBooking();
        navigate('/modify');
      } catch (err) {
        toast(err.message || 'Could not update the booking');
      } finally {
        setSaving(false);
      }
      return;
    }
    navigate('/booking/add-ons');
  }

  return (
    <div className="anim-screen-fade mx-auto max-w-[1120px] px-6 pt-14 pb-28 md:px-8">
      <button type="button" onClick={() => navigate(editingBooking ? '/modify' : '/')} className="mb-5.5 flex items-center gap-1.5 text-[13.5px] font-medium text-ink/55 hover:text-ink">
        &larr; {editingBooking ? `Back to ${editingBooking.confirmationNo}` : 'Back to search'}
      </button>
      <StepBar current={2} />
      <div className="mb-9">
        <h1 className="mb-2 font-display text-[26px] font-semibold tracking-[-0.01em] md:text-[30px]">
          {editingBooking ? `Update booking ${editingBooking.confirmationNo}` : 'Choose your car category'}
        </h1>
        <p className="max-w-135 text-[15px] text-ink/55">
          Reserve a category now — base rates shown per day, week, and month. The exact vehicle within it is
          assigned by our staff when you arrive for pickup.
        </p>
      </div>

      {loading ? (
        <p className="text-sm text-ink/50">Loading categories…</p>
      ) : (
        <div className="flex flex-col gap-3.5">
          {carTypes.map((carType) => {
            const selected = draft.carType?.carTypeId === carType.carTypeId;
            const disabled = (carType.availableCount ?? 0) <= 0;
            return (
              <button
                type="button"
                key={carType.carTypeId}
                disabled={disabled}
                onClick={() => selectCarType(carType)}
                className={`relative grid grid-cols-1 items-center gap-5 rounded-[20px] border bg-white/70 p-4.5 text-left shadow-[0_6px_20px_rgba(18,21,28,0.05)] transition-all duration-300 sm:grid-cols-[96px_1fr_auto_auto] ${
                  disabled
                    ? 'cursor-not-allowed opacity-55'
                    : selected
                      ? 'border-primary bg-primary/5 shadow-lg ring-2 ring-primary/20'
                      : 'cursor-pointer border-line hover:-translate-y-1 hover:border-primary hover:shadow-xl'
                }`}
              >
                {selected && (
                  <span className="absolute right-4 top-4 rounded-full bg-primary px-3 py-1 text-[11px] font-medium text-white">
                    Selected
                  </span>
                )}
                <VehicleArt carTypeName={carType.carTypeName} className="w-full sm:w-24" />

                <div>
                  <p className="font-mono text-[10.5px] tracking-[0.14em] text-steel uppercase">Category</p>
                  <p className="mt-0.75 mb-0.5 font-display text-[16.5px] font-semibold">{carType.carTypeName}</p>
                  <p className="text-[12.5px] text-ink/50">
                    {disabled
                      ? 'No cars of this category left for these dates'
                      : `${carType.availableCount} car${carType.availableCount === 1 ? '' : 's'} available at this hub`}
                  </p>
                </div>

                <div className="flex gap-4.5 justify-self-start sm:gap-5">
                  <div className="text-[11px] tracking-[0.06em] text-ink/40 uppercase">
                    Daily
                    <b className="mt-0.5 block font-display text-[14.5px] tracking-normal text-ink normal-case">
                      {formatCurrency(carType.dailyRate)}
                    </b>
                  </div>
                  <div className="text-[11px] tracking-[0.06em] text-ink/40 uppercase">
                    Weekly
                    <b className="mt-0.5 block font-display text-[14.5px] tracking-normal text-ink normal-case">
                      {formatCurrency(carType.weeklyRate)}
                    </b>
                  </div>
                  <div className="text-[11px] tracking-[0.06em] text-ink/40 uppercase">
                    Monthly
                    <b className="mt-0.5 block font-display text-[14.5px] tracking-normal text-ink normal-case">
                      {formatCurrency(carType.monthlyRate)}
                    </b>
                  </div>
                </div>

                <div className="justify-self-start sm:justify-self-end">
                  {disabled && (
                    <span className="rounded-full bg-danger/9 px-3 py-1.5 text-[11.5px] font-semibold tracking-[0.08em] text-danger uppercase">
                      Unavailable
                    </span>
                  )}
                </div>
              </button>
            );
          })}
          {carTypes.length === 0 && <p className="text-sm text-ink/50">No car categories found at this hub for your dates.</p>}
        </div>
      )}

      <div className="mt-7 flex flex-wrap gap-3">
        <button
          type="button"
          onClick={handleContinue}
          disabled={saving}
          className="flex items-center gap-1.5 rounded-full bg-primary px-6.5 py-3 text-[14.5px] font-semibold text-white shadow-[0_4px_14px_rgba(47,111,237,0.3)] transition-[filter] hover:brightness-95 disabled:opacity-60"
        >
          {editingBooking ? (saving ? 'Saving…' : 'Save changes') : 'Continue booking'}
        </button>
        <button
          type="button"
          onClick={() => navigate(editingBooking ? '/modify' : '/')}
          className="rounded-full border border-line bg-white px-5.5 py-2.5 text-sm font-medium text-ink transition-colors hover:border-ink/30 hover:bg-paper"
        >
          Cancel
        </button>
      </div>
    </div>
  );
}
