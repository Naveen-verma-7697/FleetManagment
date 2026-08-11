import { useEffect, useMemo, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { MapPinIcon, PlaneIcon, CalendarIcon, ClockIcon, SearchIcon } from '../icons/Icons';
import * as locationService from '../../api/locationService';
import { useBooking } from '../../context/BookingContext';
import { useToast } from '../../context/ToastContext';

const fieldClass =
  'w-full rounded-[11px] border border-ink/10 bg-white/70 px-[11px] py-2.5 text-[13.5px] text-ink outline-none focus:border-steel appearance-none';

function todayISO() {
  return new Date().toISOString().slice(0, 10);
}
function plusDaysISO(days) {
  const d = new Date();
  d.setDate(d.getDate() + days);
  return d.toISOString().slice(0, 10);
}

function LocationPicker({ prefix, mode, onModeChange, state, city, onStateChange, onCityChange, airportText, onAirportChange, onAirportPick }) {
  const [states, setStates] = useState([]);
  const [cities, setCities] = useState([]);
  const [citySuggestOpen, setCitySuggestOpen] = useState(false);
  const [airportOptions, setAirportOptions] = useState([]);
  const [airportSuggestOpen, setAirportSuggestOpen] = useState(false);

  useEffect(() => {
    locationService.getStates().then(setStates);
  }, []);

  useEffect(() => {
    if (!state) {
      setCities([]);
      return;
    }
    locationService.getCitiesByState(state).then(setCities);
  }, [state]);

  useEffect(() => {
    if (mode !== 'airport') return;
    locationService.searchAirports(airportText).then(setAirportOptions);
  }, [mode, airportText]);

  const cityMatches = useMemo(
    () => cities.filter((c) => c.cityName.toLowerCase().includes((city || '').toLowerCase())),
    [cities, city]
  );

  return (
    <div>
      <div className="mb-3.5 flex rounded-full bg-ink/6 p-[3px]">
        <button
          type="button"
          onClick={() => onModeChange('city')}
          className={`flex-1 rounded-full py-1.5 text-[13px] font-medium transition-colors ${mode === 'city' ? 'bg-ink text-white' : 'text-ink/55'}`}
        >
          City
        </button>
        <button
          type="button"
          onClick={() => onModeChange('airport')}
          className={`flex-1 rounded-full py-1.5 text-[13px] font-medium transition-colors ${mode === 'airport' ? 'bg-ink text-white' : 'text-ink/55'}`}
        >
          Airport
        </button>
      </div>

      {mode === 'city' ? (
        <div className="mb-1.5 grid grid-cols-2 gap-2.5">
          <label className="field block">
            <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">State</span>
            <select
              className={fieldClass}
              value={state}
              onChange={(e) => {
                onStateChange(e.target.value);
                onCityChange('');
              }}
            >
              <option value="">Select state</option>
              {states.map((s) => (
                <option key={s.stateId} value={s.stateId}>
                  {s.stateName}
                </option>
              ))}
            </select>
          </label>
          <label className="field relative block">
            <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">City</span>
            <div className="relative">
              <MapPinIcon className="pointer-events-none absolute left-[9px] top-1/2 -translate-y-1/2 opacity-45" width="13" height="13" />
              <input
                type="text"
                disabled={!state}
                placeholder={state ? 'Type a city' : 'Pick a state first'}
                autoComplete="off"
                className={`${fieldClass} pl-[30px] disabled:opacity-60`}
                value={city}
                onChange={(e) => onCityChange(e.target.value)}
                onFocus={() => setCitySuggestOpen(true)}
                onBlur={() => setTimeout(() => setCitySuggestOpen(false), 150)}
              />
            </div>
            {citySuggestOpen && state && cityMatches.length > 0 && (
              <ul className="suggestions-scroll absolute z-20 mt-1.5 max-h-42 w-full overflow-auto rounded-xl border border-ink/10 bg-white/98 py-1 shadow-[0_10px_30px_rgba(18,21,28,0.18)]">
                {cityMatches.map((c) => (
                  <li key={c.cityId}>
                    <button
                      type="button"
                      onMouseDown={(e) => {
                        e.preventDefault();
                        onCityChange(c.cityName);
                        setCitySuggestOpen(false);
                      }}
                      className="block w-full px-[11px] py-1.5 text-left text-[13px] text-ink hover:bg-steel/8"
                    >
                      {c.cityName}
                    </button>
                  </li>
                ))}
              </ul>
            )}
          </label>
        </div>
      ) : (
        <label className="field relative mb-4 block">
          <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">Airport</span>
          <div className="relative">
            <PlaneIcon className="pointer-events-none absolute left-[9px] top-1/2 -translate-y-1/2 opacity-45" width="13" height="13" />
            <input
              type="text"
              placeholder="Search airport or code"
              autoComplete="off"
              className={`${fieldClass} pl-[30px]`}
              value={airportText}
              onChange={(e) => onAirportChange(e.target.value)}
              onFocus={() => setAirportSuggestOpen(true)}
              onBlur={() => setTimeout(() => setAirportSuggestOpen(false), 150)}
            />
          </div>
          {airportSuggestOpen && airportOptions.length > 0 && (
            <ul className="suggestions-scroll absolute z-20 mt-1.5 max-h-42 w-full overflow-auto rounded-xl border border-ink/10 bg-white/98 py-1 shadow-[0_10px_30px_rgba(18,21,28,0.18)]">
              {airportOptions.map((a) => (
                <li key={a.airportId}>
                  <button
                    type="button"
                    onMouseDown={(e) => {
                      e.preventDefault();
                      onAirportPick(a);
                      setAirportSuggestOpen(false);
                    }}
                    className="block w-full px-[11px] py-1.5 text-left text-[13px] text-ink hover:bg-steel/8"
                  >
                    {a.airportCode} — {a.airportName}
                    <small className="block text-[11px] text-ink/45">{a.cityId && a.airportCode}</small>
                  </button>
                </li>
              ))}
            </ul>
          )}
        </label>
      )}
      <input type="hidden" name={`${prefix}-noop`} />
    </div>
  );
}

export default function BookingSearchCard() {
  const { draft, setSearch, resetDraft, staffMode } = useBooking();
  const seed = draft.search;
  const navigate = useNavigate();
  const toast = useToast();
  const [show, setShow] = useState(false);

  const [pickupMode, setPickupMode] = useState(seed.pickupMode || 'city');
  const [pickupState, setPickupState] = useState(seed.pickupState || '');
  const [pickupCity, setPickupCity] = useState(seed.pickupCity || '');
  const [pickupAirportText, setPickupAirportText] = useState(seed.pickupAirport || '');
  const [pickupAirport, setPickupAirport] = useState(null);

  const [pickupDate, setPickupDate] = useState(seed.pickupDate || todayISO());
  const [pickupTime, setPickupTime] = useState(seed.pickupTime || '10:00');
  const [returnDate, setReturnDate] = useState(seed.returnDate || plusDaysISO(3));
  const [returnTime, setReturnTime] = useState(seed.returnTime || '10:00');

  const [differentDropoff, setDifferentDropoff] = useState(seed.differentDropoff || false);
  const [dropoffMode, setDropoffMode] = useState(seed.dropoffMode || 'city');
  const [dropoffState, setDropoffState] = useState(seed.dropoffState || '');
  const [dropoffCity, setDropoffCity] = useState(seed.dropoffCity || '');
  const [dropoffAirportText, setDropoffAirportText] = useState(seed.dropoffAirport || '');
  const [dropoffAirport, setDropoffAirport] = useState(null);

  useEffect(() => {
    const t = setTimeout(() => setShow(true), 50);
    return () => clearTimeout(t);
  }, []);

  const cardRef = useRef(null);

  // Turning the toggle on makes the card taller (a second LocationPicker
  // slides in below) - on a shorter laptop screen that growth can push the
  // bottom of the card past the viewport. Scrolling the card back into
  // view (block: 'nearest' - the smallest scroll that gets the whole
  // element on screen, not a jump to the top or bottom) keeps the whole
  // card visible without moving the page around any more than it has to.
  function handleToggleDropoff() {
    setDifferentDropoff((prev) => {
      const next = !prev;
      if (next) {
        requestAnimationFrame(() => {
          setTimeout(() => {
            cardRef.current?.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
          }, 220);
        });
      }
      return next;
    });
  }

  async function resolveCity(stateId, cityName) {
    const cities = await locationService.getCitiesByState(stateId);
    return cities.find((c) => c.cityName.toLowerCase().trim() === cityName.toLowerCase().trim());
  }

  async function handleSubmit(e) {
    e.preventDefault();

    let pickupCityObj = null;
    let pickupHub = null;

    if (pickupMode === "city") {
      pickupCityObj = await resolveCity(pickupState, pickupCity);

      if (!pickupCityObj) {
        toast("Pick a valid city");
        return;
      }
    } else {
      if (!pickupAirport) {
        toast("Pick an airport for pickup");
        return;
      }

      pickupHub = locationService.findHubForAirport(
        pickupAirport.airportId
      );

      if (!pickupHub) {
        toast("No hub found for this airport");
        return;
      }
    }

    let dropHub = null;

if (differentDropoff && dropoffMode === "airport") {

    if (!dropoffAirport) {
        toast("Pick an airport for return");
        return;
    }

    dropHub = locationService.findHubForAirport(
        dropoffAirport.airportId
    );

    if (!dropHub) {
        toast("No hub found for this airport");
        return;
    }
}

    if (!pickupDate || !returnDate) {
      toast("Pick both pickup and return dates");
      return;
    }

    let dropoffCityObj = null;

    if (differentDropoff && dropoffMode === "city") {
      dropoffCityObj = await resolveCity(dropoffState, dropoffCity);

      if (!dropoffCityObj) {
        toast("Pick a valid return city");
        return;
      }
    }


    setSearch({
      pickupMode,
      pickupState,
      pickupCity: pickupMode === "city" ? pickupCity : "",
      pickupCityId: pickupMode === "city" ? pickupCityObj.cityId : null,

      pickupAirport:
        pickupMode === "airport" && pickupAirport
          ? `${pickupAirport.airportCode} — ${pickupAirport.airportName}`
          : "",

      pickupHubId:
        pickupMode === "airport"
          ? pickupHub.hubId
          : null,

      pickupDate,
      pickupTime,
      returnDate,
      returnTime,

      differentDropoff,

      dropoffMode: differentDropoff ? dropoffMode : "city",
      dropoffState: differentDropoff ? dropoffState : "",
      dropoffCity:
        differentDropoff && dropoffMode === "city"
          ? dropoffCity
          : "",
      dropoffCityId:
        differentDropoff && dropoffMode === "city"
          ? dropoffCityObj.cityId
          : null,
      dropoffAirport:
        differentDropoff &&
          dropoffMode === "airport" &&
          dropoffAirport
          ? `${dropoffAirport.airportCode} — ${dropoffAirport.airportName}`
          : "",

      dropoffHubId:
    differentDropoff && dropoffMode === "airport"
        ? dropHub.hubId
        : null,
    });

    const needsPickupHub = pickupMode === "city";

const needsReturnHub =
    differentDropoff &&
    dropoffMode === "city";

if (needsPickupHub || needsReturnHub) {
    navigate("/hub-selection");
} else {
    navigate("/vehicles");
}
  }

  return (
    <div className={`transition-all duration-700 ${show ? 'translate-y-0 opacity-100' : 'translate-y-12 opacity-0'}`}>
      <div className="relative">
        <div
          className="absolute -inset-8 -z-10 rounded-[40px] blur-[28px]"
          style={{
            background:
              'radial-gradient(closest-side, rgba(62,79,134,0.28), rgba(255,107,53,0.12) 70%, transparent 100%)',
          }}
        />
        <form
          onSubmit={handleSubmit}
          ref={cardRef}
          className="card-sheen relative overflow-hidden rounded-[30px] border border-white/45 bg-[rgba(176,179,185,0.6)] p-5.5 shadow-[0_20px_50px_rgba(18,21,28,0.14),inset_0_1px_0_rgba(255,255,255,0.55)] backdrop-blur-2xl backdrop-saturate-150"
        >
          <div
            className="pointer-events-none absolute inset-0 z-[1] rounded-[30px]"
            style={{ background: 'linear-gradient(135deg, rgba(255,255,255,0.4), rgba(255,255,255,0) 40%)' }}
          />
          <div className="relative z-[2]">
            <p className="mb-4 font-mono text-[11px] tracking-[0.2em] text-steel uppercase">
              {staffMode ? 'On-spot booking' : 'Book a car'}
            </p>

            <LocationPicker
              prefix="pickup"
              mode={pickupMode}
              onModeChange={setPickupMode}
              state={pickupState}
              city={pickupCity}
              onStateChange={setPickupState}
              onCityChange={setPickupCity}
              airportText={pickupAirportText}
              onAirportChange={setPickupAirportText}
              onAirportPick={(a) => {
                setPickupAirport(a);
                setPickupAirportText(`${a.airportCode} - ${a.airportName}`);
              }}
            />

            <div className="mb-4 grid grid-cols-2 gap-2.5">
              <label className="field block">
                <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">Pickup date</span>
                <div className="relative">
                  <CalendarIcon className="pointer-events-none absolute left-[9px] top-1/2 -translate-y-1/2 opacity-45" width="13" height="13" />
                  <input type="date" className={`${fieldClass} pl-[30px]`} value={pickupDate} min={todayISO()} onChange={(e) => setPickupDate(e.target.value)} />
                </div>
              </label>
              <label className="field block">
                <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">Pickup time</span>
                <div className="relative">
                  <ClockIcon className="pointer-events-none absolute left-[9px] top-1/2 -translate-y-1/2 opacity-45" width="13" height="13" />
                  <input type="time" className={`${fieldClass} pl-[30px]`} value={pickupTime} onChange={(e) => setPickupTime(e.target.value)} />
                </div>
              </label>
            </div>

            <div className="mb-4 grid grid-cols-2 gap-2.5">
              <label className="field block">
                <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">Return date</span>
                <div className="relative">
                  <CalendarIcon className="pointer-events-none absolute left-[9px] top-1/2 -translate-y-1/2 opacity-45" width="13" height="13" />
                  <input type="date" className={`${fieldClass} pl-[30px]`} value={returnDate} min={pickupDate} onChange={(e) => setReturnDate(e.target.value)} />
                </div>
              </label>
              <label className="field block">
                <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">Return time</span>
                <div className="relative">
                  <ClockIcon className="pointer-events-none absolute left-[9px] top-1/2 -translate-y-1/2 opacity-45" width="13" height="13" />
                  <input type="time" className={`${fieldClass} pl-[30px]`} value={returnTime} onChange={(e) => setReturnTime(e.target.value)} />
                </div>
              </label>
            </div>

            <div className="mt-1 mb-1 border-t border-ink/8 pt-3.5">
              <div
                className="flex cursor-pointer items-center justify-between select-none"
                onClick={handleToggleDropoff}
              >
                <span className="text-[13.5px] text-ink/65">Return to a different location</span>
                <button
                  type="button"
                  className={`relative h-[23px] w-[42px] rounded-full transition-colors ${differentDropoff ? 'bg-primary' : 'bg-ink/15'}`}
                >
                  <span
                    className={`absolute top-0.5 left-0.5 h-[19px] w-[19px] rounded-full bg-white shadow-[0_1px_3px_rgba(0,0,0,0.25)] transition-transform ${differentDropoff ? 'translate-x-[19px]' : ''
                      }`}
                  />
                </button>
              </div>
            </div>

            {differentDropoff && (
              <div className="anim-rise-in mt-4">
                <LocationPicker
                  prefix="dropoff"
                  mode={dropoffMode}
                  onModeChange={setDropoffMode}
                  state={dropoffState}
                  city={dropoffCity}
                  onStateChange={setDropoffState}
                  onCityChange={setDropoffCity}
                  airportText={dropoffAirportText}
                  onAirportChange={setDropoffAirportText}
                  onAirportPick={(a) => {
                    setDropoffAirport(a);
                    setDropoffAirportText(`${a.airportCode} - ${a.airportName}`);
                  }}
                />
              </div>
            )}

            <button
              type="submit"
              className="mt-4.5 flex w-full items-center justify-center gap-2 rounded-full bg-primary py-3 text-[14.5px] font-medium text-white transition-[filter] hover:brightness-95"
            >
              <SearchIcon width="15" height="15" />
              Continue Booking
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
