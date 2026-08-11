const STEPS = ['Location', 'Vehicle', 'Add-ons', 'Your details', 'Confirm'];

export default function StepBar({ current }) {
  const fillPct = ((current - 1) / (STEPS.length - 1)) * 100;
  return (
    <div className="relative mb-10 flex items-start">
      <div className="absolute top-[15px] right-4 left-4 h-[3px] rounded-[3px] bg-line" />
      <div
        className="absolute top-[15px] left-4 h-[3px] rounded-[3px] bg-primary transition-[width] duration-450"
        style={{ width: `calc(${fillPct}% * (100% - 2rem) / 100%)` }}
      />
      {STEPS.map((label, i) => {
        const stepNo = i + 1;
        const done = stepNo < current;
        const now = stepNo === current;
        return (
          <div key={label} className="relative z-10 flex-1 text-center">
            <div
              className={`mx-auto mb-2 flex h-8 w-8 items-center justify-center rounded-full border-2 font-mono text-xs font-medium transition-all duration-200 ${
                done
                  ? 'border-primary bg-primary text-white'
                  : now
                    ? 'border-primary bg-white text-primary shadow-[0_0_0_5px_rgba(47,111,237,0.14)]'
                    : 'border-line bg-white text-ink/40'
              }`}
            >
              {stepNo}
            </div>
            <small className={`text-[11.5px] ${now ? 'font-semibold text-ink' : 'text-ink/45'}`}>{label}</small>
          </div>
        );
      })}
    </div>
  );
}
