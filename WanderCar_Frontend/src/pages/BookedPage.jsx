import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { CheckIcon, LogoIcon } from '../components/icons/Icons';
import { useBooking } from '../context/BookingContext';

export default function BookedPage() {
  const { confirmedBooking, resetDraft } = useBooking();
  const navigate = useNavigate();
  const [copied, setCopied] = useState(false);

  useEffect(() => {
    if (!confirmedBooking) navigate('/');
  }, [confirmedBooking, navigate]);

  if (!confirmedBooking) return null;

  function copyConfirmationNo() {
    navigator.clipboard?.writeText(confirmedBooking.confirmationNo).then(() => {
      setCopied(true);
      setTimeout(() => setCopied(false), 1500);
    });
  }

  return (
    <div className="anim-screen-fade mx-auto max-w-[900px] px-6 pt-14 pb-28 md:px-8">
      <div className="relative overflow-hidden rounded-[22px] border border-line bg-white/70 p-9 text-center shadow-[0_6px_20px_rgba(18,21,28,0.05)] md:p-14">
        <div className="anim-tick-pop mx-auto mb-4.5 grid h-15 w-15 place-items-center rounded-full bg-success text-white shadow-[0_0_0_10px_rgba(31,157,99,0.12)]">
          <CheckIcon width="26" height="26" />
        </div>
        <h1 className="mb-1 text-[26px] font-semibold">You're booked!</h1>
        <p className="text-[15px] text-ink/55">
          Confirmation also sent to <span className="font-medium">{confirmedBooking.customer?.email}</span>
        </p>

        <div className="anim-plate-stamp my-5.5 inline-flex items-center gap-3.5 rounded-2xl border border-white/80 bg-white/60 px-7.5 py-4 shadow-[0_16px_40px_rgba(18,21,28,0.14),inset_0_1px_0_rgba(255,255,255,0.9)] backdrop-blur-xl backdrop-saturate-150">
          <span className="grid h-9 w-9 shrink-0 place-items-center rounded-[9px] bg-steel text-white">
            <LogoIcon width="17" height="17" />
          </span>
          <span className="font-mono text-2xl font-semibold tracking-[0.08em]">{confirmedBooking.confirmationNo}</span>
          <button
            type="button"
            onClick={copyConfirmationNo}
            className="ml-1 rounded-full px-3 py-1.5 text-[11.5px] font-semibold text-ink/50 transition-colors hover:bg-ink/6 hover:text-ink"
          >
            {copied ? 'Copied ✓' : 'Copy'}
          </button>
        </div>

        <p className="text-[13.5px] text-ink/50">
          Quote this number at pickup, or use it anytime to modify or cancel your booking.
        </p>

        <div className="mt-6 flex flex-wrap justify-center gap-3">
          <button
            type="button"
            onClick={() => {
              resetDraft();
              navigate('/');
            }}
            className="flex items-center gap-1.5 rounded-full bg-primary px-6.5 py-3 text-[14.5px] font-semibold text-white shadow-[0_4px_14px_rgba(47,111,237,0.3)] transition-[filter] hover:brightness-95"
          >
            Book another car
          </button>
          <button
            type="button"
            onClick={() => navigate('/modify')}
            className="rounded-full border border-line bg-white px-5.5 py-2.5 text-sm font-medium text-ink transition-colors hover:border-ink/30 hover:bg-paper"
          >
            Modify / Cancel
          </button>
        </div>
      </div>
    </div>
  );
}
