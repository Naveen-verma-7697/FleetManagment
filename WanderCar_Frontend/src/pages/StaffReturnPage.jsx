import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Overlay from '../components/Overlay';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import * as bookingService from '../api/bookingService';
import * as staffService from '../api/staffService';
import { daysBetween } from '../context/BookingContext';
import { calculateRentalAmount, describeRentalDuration, calculateFuelCharge, calculateAddonLineAmount } from '../utils/pricing';
import { formatCurrency, formatDateTime } from '../utils/format';

const fuelLevels = [
  { label: '¼', value: 25 },
  { label: '½', value: 50 },
  { label: '¾', value: 75 },
  { label: 'Full', value: 100 },
];

function fuelLabel(value) {
  return fuelLevels.find((f) => f.value === value)?.label ?? (value != null ? `${value}%` : '—');
}

const paymentTypes = [
  { label: 'Cash', value: 'CASH' },
  { label: 'UPI', value: 'UPI' },
  { label: 'Card', value: 'CARD' },
  { label: 'Net banking', value: 'NET_BANKING' },
  { label: 'Not collected yet', value: null },
];

export default function StaffReturnPage() {
  const { staff } = useAuth();
  const navigate = useNavigate();
  const toast = useToast();

  const [confirmationNo, setConfirmationNo] = useState('WDR-2894');
  const [booking, setBooking] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const [extraMiles, setExtraMiles] = useState(0);
  const [extraChargeAmount, setExtraChargeAmount] = useState(0);
  const [damageNotes, setDamageNotes] = useState('');
  const [fuel, setFuel] = useState(25);
  const [paymentType, setPaymentType] = useState('CASH');
  const [modalOpen, setModalOpen] = useState(false);
  const [invoice, setInvoice] = useState(null);
  const [now, setNow] = useState(() => new Date());

  useEffect(() => {
    if (!staff) {
      navigate('/');
      return;
    }
    fetchBooking('WonderCar-28');
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [staff]);

  // The billed amount depends on "right now" (the actual return moment),
  // so keep it live rather than freezing it at the first render — a staff
  // member could sit on this screen across a day boundary before hitting
  // "Process return".
  useEffect(() => {
    const id = setInterval(() => setNow(new Date()), 30000);
    return () => clearInterval(id);
  }, []);

  // Default the return fuel picker to whatever the car was handed over
  // with — most returns come back at the same level, and this way the
  // fuel charge starts at ₹0 instead of silently assuming a near-empty
  // tank (the old hardcoded default of ¼) every time a booking loads.
  useEffect(() => {
    if (booking?.handoverFuelLevel != null) {
      setFuel(booking.handoverFuelLevel);
    }
  }, [booking?.confirmationNo, booking?.handoverFuelLevel]);

  async function fetchBooking(no) {
    setLoading(true);
    setError('');
    setInvoice(null);
    try {
      const result = await bookingService.getBookingByConfirmation(no ?? confirmationNo);
      setBooking(result);
    } catch (err) {
      setBooking(null);
      setError(err.message || 'Booking not found');
    } finally {
      setLoading(false);
    }
  }

  if (!staff) return null;

  // Bill against the actual hand-over time through right now, not the
  // originally booked pickup/return window — this preview is what
  // processReturn on the backend will also charge. Once the return has
  // actually been processed, freeze on the invoice's own numbers instead
  // of continuing to recompute against a live, ever-advancing "now" —
  // otherwise the row keeps drifting away from the amount that was
  // actually billed (and already in the PDF/email) every time the 30s
  // clock ticks.
  const handoverTime = invoice ? invoice.handoverDatetime : booking?.actualHandoverDatetime || booking?.pickupDatetime;
  const returnTime = invoice ? invoice.returnDatetime : now;
  const days = invoice ? invoice.days : booking ? daysBetween(handoverTime, returnTime) : 0;
  const rentalAmount = invoice ? invoice.rentalAmount : booking ? calculateRentalAmount(booking.carType, days) : 0;
  const addonAmount = invoice ? invoice.addonAmount : booking ? booking.addonLines.reduce((s, l) => s + calculateAddonLineAmount(l, days), 0) : 0;
  const fuelCharge = invoice ? invoice.fuelCharge : calculateFuelCharge(booking?.handoverFuelLevel, fuel);
  const previewTotal = rentalAmount + addonAmount + fuelCharge + Number(extraChargeAmount || 0);

  async function handleProcessReturn() {
    try {
      const result = await staffService.processReturn(confirmationNo, {
        extraMiles,
        extraChargeAmount,
        damageNotes,
        fuelStatus: fuel,
        paymentType,
      });
      setInvoice(result);
      setModalOpen(false);
      toast('Return recorded — invoice emailed, vehicle available again');
      fetchBooking(confirmationNo);
    } catch (err) {
      toast(err.message || 'Could not process return');
    }
  }

  return (
    <div className="anim-screen-fade mx-auto max-w-[900px] px-6 pt-14 pb-28 md:px-8">
      <div className="mb-9">
        <h1 className="mb-2 font-display text-[26px] font-semibold tracking-[-0.01em] md:text-[30px]">Process return</h1>
        <p className="max-w-135 text-[15px] text-ink/55">
          Look up the booking's confirmation number to pull up its invoice preview.
        </p>
      </div>

      <div className="mb-6 flex max-w-130 gap-2.5">
        <input
          type="text"
          value={confirmationNo}
          onChange={(e) => setConfirmationNo(e.target.value)}
          placeholder="WonderCar-2894"
          className="flex-1 rounded-[11px] border border-ink/10 bg-white/70 px-[11px] py-2.5 text-[13.5px] outline-none focus:border-steel"
        />
        <button
          type="button"
          onClick={() => fetchBooking()}
          disabled={loading}
          className="rounded-full bg-primary px-5.5 py-2.5 text-sm font-semibold text-white shadow-[0_4px_14px_rgba(47,111,237,0.3)] transition-[filter] hover:brightness-95 disabled:opacity-60"
        >
          {loading ? 'Fetching…' : 'Fetch record'}
        </button>
      </div>

      {error && <p className="mb-4 max-w-130 rounded-lg bg-danger/10 px-3 py-2 text-[12.5px] text-danger">{error}</p>}

      {booking && (
        <div className="rounded-[22px] border border-line bg-white/70 p-6.5 shadow-[0_6px_20px_rgba(18,21,28,0.05)]">
          <p className="mb-4 text-[13.5px] text-ink/60">
            Invoice preview for booking <b className="font-mono text-ink">{booking.confirmationNo}</b> — vehicle{' '}
            <b className="font-mono text-ink">{booking.car?.vehicleNumber || 'not assigned yet'}</b> ({booking.bookingStatus})
          </p>

          {booking.car && (
            <p className="mb-4 text-[12.5px] text-ink/50">
              Handed over <b className="text-ink">{formatDateTime(handoverTime)}</b> ·{' '}
              {invoice ? 'returned' : 'returning as of'}{' '}
              <b className="text-ink">{formatDateTime(returnTime)}</b> — {days} day{days > 1 ? 's' : ''} billed
            </p>
          )}

          <table className="w-full border-collapse text-[13.5px]">
            <thead>
              <tr>
                <th className="border-b-2 border-ink px-1.5 py-2.5 text-left text-[11px] tracking-[0.08em] text-ink/45 uppercase">Item</th>
                <th className="border-b-2 border-ink px-1.5 py-2.5 text-left text-[11px] tracking-[0.08em] text-ink/45 uppercase">Detail</th>
                <th className="border-b-2 border-ink px-1.5 py-2.5 text-right text-[11px] tracking-[0.08em] text-ink/45 uppercase">Amount</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td className="border-b border-line px-1.5 py-2.5">Base fare</td>
                <td className="border-b border-line px-1.5 py-2.5">
                  {booking.carType?.carTypeName}
                  {booking.car ? ` · ${booking.car.brand} ${booking.car.model}` : ' · not yet handed over'} · {describeRentalDuration(booking.carType, days)}
                </td>
                <td className="border-b border-line px-1.5 py-2.5 text-right font-mono">{formatCurrency(rentalAmount)}</td>
              </tr>
              {booking.addonLines.map((l) => (
                <tr key={l.bookingDetailId}>
                  <td className="border-b border-line px-1.5 py-2.5">Add-on</td>
                  <td className="border-b border-line px-1.5 py-2.5">
                    {l.addon?.addonName}, {l.quantity} × {formatCurrency(l.addonRate)}
                  </td>
                  <td className="border-b border-line px-1.5 py-2.5 text-right font-mono">{formatCurrency(calculateAddonLineAmount(l, days))}</td>
                </tr>
              ))}
              {booking.car && (
                <tr>
                  <td className="border-b border-line px-1.5 py-2.5">Fuel</td>
                  <td className="border-b border-line px-1.5 py-2.5">
                    Handed over {fuelLabel(invoice ? invoice.handoverFuelLevel : booking.handoverFuelLevel)} · returned{' '}
                    {fuelLabel(invoice ? invoice.returnFuelLevel : fuel)}
                    {fuelCharge > 0 ? ' · shortfall charge' : ''}
                  </td>
                  <td className="border-b border-line px-1.5 py-2.5 text-right font-mono">{formatCurrency(fuelCharge)}</td>
                </tr>
              )}
              <tr>
  <td className="border-b border-line px-1.5 py-2.5">
    Extra Charges
  </td>

  <td className="border-b border-line px-1.5 py-2.5">
    <input
      type="number"
      min="0"
      value={extraChargeAmount}
      onChange={(e) => setExtraChargeAmount(Number(e.target.value))}
      placeholder="Enter amount"
      className="w-32 rounded-md border border-gray-300 px-2 py-1 text-sm outline-none focus:border-primary"
    />
  </td>

  <td className="border-b border-line px-1.5 py-2.5 text-right font-mono">
    {formatCurrency(extraChargeAmount)}
  </td>
</tr>
              <tr>
                <td colSpan={2} className="px-1.5 py-2.5">
                  <b>Total {invoice ? `(Invoice INV-${invoice.invoiceId})` : ''}</b>
                </td>
                <td className="px-1.5 py-2.5 text-right font-mono">
                  <b>{formatCurrency(invoice ? invoice.totalAmount : previewTotal)}</b>
                </td>
              </tr>
            </tbody>
          </table>

          {invoice && (
            <p className="mt-3 text-[12.5px] text-ink/50">Payment {invoice.paymentStatus?.toLowerCase()}</p>
          )}

          <div className="mt-6 flex flex-wrap gap-3">
            <button
              type="button"
              disabled={booking.bookingStatus === 'COMPLETED' || !booking.car}
              onClick={() => setModalOpen(true)}
              className="flex items-center gap-1.5 rounded-full bg-success px-6.5 py-3 text-[14.5px] font-semibold text-white shadow-[0_4px_14px_rgba(31,157,99,0.3)] transition-[filter] hover:brightness-95 disabled:opacity-50"
            >
              {booking.bookingStatus === 'COMPLETED'
                ? 'Already returned'
                : !booking.car
                  ? 'Awaiting hand-over'
                  : 'Process return'}
            </button>
            <button
              type="button"
              onClick={() => toast('Invoice PDF emailed to the customer')}
              className="rounded-full border border-line bg-white px-5.5 py-2.5 text-sm font-medium text-ink transition-colors hover:border-ink/30 hover:bg-paper"
            >
              Email invoice
            </button>
            <button
              type="button"
              onClick={() => toast('Sent to printer')}
              className="rounded-full border border-line bg-white px-5.5 py-2.5 text-sm font-medium text-ink transition-colors hover:border-ink/30 hover:bg-paper"
            >
              Print invoice
            </button>
          </div>
        </div>
      )}

      <Overlay open={modalOpen} onClose={() => setModalOpen(false)}>
        <h3 className="mb-2.5 font-display text-[19px] font-semibold">Confirm return</h3>
        <label className="field mb-3.5 block">
          <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">Vehicle</span>
          <input
            type="text"
            readOnly
            value={`${booking?.car?.vehicleNumber || ''} — ${booking?.car?.brand || ''} ${booking?.car?.model || ''}`}
            className="w-full rounded-[11px] border border-ink/10 bg-[#f2f2ef] px-[11px] py-2.5 text-[13.5px]"
          />
        </label>
        <label className="field mb-3.5 block">
          <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">Damage notes</span>
          <textarea
            rows={2}
            value={damageNotes}
            onChange={(e) => setDamageNotes(e.target.value)}
            placeholder="Minor scratch, rear-left bumper"
            className="w-full rounded-[11px] border border-ink/10 bg-white/70 px-[11px] py-2.5 text-[13.5px] outline-none focus:border-steel"
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
        <span className="mb-1.5 mt-3.5 block text-[11.5px] font-medium text-ink/50">Payment method</span>
        <div className="mb-1 flex flex-wrap gap-2">
          {paymentTypes.map((p) => (
            <button
              type="button"
              key={p.label}
              onClick={() => setPaymentType(p.value)}
              className={`rounded-[10px] border-[1.5px] px-4 py-2 text-[13px] font-medium transition-colors ${
                paymentType === p.value ? 'border-ink bg-ink text-white' : 'border-line text-ink'
              }`}
            >
              {p.label}
            </button>
          ))}
        </div>
        <div className="mt-4 flex flex-wrap gap-3">
          <button
            type="button"
            onClick={handleProcessReturn}
            className="rounded-full bg-success px-6.5 py-3 text-[14.5px] font-semibold text-white shadow-[0_4px_14px_rgba(31,157,99,0.3)] transition-[filter] hover:brightness-95"
          >
            Done
          </button>
          <button
            type="button"
            onClick={() => setModalOpen(false)}
            className="rounded-full border border-line bg-white px-5.5 py-2.5 text-sm font-medium text-ink transition-colors hover:border-ink/30 hover:bg-paper"
          >
            Cancel
          </button>
        </div>
      </Overlay>
    </div>
  );
}
