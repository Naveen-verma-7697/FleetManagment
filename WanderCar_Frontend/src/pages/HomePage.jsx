import Hero from '../components/home/Hero';
import TripHistory from '../components/home/TripHistory';
import Features from '../components/home/Features';
import AboutUs from '../components/home/AboutUs';
import Reviews from '../components/home/Reviews';
import CtaFooter from '../components/home/CtaFooter';

export default function HomePage() {
  return (
    <div className="anim-screen-fade">
      <Hero />
      <TripHistory />
      <Features />
      <AboutUs />
      <Reviews />
      <CtaFooter />
    </div>
  );
}
