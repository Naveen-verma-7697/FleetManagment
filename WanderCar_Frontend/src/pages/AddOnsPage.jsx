import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import StepBar from '../components/StepBar';
import SideSummary from '../components/booking/SideSummary';
import * as addonService from '../api/addonService';
import * as bookingService from '../api/bookingService';
import { useBooking } from '../context/BookingContext';
import { useToast } from '../context/ToastContext';
import { formatCurrency } from '../utils/format';

const CHILD_SEAT_MAX = 3;

export default function AddOnsPage() {
  const { draft, addons, setAddonsCatalog, setAddonQuantity, selectedAddonLines, editingBooking, clearEditingBooking } = useBooking();
  const navigate = useNavigate();
  const toast = useToast();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (!draft.carType) {
      navigate('/vehicles');
      return;
    }
    setLoading(true);
    addonService.getAddons().then((list) => {
      setAddonsCatalog(list);
      setLoading(false);
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [draft.carType, navigate]);

  function toggleAddon(addonId, checked) {
    setAddonQuantity(addonId, checked ? 1 : 0);
  }

  function stepQuantity(addonId, currentQuantity, delta) {
    const next = Math.min(CHILD_SEAT_MAX, Math.max(0, currentQuantity + delta));
    setAddonQuantity(addonId, next);
  }

  async function handleContinue() {
    if (!editingBooking) {
      navigate('/booking/details');
      return;
    }
    setSaving(true);
    try {
      await bookingService.modifyBooking(editingBooking.confirmationNo, {
        addons: selectedAddonLines.map((l) => ({ addonId: l.addon.addonId, quantity: l.quantity })),
      });
      toast(`Add-ons updated for ${editingBooking.confirmationNo}`);
      clearEditingBooking();
      navigate('/modify');
    } catch (err) {
      toast(err.message || 'Could not update the add-ons');
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="anim-screen-fade mx-auto max-w-[1120px] px-6 pt-14 pb-28 md:px-8">
      <button type="button" onClick={() => navigate(editingBooking ? '/modify' : '/vehicles')} className="mb-5.5 flex items-center gap-1.5 text-[13.5px] font-medium text-ink/55 hover:text-ink">
        &larr; {editingBooking ? `Back to ${editingBooking.confirmationNo}` : 'Back to car choice'}
      </button>
      <StepBar current={3} />

      <div className="grid items-start gap-7 md:grid-cols-[1fr_300px]">
        <div>
          <div className="mb-9">
            <h1 className="mb-2 font-display text-[26px] font-semibold tracking-[-0.01em] md:text-[30px]">
              {editingBooking ? `Change add-ons for ${editingBooking.confirmationNo}` : 'Rental add-ons'}
            </h1>
            <p className="max-w-135 text-[15px] text-ink/55">
              Optional extras, billed per day and added to your total. Skip anything you don't need.
            </p>
          </div>

          {loading ? (
            <p className="text-sm text-ink/50">Loading add-ons…</p>
          ) : (
            <div className="flex flex-col gap-3">
              {addons.map((addon) => {
                const quantity = draft.addonQuantities[addon.addonId] || 0;
                const checked = quantity > 0;
                // Child Seat is the only add-on that makes sense in multiples
                // (0-3) - GPS Navigation and WiFi Hotspot are one-per-booking
                // toggles, so they get a plain checkbox instead of a stepper.
                const isSteppable = addon.addonName === 'Child Seat';

                return (
                  <label
                    key={addon.addonId}
                    className={`flex cursor-pointer items-center gap-3.5 rounded-2xl border p-4.5 transition-colors ${
                      checked ? 'border-primary bg-primary/5' : 'border-line bg-[rgba(212,215,220,0.7)]'
                    }`}
                  >
                    <input
                      type="checkbox"
                      className="h-[19px] w-[19px] shrink-0 accent-primary"
                      checked={checked}
                      onChange={(e) => toggleAddon(addon.addonId, e.target.checked)}
                    />
                    <div className="flex-1">
                      <b className="block text-[14.5px] font-semibold">{addon.addonName}</b>
                      <span className="text-[12.5px] text-ink/50">{addon.description}</span>
                    </div>
                    {checked && isSteppable && (
                      <div
                        className="ml-2 flex items-center gap-2.5 rounded-full border border-ink/10 bg-white px-1.5 py-1"
                        onClick={(e) => e.preventDefault()}
                      >
                        <button
                          type="button"
                          disabled={quantity <= 0}
                          onClick={() => stepQuantity(addon.addonId, quantity, -1)}
                          className="grid h-6 w-6 place-items-center rounded-full text-ink/60 transition-colors hover:bg-ink/6 disabled:opacity-30"
                          aria-label={`Decrease ${addon.addonName} quantity`}
                        >
                          −
                        </button>
                        <span className="w-4 text-center font-mono text-[13px] text-ink">{quantity}</span>
                        <button
                          type="button"
                          disabled={quantity >= CHILD_SEAT_MAX}
                          onClick={() => stepQuantity(addon.addonId, quantity, 1)}
                          className="grid h-6 w-6 place-items-center rounded-full text-ink/60 transition-colors hover:bg-ink/6 disabled:opacity-30"
                          aria-label={`Increase ${addon.addonName} quantity`}
                        >
                          +
                        </button>
                      </div>
                    )}
                    <span className="font-mono text-[13.5px] whitespace-nowrap text-ink">{formatCurrency(addon.dailyRate)}/day</span>
                  </label>
                );
              })}
            </div>
          )}

          <div className="mt-7 flex flex-wrap gap-3">
            <button
              type="button"
              onClick={handleContinue}
              disabled={saving}
              className="flex items-center gap-1.5 rounded-full bg-primary px-6.5 py-3 text-[14.5px] font-semibold text-white shadow-[0_4px_14px_rgba(47,111,237,0.3)] transition-[filter] hover:brightness-95 disabled:opacity-60"
            >
              {editingBooking ? (saving ? 'Saving…' : 'Save add-ons') : 'Continue booking'}
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

        <SideSummary />
      </div>
    </div>
  );
}
