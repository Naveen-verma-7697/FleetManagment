export default function Overlay({ open, onClose, children, maxWidth = 440 }) {
  if (!open) return null;
  return (
    <div
      className="anim-overlay-fade fixed inset-0 z-80 flex items-center justify-center bg-ink/50 p-5 backdrop-blur-sm"
      onMouseDown={(e) => {
        if (e.target === e.currentTarget) onClose?.();
      }}
    >
      <div
        className="anim-modal-up w-full rounded-3xl border border-white/80 bg-white/85 p-7 shadow-[0_26px_60px_rgba(18,21,28,0.25)] backdrop-blur-2xl backdrop-saturate-150"
        style={{ maxWidth }}
      >
        {children}
      </div>
    </div>
  );
}
