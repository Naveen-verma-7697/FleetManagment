const reviews = [
  {
    stars: '★★★★★',
    quote:
      "Picked a car at the airport and it was waiting exactly where the app said. Didn't expect it to be that smooth.",
    name: 'Priya N.',
    role: 'Frequent renter',
    color: '#2B3A67',
    initial: 'P',
  },
  {
    stars: '★★★★★',
    quote: 'Dropped the car off in a different city than I picked it up. The whole thing took two taps in the booking card.',
    name: 'Marcus D.',
    role: 'Business traveler',
    color: '#FF6B35',
    initial: 'M',
  },
  {
    stars: '★★★★☆',
    quote: 'Support picked up in under a minute when I needed to extend my rental. Small thing, but it matters.',
    name: 'Sara K.',
    role: 'Weekend driver',
    color: '#1F9D63',
    initial: 'S',
  },
];

export default function Reviews() {
  return (
    <section className="mx-auto max-w-[1200px] bg-paper px-6 py-16 md:px-8 md:py-22">
      <div className="mb-11 flex flex-wrap items-end justify-between gap-5">
        <div>
          <p className="mb-3 font-mono text-xs tracking-[0.2em] text-steel uppercase">What renters say</p>
          <h2 className="font-display text-[28px] font-semibold tracking-[-0.01em] md:text-[34px]">Customer reviews</h2>
          <div className="mt-3.5 flex items-center gap-2">
            <span className="text-sm tracking-[2px] text-warn">★★★★★</span>
            <span className="text-[13.5px] text-ink/50">4.9 average from 2,300+ renters</span>
          </div>
        </div>
      </div>

      <div className="grid gap-4.5 sm:grid-cols-2 lg:grid-cols-3">
        {reviews.map((r) => (
          <div key={r.name} className="rounded-[22px] border border-line bg-white p-6">
            <div className="mb-3 text-[13px] tracking-[2px] text-warn">{r.stars}</div>
            <p className="text-[14.5px] leading-[1.6] text-ink/75">{r.quote}</p>
            <div className="mt-4.5 flex items-center gap-2.5">
              <span
                className="grid h-9 w-9 shrink-0 place-items-center rounded-full font-display text-[13px] font-semibold text-white"
                style={{ background: r.color }}
              >
                {r.initial}
              </span>
              <span className="flex flex-col">
                <span className="text-[13.5px] font-semibold">{r.name}</span>
                <span className="text-xs text-ink/45">{r.role}</span>
              </span>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}
