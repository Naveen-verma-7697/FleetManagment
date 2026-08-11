import { CheckIcon, MapPinIcon, HeadsetIcon, ShieldCheckIcon } from '../icons/Icons';

const features = [
  {
    icon: CheckIcon,
    title: 'All car types available',
    desc: 'From economy hatchbacks to premium SUVs — every category, ready when you are.',
  },
  {
    icon: MapPinIcon,
    title: 'Pickup and return anywhere',
    desc: 'Choose any hub for pickup or drop-off, and modify your booking anytime before pickup.',
  },
  {
    icon: HeadsetIcon,
    title: '24/7 support and vehicle delivery',
    desc: 'Round-the-clock assistance, with vehicles delivered to your doorstep any time of day.',
  },
  {
    icon: ShieldCheckIcon,
    title: 'Fair service charges',
    desc: 'Transparent, upfront pricing — no hidden fees or last-minute surprises.',
  },
];

export default function Features() {
  return (
    <section
      id="features"
      className="relative mx-auto max-w-[1200px] overflow-hidden rounded-[32px] px-6 py-16 text-white md:px-8 md:py-22"
      style={{
        backgroundImage:
          'linear-gradient(180deg, rgba(15,18,26,0.6), rgba(15,18,26,0.45)), url(/mountains.jpeg)',
        backgroundSize: 'cover',
        backgroundPosition: 'center 62%',
      }}
    >
      <div className="mb-11 flex flex-wrap items-end justify-between gap-5">
        <div>
          <p className="mb-3 font-mono text-xs tracking-[0.2em] text-white/85 uppercase">Taking care of every client</p>
          <h2 className="font-display text-[28px] font-semibold tracking-[-0.01em] text-white md:text-[34px]">Key Features</h2>
          <p className="mt-3 max-w-115 text-[15.5px] text-white/72">
            Built around the two things fleets live or die by: uptime and visibility.
          </p>
        </div>
      </div>

      <div className="grid grid-cols-2 gap-4 lg:grid-cols-4">
        {features.map((f) => (
          <div
            key={f.title}
            className="rounded-[20px] border border-white/35 bg-[rgba(228,230,235,0.28)] p-6.5 shadow-[0_10px_30px_rgba(0,0,0,0.16),inset_0_1px_0_rgba(255,255,255,0.25)] backdrop-blur-xl backdrop-saturate-150 transition-all hover:-translate-y-0.75 hover:bg-[rgba(228,230,235,0.38)]"
          >
            <div className="mb-4.5 grid h-10.5 w-10.5 place-items-center rounded-xl bg-white/55 text-steel">
              <f.icon width="20" height="20" />
            </div>
            <h3 className="mb-1.5 font-display text-[15.5px] leading-[1.3] font-semibold text-white">{f.title}</h3>
            <p className="text-[13.5px] leading-[1.5] text-white/75">{f.desc}</p>
          </div>
        ))}
      </div>
    </section>
  );
}
