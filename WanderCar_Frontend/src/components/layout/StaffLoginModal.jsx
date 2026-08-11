import { useState } from 'react';
import Overlay from '../Overlay';
import { MailIcon, LockIcon } from '../icons/Icons';
import { useAuth } from '../../context/AuthContext';

const fieldClass =
  'w-full rounded-[11px] border border-ink/10 bg-white/70 px-[11px] py-2.5 text-[13.5px] text-ink outline-none focus:border-steel';

function LabeledInput({ label, icon: Icon, ...props }) {
  return (
    <label className="field mb-3.5 block">
      <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">{label}</span>
      <div className="relative">
        {Icon && (
          <Icon className="pointer-events-none absolute left-[9px] top-1/2 -translate-y-1/2 opacity-45" width="13" height="13" />
        )}
        <input className={`${fieldClass} ${Icon ? 'pl-[30px]' : ''}`} {...props} />
      </div>
    </label>
  );
}

// Staff sign-in only - there is no staff registration flow. Checked against
// a single fixed account on the backend (no staff table, just one hardcoded
// email/password pair - see CustomerServiceImpl.staffLogin on the server).
export default function StaffLoginModal() {
  const { staffAuthOpen, closeStaffAuth, staffLogin } = useAuth();
  const [form, setForm] = useState({ username: '', password: '' });
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setBusy(true);
    try {
      await staffLogin(form.username, form.password);
      setForm({ username: '', password: '' });
    } catch (err) {
      setError(err.message || 'Could not log in');
    } finally {
      setBusy(false);
    }
  }

  return (
    <Overlay open={staffAuthOpen} onClose={closeStaffAuth} maxWidth={400}>
      <div className="relative pt-[6px]">
        <button
          type="button"
          onClick={closeStaffAuth}
          aria-label="Close"
          className="absolute -top-3 -right-3 grid h-[30px] w-[30px] place-items-center rounded-full bg-ink/6 text-lg leading-none text-ink/60 transition-colors hover:bg-ink/12 hover:text-ink"
        >
          &times;
        </button>
        <h3 className="mb-1 text-center font-display text-[19px] font-semibold">Staff login</h3>
        <p className="mb-4.5 text-center text-[12.5px] text-ink/50">Hub staff only — for handovers, returns, and the fleet dashboard.</p>

        {error && (
          <p className="mb-3 rounded-lg bg-danger/10 px-3 py-2 text-[12.5px] text-danger">{error}</p>
        )}

        <form onSubmit={handleSubmit}>
          <LabeledInput
            label="Staff email"
            icon={MailIcon}
            type="email"
            placeholder="you@wandercar.example"
            required
            autoComplete="username"
            value={form.username}
            onChange={(e) => setForm((f) => ({ ...f, username: e.target.value }))}
          />
          <LabeledInput
            label="Password"
            icon={LockIcon}
            type="password"
            placeholder="••••••••"
            required
            autoComplete="current-password"
            value={form.password}
            onChange={(e) => setForm((f) => ({ ...f, password: e.target.value }))}
          />
          <button
            type="submit"
            disabled={busy}
            className="mt-1 w-full rounded-full bg-ink py-3 text-[14.5px] font-medium text-white transition-[filter] hover:brightness-110 disabled:opacity-60"
          >
            {busy ? 'Logging in…' : 'Log in'}
          </button>
        </form>
      </div>
    </Overlay>
  );
}
