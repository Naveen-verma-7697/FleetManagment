const stats = [
  { value: '40+', label: 'Cities covered' },
  { value: '1,200+', label: 'Cars in fleet' },
  { value: '99.9%', label: 'Booking uptime' },
];

export default function AboutUs() {
  return (
    <section id="about" className="mx-auto max-w-[1200px] px-6 py-16 md:px-8 md:py-22">
      <div className="grid items-start gap-10 md:grid-cols-[1.3fr_1fr]">
        <div>
          <p className="mb-3 font-mono text-xs tracking-[0.2em] text-steel uppercase">Who's behind the wheel</p>
          <h2 className="font-display text-[28px] font-semibold tracking-[-0.01em] md:text-[34px]">About WanderCar</h2>
          <p className="mt-3 max-w-130 text-[15.5px] text-ink/55">
            Wander CAR started as a small team tired of fleet spreadsheets. Today we run a self-drive rental network
            built around one idea: booking a car — or handing one back — should take less time than parking it.
          </p>
        </div>
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-3 md:grid-cols-1 lg:grid-cols-3">
          {stats.map((s) => (
            <div key={s.label} className="rounded-[18px] border border-line bg-paper p-5 text-left">
              <span className="block font-display text-[26px] font-bold text-primary">{s.value}</span>
              <span className="mt-1 block text-[12.5px] text-ink/50">{s.label}</span>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
