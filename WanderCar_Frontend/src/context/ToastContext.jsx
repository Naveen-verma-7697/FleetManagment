import { createContext, useCallback, useContext, useRef, useState } from 'react';

const ToastContext = createContext(null);

export function ToastProvider({ children }) {
  const [toast, setToast] = useState({ message: '', show: false });
  const timerRef = useRef(null);

  const showToast = useCallback((message) => {
    clearTimeout(timerRef.current);
    setToast({ message, show: true });
    timerRef.current = setTimeout(() => {
      setToast((t) => ({ ...t, show: false }));
    }, 3200);
  }, []);

  return (
    <ToastContext.Provider value={showToast}>
      {children}
      <div
        className={`fixed left-1/2 z-90 max-w-[340px] -translate-x-1/2 rounded-2xl border-l-4 border-primary bg-ink px-6 py-3.5 text-[13.5px] text-white shadow-[0_16px_40px_rgba(18,21,28,0.3)] transition-[bottom] duration-350 ${
          toast.show ? 'bottom-6' : '-bottom-24'
        }`}
      >
        {toast.message}
      </div>
    </ToastContext.Provider>
  );
}

export function useToast() {
  const ctx = useContext(ToastContext);
  if (!ctx) throw new Error('useToast must be used within ToastProvider');
  return ctx;
}
