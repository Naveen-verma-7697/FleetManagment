import { useBooking } from '../../context/BookingContext';
import { formatCurrency } from '../../utils/format';
import VehicleArt from '../VehicleArt';

export default function SideSummary() {
  const { draft, selectedAddonLines, totals } = useBooking();
  const { carType } = draft;

  return (
    <aside className="sticky top-22 overflow-hidden rounded-[22px] border border-line bg-white/80 shadow-[0_16px_40px_rgba(18,21,28,0.08)] backdrop-blur-xl">
      {carType && (
        <VehicleArt
          carTypeName={carType.carTypeName}
          className="!h-32 !w-full !rounded-none"
          imageClassName="!h-32 !w-full !object-contain"
        />
      )}

      <div className="p-5.5">
        <h4 className="mb-3.5 font-mono text-[11px] tracking-[0.18em] text-steel uppercase">Trip summary</h4>

        <div className="flex justify-between gap-3 border-b border-dashed border-ink/10 py-2.5 text-[13px]">
          <span className="shrink-0 text-ink/45">Category</span>
          <div className="text-right font-medium text-ink">
            {carType ? carType.carTypeName : '—'}
          </div>
        </div>
        <div className="flex justify-between gap-3 border-b border-dashed border-ink/10 py-2.5 text-[13px]">
          <span className="shrink-0 text-ink/45">Pickup</span>
          <div className="text-right text-ink/80">
            {draft.search.pickupDate} · {draft.search.pickupTime}
          </div>
        </div>
        <div className="flex justify-between gap-3 border-b border-dashed border-ink/10 py-2.5 text-[13px]">
          <span className="shrink-0 text-ink/45">Return</span>
          <div className="text-right text-ink/80">
            {draft.search.returnDate} · {draft.search.returnTime}
          </div>
        </div>
        <div className="flex justify-between gap-3 border-b border-dashed border-ink/10 py-2.5 text-[13px]">
          <span className="shrink-0 text-ink/45">Duration</span>
          <div className="text-right text-ink/80">
            {totals.days} day{totals.days > 1 ? 's' : ''}
          </div>
        </div>
        <div className="flex justify-between gap-3 border-b border-dashed border-ink/10 py-2.5 text-[13px]">
          <span className="shrink-0 text-ink/45">Rental</span>
          <div className="text-right text-ink/80">{formatCurrency(totals.rentalAmount)}</div>
        </div>
        {selectedAddonLines.map((line) => (
          <div key={line.addon.addonId} className="flex justify-between gap-3 border-b border-dashed border-ink/10 py-2.5 text-[13px]">
            <span className="shrink-0 text-ink/45">
              {line.addon.addonName} ×{line.quantity}
            </span>
            <div className="text-right text-ink/80">{formatCurrency(line.subtotal)}</div>
          </div>
        ))}

        <div className="flex items-center justify-between gap-3 pt-4 text-sm">
          <span className="text-ink/45">Estimated total</span>
          <b className="font-display text-[21px] text-primary">{formatCurrency(totals.total)}</b>
        </div>
      </div>
    </aside>
  );
}
