import { useState } from 'react';
import { LogoIcon, ArrowRightIcon } from '../icons/Icons';
import { useAuth } from '../../context/AuthContext';
import { useToast } from '../../context/ToastContext';

export default function CtaFooter() {
  const { openAuth } = useAuth();
  const toast = useToast();
  const [email, setEmail] = useState('');

  function handleSubscribe(e) {
    e.preventDefault();
    if (!email) return;
    toast('Subscribed — watch your inbox for fleet updates');
    setEmail('');
  }

  return (
    <footer className="mx-auto max-w-[1200px] px-6 py-16 md:px-8 md:py-22">
      <div
        className="relative overflow-hidden rounded-[28px] bg-ink px-6 py-16 text-center text-white md:py-20"
      >
        <div
          className="pointer-events-none absolute inset-0"
          style={{
            background:
              'radial-gradient(circle at 30% 20%, rgba(62,79,134,0.5), transparent 55%), radial-gradient(circle at 80% 80%, rgba(255,107,53,0.25), transparent 55%)',
          }}
        />
        <div className="relative">
          <h2 className="font-display text-[26px] font-semibold md:text-[32px]">Manage your fleet with confidence</h2>
          <p className="mx-auto mt-3.5 max-w-105 text-white/60">
            Sign in to see every vehicle, booking, and service record in one place.
          </p>
          <button
            type="button"
            onClick={() => openAuth('signup')}
            className="mt-7 inline-flex items-center gap-2 rounded-full bg-white px-6.5 py-3 text-[14.5px] font-medium text-ink transition-transform hover:scale-[1.02]"
          >
            Get started
            <ArrowRightIcon width="15" height="15" />
          </button>
        </div>
      </div>

      <div className="mt-14 grid grid-cols-1 gap-9 md:grid-cols-[1.4fr_1fr_1fr]">
        <div>
          <a href="#home" className="flex items-center gap-2 font-display text-[17px] font-semibold text-ink no-underline">
            <span className="grid h-7 w-7 place-items-center rounded-[9px] bg-steel text-white">
              <LogoIcon width="15" height="15" />
            </span>
            Wander CAR
          </a>
          <p className="mt-3 max-w-70 text-[13.5px] leading-[1.6] text-ink/50">
            Fleet management, built for teams who'd rather look at a dashboard than a spreadsheet.
          </p>
        </div>
        <nav>
          <p className="mb-3 text-[13.5px] font-semibold">Product</p>
          <ul className="m-0 flex list-none flex-col gap-2.5 p-0">
            <li>
              <a href="#home" className="text-[13.5px] text-ink/55 no-underline hover:text-ink">
                Overview
              </a>
            </li>
            <li>
              <a href="#trips" className="text-[13.5px] text-ink/55 no-underline hover:text-ink">
                Fleet
              </a>
            </li>
            <li>
              <a href="#features" className="text-[13.5px] text-ink/55 no-underline hover:text-ink">
                Features
              </a>
            </li>
          </ul>
        </nav>
        <div>
          <p className="mb-3 text-[13.5px] font-semibold">Subscribe to updates</p>
          <form onSubmit={handleSubscribe} className="mt-1 flex gap-2">
            <input
              type="email"
              placeholder="Team2@company.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="flex-1 rounded-full border border-line px-4 py-2.5 text-[13.5px] outline-none focus:border-steel"
            />
            <button
              type="submit"
              aria-label="Subscribe"
              className="grid h-10 w-10 shrink-0 place-items-center rounded-full bg-ink text-white"
            >
              <ArrowRightIcon width="15" height="15" />
            </button>
          </form>
        </div>
      </div>

      <div className="mt-12 flex flex-wrap items-center justify-between gap-3 border-t border-line pt-5.5 text-[12.5px] text-ink/40">
        <p>© 2026 Wander CAR. All rights reserved.</p>
        <div className="flex gap-4.5">
          <a href="#" className="text-inherit no-underline hover:text-ink">
            Terms
          </a>
          <a href="#" className="text-inherit no-underline hover:text-ink">
            Privacy
          </a>
        </div>
      </div>
    </footer>
  );
}
