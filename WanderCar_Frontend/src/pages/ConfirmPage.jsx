import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import StepBar from '../components/StepBar';
import VehicleArt from '../components/VehicleArt';
import { useBooking } from '../context/BookingContext';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import * as customerService from '../api/customerService';
import * as bookingService from '../api/bookingService';
import { getHubById } from '../api/locationService';
import { formatCurrency } from '../utils/format';

function Row({ label, children }) {
  return (
    <tr>
      <td className="w-42.5 py-3 align-top text-[13.5px] text-ink/50">{label}</td>
      <td className="py-3 align-top text-[13.5px]">{children}</td>
    </tr>
  );
}

function SectionLabel({ children }) {
  return <p className="mb-2.5 font-mono text-[11px] tracking-[0.2em] text-steel uppercase">{children}</p>;
}

export default function ConfirmPage() {
  const { draft, selectedAddonLines, totals, setConfirmedBooking } = useBooking();
  const { customer: loggedInCustomer } = useAuth();
  const navigate = useNavigate();
  const toast = useToast();
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    if (!draft.carType) {
      navigate('/vehicles');
    } else if (!draft.customer.email) {
      navigate('/booking/details');
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [draft.carType, draft.customer.email]);

  if (!draft.carType) return null;

  const pickupHub = getHubById(draft.search.pickupHubId);
  const dropHub = getHubById(draft.search.dropoffHubId || draft.search.pickupHubId);
  const c = draft.customer;

  async function handleConfirm() {
    setBusy(true);
    try {
      const customerRecord = loggedInCustomer
        ? await customerService.updateCustomer({
            customerId: loggedInCustomer.customerId,
            fullName: `${c.firstName} ${c.lastName}`.trim(),
            phone: c.phone,
            address1: c.address1,
            pincode: c.zip,
            drivingLicenseNo: c.drivingLicenseNo,
            // Omitted (undefined) rather than sent as '' when nothing
            // changed - updateCustomer is a partial patch, and an empty
            // string would wipe out a document already on file.
            govtIdDocument: c.govtIdDocument || undefined,
            govtIdDocumentName: c.govtIdDocument ? c.govtIdDocumentName : undefined,
            govtIdDocumentType: c.govtIdDocument ? c.govtIdDocumentType : undefined,
          })
        : await customerService.upsertGuestCustomer(c);

      const booking = await bookingService.createBooking({
        customerId: customerRecord.customerId,
        carTypeId: draft.carType.carTypeId,
        pickupHubId: draft.search.pickupHubId,
        dropHubId: draft.search.dropoffHubId || draft.search.pickupHubId,
        pickupDatetime: `${draft.search.pickupDate}T${draft.search.pickupTime}:00`,
        returnDatetime: `${draft.search.returnDate}T${draft.search.returnTime}:00`,
        addons: selectedAddonLines.map((l) => ({ addonId: l.addon.addonId, quantity: l.quantity })),
        estimatedAmount: totals.total,
      });

      setConfirmedBooking(booking);
      navigate('/booking/booked');
    } catch (err) {
      toast(err.message || 'Could not confirm booking');
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="anim-screen-fade mx-auto max-w-[900px] px-6 pt-14 pb-28 md:px-8">
      <button type="button" onClick={() => navigate('/booking/details')} className="mb-5.5 flex items-center gap-1.5 text-[13.5px] font-medium text-ink/55 hover:text-ink">
        &larr; Back to your details
      </button>
      <StepBar current={5} />
      <div className="mb-9">
        <h1 className="mb-2 font-display text-[26px] font-semibold tracking-[-0.01em] md:text-[30px]">Confirm your booking</h1>
        <p className="max-w-135 text-[15px] text-ink/55">
          Check everything once — "Modify" takes you back to the search with your choices kept.
        </p>
      </div>

      <div className="overflow-hidden rounded-[22px] border border-line bg-white/70 shadow-[0_6px_20px_rgba(18,21,28,0.05)]">
        <VehicleArt
          carTypeName={draft.carType.carTypeName}
          className="!h-36 !w-full !rounded-none"
          imageClassName="!h-36 !w-full !object-contain"
        />

        <div className="p-6.5">
          <SectionLabel>Trip</SectionLabel>
          <table className="mb-5 w-full border-collapse text-[13.5px]">
            <tbody>
              <Row label="Category">
                <b className="font-semibold">{draft.carType.carTypeName}</b>{' '}
                <span className="text-ink/45">— exact vehicle assigned at pickup</span>
              </Row>
              <Row label="Pickup">
                {pickupHub?.hubName} — {draft.search.pickupDate} at {draft.search.pickupTime}
              </Row>
              <Row label="Return">
                {dropHub?.hubName} — {draft.search.returnDate} at {draft.search.returnTime}
              </Row>
              <Row label="Duration">
                {totals.days} day{totals.days > 1 ? 's' : ''}
              </Row>
              <Row label="Add-ons">
                {selectedAddonLines.length
                  ? selectedAddonLines.map((l) => `${l.addon.addonName} ×${l.quantity}`).join(', ')
                  : 'None'}
              </Row>
            </tbody>
          </table>

          <SectionLabel>Renter</SectionLabel>
          <table className="mb-5 w-full border-collapse text-[13.5px]">
            <tbody>
              <Row label="Name">
                {c.firstName} {c.lastName} — {c.email} — {c.phone}
              </Row>
              <Row label="Driving licence">
                {c.drivingLicenseNo} ({c.drivingLicenseState})
              </Row>
              <Row label="Government ID">{c.govtIdDocumentName || 'Not provided'}</Row>
            </tbody>
          </table>

          <SectionLabel>Payment</SectionLabel>
          <table className="w-full border-collapse text-[13.5px]">
            <tbody>
              <Row label="Card on file">
                {c.cardType} ending {c.cardNumber ? c.cardNumber.slice(-4) : '····'}
              </Row>
            </tbody>
          </table>
        </div>

        <div className="flex items-center justify-between border-t border-line bg-ink/3 px-6.5 py-4">
          <span className="text-[13px] text-ink/55">Estimated total</span>
          <b className="font-display text-[22px] text-primary">{formatCurrency(totals.total)}</b>
        </div>
      </div>

      <div className="mt-7 flex flex-wrap gap-3">
        <button
          type="button"
          disabled={busy}
          onClick={handleConfirm}
          className="flex items-center gap-1.5 rounded-full bg-success px-6.5 py-3 text-[14.5px] font-semibold text-white shadow-[0_4px_14px_rgba(31,157,99,0.3)] transition-[filter] hover:brightness-95 disabled:opacity-60"
        >
          {busy ? 'Confirming…' : 'Confirm booking'}
        </button>
        <button
          type="button"
          onClick={() => navigate('/')}
          className="rounded-full border border-line bg-white px-5.5 py-2.5 text-sm font-medium text-ink transition-colors hover:border-ink/30 hover:bg-paper"
        >
          Modify
        </button>
      </div>
    </div>
  );
}
