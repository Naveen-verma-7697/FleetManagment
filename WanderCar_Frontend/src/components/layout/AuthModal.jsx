import { useState } from 'react';
import Overlay from '../Overlay';
import { MailIcon, LockIcon, UserIcon, GoogleIcon } from '../icons/Icons';
import { useAuth } from '../../context/AuthContext';
import { useToast } from '../../context/ToastContext';

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

export default function AuthModal() {
  const { authModal, closeAuth, setAuthMode, login, register, ssoLogin } = useAuth();
  const toast = useToast();
  const [loginForm, setLoginForm] = useState({ email: '', password: '' });
  const [signupForm, setSignupForm] = useState({ username: '', email: '', password: '' });
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  const mode = authModal.mode;

  async function handleLogin(e) {
    e.preventDefault();
    setError('');
    setBusy(true);
    try {
      await login(loginForm.email, loginForm.password);
      setLoginForm({ email: '', password: '' });
    } catch (err) {
      setError(err.message || 'Could not log in');
    } finally {
      setBusy(false);
    }
  }

  async function handleSignup(e) {
    e.preventDefault();
    setError('');
    setBusy(true);
    try {
      await register(signupForm.username, signupForm.email, signupForm.password);
      setSignupForm({ username: '', email: '', password: '' });
    } catch (err) {
      setError(err.message || 'Could not create account');
    } finally {
      setBusy(false);
    }
  }

  return (
    <Overlay open={authModal.open} onClose={closeAuth} maxWidth={400}>
      <div className="relative pt-[6px]">
        <button
          type="button"
          onClick={closeAuth}
          aria-label="Close"
          className="absolute -top-3 -right-3 grid h-[30px] w-[30px] place-items-center rounded-full bg-ink/6 text-lg leading-none text-ink/60 transition-colors hover:bg-ink/12 hover:text-ink"
        >
          &times;
        </button>
        <h3 className="mb-4 text-center font-display text-[19px] font-semibold">
          {mode === 'login' ? 'Log in to WanderCar' : 'Create your WanderCar account'}
        </h3>

        <div className="mb-4.5 flex rounded-full bg-ink/6 p-[3px]">
          <button
            type="button"
            onClick={() => setAuthMode('login')}
            className={`flex-1 rounded-full py-1.5 text-[13px] font-medium transition-colors ${mode === 'login' ? 'bg-ink text-white' : 'text-ink/55'}`}
          >
            Log in
          </button>
          <button
            type="button"
            onClick={() => setAuthMode('signup')}
            className={`flex-1 rounded-full py-1.5 text-[13px] font-medium transition-colors ${mode === 'signup' ? 'bg-ink text-white' : 'text-ink/55'}`}
          >
            Sign up
          </button>
        </div>

        {error && (
          <p className="mb-3 rounded-lg bg-danger/10 px-3 py-2 text-[12.5px] text-danger">{error}</p>
        )}

        {mode === 'login' ? (
          <form onSubmit={handleLogin}>
            <LabeledInput
              label="Email"
              icon={MailIcon}
              type="email"
              placeholder="you@email.com"
              required
              autoComplete="email"
              value={loginForm.email}
              onChange={(e) => setLoginForm((f) => ({ ...f, email: e.target.value }))}
            />
            <LabeledInput
              label="Password"
              icon={LockIcon}
              type="password"
              placeholder="••••••••"
              required
              minLength={6}
              autoComplete="current-password"
              value={loginForm.password}
              onChange={(e) => setLoginForm((f) => ({ ...f, password: e.target.value }))}
            />
            <div className="-mt-2 mb-3.5 flex justify-end">
              <button
                type="button"
                onClick={() => toast('Password reset link sent (mock)')}
                className="text-[12.5px] font-semibold text-primary hover:underline"
              >
                Forgot password?
              </button>
            </div>
            <button
              type="submit"
              disabled={busy}
              className="w-full rounded-full bg-primary py-3 text-[14.5px] font-medium text-white transition-[filter] hover:brightness-95 disabled:opacity-60"
            >
              {busy ? 'Logging in…' : 'Log in'}
            </button>
            <p className="mt-2 text-center text-[11.5px] text-ink/40">
              Demo account: TEAM2@email.com / password123
            </p>
          </form>
        ) : (
          <form onSubmit={handleSignup}>
            <LabeledInput
              label="Full name"
              icon={UserIcon}
              type="text"
              placeholder="Gryffindors"
              required
              autoComplete="name"
              value={signupForm.username}
              onChange={(e) => setSignupForm((f) => ({ ...f, username: e.target.value }))}
            />
            <LabeledInput
              label="Email"
              icon={MailIcon}
              type="email"
              placeholder="you@email.com"
              required
              autoComplete="email"
              value={signupForm.email}
              onChange={(e) => setSignupForm((f) => ({ ...f, email: e.target.value }))}
            />
            <LabeledInput
              label="Password"
              icon={LockIcon}
              type="password"
              placeholder="Create a password"
              required
              minLength={6}
              autoComplete="new-password"
              value={signupForm.password}
              onChange={(e) => setSignupForm((f) => ({ ...f, password: e.target.value }))}
            />
            <button
              type="submit"
              disabled={busy}
              className="w-full rounded-full bg-primary py-3 text-[14.5px] font-medium text-white transition-[filter] hover:brightness-95 disabled:opacity-60"
            >
              {busy ? 'Creating account…' : 'Create account'}
            </button>
          </form>
        )}

        <div className="my-4.5 flex items-center gap-3 text-[12px] uppercase tracking-[0.06em] text-ink/40">
          <span className="h-px flex-1 bg-ink/10" />
          or continue with
          <span className="h-px flex-1 bg-ink/10" />
        </div>

        <button
          type="button"
          onClick={ssoLogin}
          className="flex w-full items-center justify-center gap-2 rounded-xl border border-ink/12 bg-white py-2.5 text-[13.5px] font-medium text-ink transition-colors hover:border-ink/20 hover:bg-ink/3"
        >
          <GoogleIcon width="16" height="16" />
          Google
        </button>

        <p className="mt-4.5 text-center text-[13px] text-ink/60">
          {mode === 'login' ? (
            <>
              New here?{' '}
              <button type="button" onClick={() => setAuthMode('signup')} className="font-semibold text-primary hover:underline">
                Create an account
              </button>
            </>
          ) : (
            <>
              Already have an account?{' '}
              <button type="button" onClick={() => setAuthMode('login')} className="font-semibold text-primary hover:underline">
                Log in
              </button>
            </>
          )}
        </p>
      </div>
    </Overlay>
  );
}
