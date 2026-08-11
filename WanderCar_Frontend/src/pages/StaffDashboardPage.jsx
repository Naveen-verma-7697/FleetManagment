import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import * as staffService from '../api/staffService';

function StatBar({ label, value, total, colorClass }) {
  const pct = total > 0 ? Math.round((value / total) * 100) : 0;
  return (
    <div>
      <div className="mb-1 flex items-center justify-between text-[12px]">
        <span className="text-ink/55">{label}</span>
        <span className="font-mono font-semibold text-ink">{value}</span>
      </div>
      <div className="h-2 overflow-hidden rounded-full bg-ink/6">
        <div className={`h-full rounded-full ${colorClass}`} style={{ width: `${pct}%` }} />
      </div>
    </div>
  );
}

// Fleet-wide (not scoped to a single hub, since staff sessions in this
// build always resolve to the one demo hub - see CustomerServiceImpl's
// DEFAULT_STAFF_HUB constants). Pass a hubId to staffService.getDashboard
// the day per-hub staff accounts exist.
export default function StaffDashboardPage() {
  const { staff } = useAuth();
  const navigate = useNavigate();
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!staff) {
      navigate('/');
      return;
    }
    staffService.getDashboard().then(setRows).finally(() => setLoading(false));
  }, [staff, navigate]);

  if (!staff) return null;

  const totals = rows.reduce(
    (acc, r) => ({
      total: acc.total + r.totalCars,
      available: acc.available + r.availableCars,
      booked: acc.booked + r.bookedCars,
      maintenance: acc.maintenance + r.maintenanceCars,
    }),
    { total: 0, available: 0, booked: 0, maintenance: 0 }
  );

  return (
    <div className="anim-screen-fade mx-auto max-w-[1000px] px-6 pt-14 pb-28 md:px-8">
      <div className="mb-9">
        <h1 className="mb-2 font-display text-[26px] font-semibold tracking-[-0.01em] md:text-[30px]">Fleet dashboard</h1>
        <p className="max-w-135 text-[15px] text-ink/55">
          How many cars of each type are available, handed over, or in maintenance right now.
        </p>
      </div>

      {loading ? (
        <p className="text-sm text-ink/50">Loading fleet status…</p>
      ) : (
        <>
          <div className="mb-6 grid grid-cols-2 gap-3 sm:grid-cols-4">
            {[
              { label: 'Total fleet', value: totals.total, sub: 'cars' },
              { label: 'Available', value: totals.available, sub: 'ready now' },
              { label: 'Handed over', value: totals.booked, sub: 'with customers' },
              { label: 'Maintenance', value: totals.maintenance, sub: 'off the road' },
            ].map((s) => (
              <div key={s.label} className="rounded-2xl border border-line bg-white/70 p-4">
                <p className="text-[11.5px] font-medium text-ink/45 uppercase tracking-[0.04em]">{s.label}</p>
                <p className="mt-1 font-display text-[26px] font-semibold text-ink">{s.value}</p>
                <p className="text-[12px] text-ink/40">{s.sub}</p>
              </div>
            ))}
          </div>

          <div className="flex flex-col gap-3">
            {rows.map((r) => (
              <div key={r.carTypeId} className="rounded-2xl border border-line bg-white/70 p-4.5">
                <div className="mb-3 flex items-center justify-between">
                  <h3 className="font-display text-[16px] font-semibold">{r.carTypeName}</h3>
                  <span className="font-mono text-[12.5px] text-ink/45">{r.totalCars} total</span>
                </div>
                <div className="grid grid-cols-1 gap-3 sm:grid-cols-3">
                  <StatBar label="Available" value={r.availableCars} total={r.totalCars} colorClass="bg-success" />
                  <StatBar label="Handed over" value={r.bookedCars} total={r.totalCars} colorClass="bg-primary" />
                  <StatBar label="Maintenance" value={r.maintenanceCars} total={r.totalCars} colorClass="bg-ink/30" />
                </div>
              </div>
            ))}
            {rows.length === 0 && <p className="text-sm text-ink/50">No cars in the fleet yet.</p>}
          </div>
        </>
      )}
    </div>
  );
}
