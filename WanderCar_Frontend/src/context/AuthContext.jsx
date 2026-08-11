import { createContext, useCallback, useContext, useMemo, useState } from 'react';
import * as customerService from '../api/customerService';
import { useToast } from './ToastContext';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  // const [customer, setCustomer] = useState(null);
  // const [staff, setStaff] = useState(null);
  const [customer, setCustomer] = useState(() => {
    const saved = localStorage.getItem("customer");
    return saved ? JSON.parse(saved) : null;
  });

  const [staff, setStaff] = useState(() => {
    const saved = localStorage.getItem("staff");
    return saved ? JSON.parse(saved) : null;
  });
  const [authModal, setAuthModal] = useState({ open: false, mode: 'login' });
  const [staffAuthOpen, setStaffAuthOpen] = useState(false);
  const [logoutConfirmOpen, setLogoutConfirmOpen] = useState(false);
  const toast = useToast();

  const openAuth = useCallback((mode = 'login') => {
    setAuthModal({ open: true, mode });
  }, []);
  const closeAuth = useCallback(() => setAuthModal((m) => ({ ...m, open: false })), []);
  const setAuthMode = useCallback((mode) => setAuthModal((m) => ({ ...m, mode })), []);

  const openStaffAuth = useCallback(() => setStaffAuthOpen(true), []);
  const closeStaffAuth = useCallback(() => setStaffAuthOpen(false), []);

  const requestLogout = useCallback(() => setLogoutConfirmOpen(true), []);
  const cancelLogout = useCallback(() => setLogoutConfirmOpen(false), []);

  const login = useCallback(
    async (email, password) => {
      const { token,customer: c } = await customerService.login({ email, password });
      localStorage.setItem("token", token);

      localStorage.setItem("customer", JSON.stringify(c));
      localStorage.removeItem("staff");

      setStaff(null);
      setCustomer(c);
      closeAuth();
      toast(`Welcome back, ${c.fullName.split(' ')[0]}`);
      return c;
    },
    [closeAuth, toast]
  );

  const register = useCallback(
    async (fullName, email, password) => {
      const {token, customer: c } = await customerService.register({ fullName, email, password });
localStorage.setItem("token", token);

      localStorage.setItem("customer", JSON.stringify(c));
      localStorage.removeItem("staff");

      setStaff(null);
      

      setCustomer(c);
      closeAuth();
      toast(`Account created — welcome, ${c.fullName.split(' ')[0]}`);
      return c;
    },
    [closeAuth, toast]
  );

  const ssoLogin = useCallback(() => {

    window.location.href =
      "http://localhost:5180/api/auth/google-login";

  }, []);

  const completeOAuthLogin = useCallback((customer, token) => {

    localStorage.setItem("token", token);

    localStorage.setItem(
      "customer",
      JSON.stringify(customer)
    );

    localStorage.removeItem("staff");

    setStaff(null);
    setCustomer(customer);

  }, []);

  const logout = useCallback(() => {
localStorage.removeItem("token");
    localStorage.removeItem("customer");
    localStorage.removeItem("staff");

    setCustomer(null);
    setStaff(null);
    setLogoutConfirmOpen(false);
    toast('Logged out');
  }, [toast]);

  const staffLoginFn = useCallback(
    async (username, password) => {
      const {token, staff: s } = await customerService.staffLogin({ username, password });
      localStorage.setItem("token", token);

      localStorage.setItem("staff", JSON.stringify(s));
      localStorage.removeItem("customer");

      setCustomer(null);
      setStaff(s);
      setStaffAuthOpen(false);
      toast(`Staff tools unlocked for ${s.username}`);
      return s;
    },
    [toast]
  );

  const staffLogout = useCallback(() => {
   localStorage.removeItem("token");
    localStorage.removeItem("customer");
    localStorage.removeItem("staff");

    setCustomer(null);
    setStaff(null);
    toast('Staff session ended');
  }, [toast]);

  // Used by the profile page - persists the edit via the same partial-patch
  // endpoint CustomerInfoPage/ConfirmPage use, then updates the locally-held
  // customer object so the navbar and every prefill reflect it immediately.
  const updateProfile = useCallback(
    async (patch) => {
      const updated = await customerService.updateCustomer({ customerId: customer.customerId, ...patch });

      localStorage.setItem(
        "customer",
        JSON.stringify(updated)
      );
      setCustomer(updated);
      toast('Profile updated');
      return updated;
    },
    [customer, toast]
  );

  const value = useMemo(
    () => ({
      customer,
      staff,
      isAuthenticated: !!customer,
      isStaff: !!staff,
      authModal,
      openAuth,
      closeAuth,
      setAuthMode,
      staffAuthOpen,
      openStaffAuth,
      closeStaffAuth,
      logoutConfirmOpen,
      requestLogout,
      cancelLogout,
      login,
      register,
      ssoLogin,
      completeOAuthLogin,
      logout,
      staffLogin: staffLoginFn,
      staffLogout,
      updateProfile,
    }),
    [
      customer,
      staff,
      authModal,
      openAuth,
      closeAuth,
      setAuthMode,
      staffAuthOpen,
      openStaffAuth,
      closeStaffAuth,
      logoutConfirmOpen,
      requestLogout,
      cancelLogout,
      login,
      register,
      ssoLogin,
      completeOAuthLogin,
      logout,
      staffLoginFn,
      staffLogout,
      updateProfile,
    ]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
