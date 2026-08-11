import BookingSearchCard from './BookingSearchCard';
import { useBooking } from '../../context/BookingContext';

export default function Hero() {
  const { prefillVersion } = useBooking();
  return (
    <section id="home" className="relative overflow-x-hidden px-6 pt-4 pb-6 md:px-8 md:pb-8">
      <div className="mx-auto max-w-[1200px]">
        <div className="relative z-[3] mb-6 max-w-[480px]">
          <p className="anim-drop-in mb-3 font-mono text-xs tracking-[0.2em] text-steel uppercase">
            Real-time fleet operations
          </p>
          <h1
            className="anim-drop-in m-0 font-display text-[32px] leading-[1.08] font-medium tracking-[-0.02em] md:text-[44px]"
            style={{ animationDelay: '0.1000s' }}
          >
            Self Drive Car Rental
            <span className="mt-1.5 block text-[0.62em] font-bold text-primary">Book Affordable Cars</span>
          </h1>
        </div>

        <div className="relative grid items-center gap-6 md:grid-cols-[1fr_380px] md:gap-8">
          {/* <div className="anim-drive-in order-2 mx-auto w-full max-w-[720px] md:order-1 md:-mt-48">
            <img
        src="silver-suv-car.png"
        alt="Rental Car"
        className="h-auto w-full"
           />
          </div> */}

           <div className="anim-drive-in order-2 mx-auto w-full max-w-[760px] md:order-1 md:-mt-48 md:translate-x-30 relative z-10">
  <div
    className="pointer-events-none absolute -inset-16 -z-10 rounded-[50%] blur-[60px]"
    style={{
      background: 'radial-gradient(closest-side, rgba(47,111,237,0.55), transparent 70%)',
    }}
  />
  <img
    src="silver-suv-car.png"
    alt="Rental Car"
    className="h-auto w-full"
  />
</div>
          {/* <div className="order-1 md:order-2 md:-mt-84">
            <BookingSearchCard key={prefillVersion} />
          </div> */}

          <div className="order-1 md:order-2 md:-mt-84 relative z-30 -ml-20">
  <BookingSearchCard key={prefillVersion} />
</div>
        </div>
      </div>
    </section>
  );
}