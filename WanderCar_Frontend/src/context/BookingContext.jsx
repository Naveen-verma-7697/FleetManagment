import { createContext, useCallback, useContext, useMemo, useState } from 'react';
import { calculateRentalAmount } from '../utils/pricing';

const BookingContext = createContext(null);

const emptySearch = {
  pickupMode: 'city',
  pickupState: '',
  pickupCity: '',
  pickupCityId: null, 
  pickupAirport: '',
  pickupHubId: null,
  pickupDate: '',
  pickupTime: '10:00',
  returnDate: '',
  returnTime: '10:00',
  differentDropoff: false,
  dropoffMode: 'city',
  dropoffState: '',
  dropoffCityId: null,  
  dropoffCity: '',
  dropoffAirport: '',
  dropoffHubId: null,
};

const initialDraft = {
  search: emptySearch,
  // The reserved category (a CarTypeDTO with availableCount for the
  // chosen hub/dates) — not a specific vehicle. The actual car is only
  // assigned by staff at hand-over, long after this booking is created.
  carType: null,
  addonQuantities: {}, // addonId -> quantity (0 = not selected)
  customer: {
    firstName: '',
    lastName: '',
    address1: '',
    city: '',
    zip: '',
    email: '',
    phone: '',
    cell: '',
    drivingLicenseNo: '',
    drivingLicenseState: '',
    drivingLicenseExp: '2030-05-31',
    cardType: 'Visa',
    cardNumber: '',
    // Government ID upload (driving licence / Aadhaar / PAN / passport).
    // govtIdDocument is the base64 PDF payload - empty until a new file is
    // chosen; govtIdDocumentName/Type describe whatever is currently on
    // file (prefilled from the logged-in customer's record, if any).
    govtIdDocument: '',
    govtIdDocumentName: '',
    govtIdDocumentType: 'DRIVING_LICENSE',
  },
};

export function daysBetween(pickupDatetime, returnDatetime) {
  if (!pickupDatetime || !returnDatetime) return 1;
  const ms = new Date(returnDatetime) - new Date(pickupDatetime);
  return Math.max(1, Math.ceil(ms / (1000 * 60 * 60 * 24)));
  
}

export function BookingProvider({ children }) {
  const [draft, setDraft] = useState(initialDraft);
  const [addons, setAddonsList] = useState([]);
  const [staffMode, setStaffMode] = useState(false);
  const [confirmedBooking, setConfirmedBooking] = useState(null);
  // Non-null while the customer is changing one part (category or add-ons)
  // of an EXISTING booking from the modify-booking page, rather than
  // building a brand new one. VehicleSelectionPage/AddOnsPage check this
  // to swap their "Continue booking" flow for a "save this one change and
  // go back to the booking" flow that calls modifyBooking directly instead
  // of walking through add-ons/details/confirm all over again.
  const [editingBooking, setEditingBooking] = useState(null);

  const [prefillVersion, setPrefillVersion] = useState(0);

  const setSearch = useCallback((patch) => {
    setDraft((d) => ({ ...d, search: { ...d.search, ...patch } }));
  }, []);

  const applyPrefill = useCallback((patch) => {
    setDraft((d) => ({ ...d, search: { ...d.search, ...patch } }));
    setPrefillVersion((v) => v + 1);
  }, []);

  const selectCarType = useCallback((carType) => {
    setDraft((d) => ({ ...d, carType }));
  }, []);

  const setAddonsCatalog = useCallback((list) => setAddonsList(list), []);

  const setAddonQuantity = useCallback((addonId, quantity) => {
    setDraft((d) => ({
      ...d,
      addonQuantities: { ...d.addonQuantities, [addonId]: quantity },
    }));
  }, []);

  const setCustomer = useCallback((patch) => {
    setDraft((d) => ({ ...d, customer: { ...d.customer, ...patch } }));
  }, []);

  const resetDraft = useCallback(() => setDraft(initialDraft), []);

  // Loads an existing booking's hub/dates/category/add-ons into the same
  // draft shape a new booking uses, and flags editingBooking so the
  // vehicle and add-ons pages know to save-and-return instead of
  // continuing the multi-step flow. quantitiesByAddonId lets
  // ModifyCancelPage pass the booking's current add-on lines without
  // needing the add-on catalog loaded yet (AddOnsPage loads it itself and
  // these ids still line up).
  const beginEditBooking = useCallback((booking, quantitiesByAddonId = {}) => {
    setDraft((d) => ({
      ...d,
      search: {
        ...d.search,
        pickupMode: 'city',
        pickupHubId: booking.pickupHubId,
        dropoffHubId: booking.dropHubId,
        differentDropoff: booking.pickupHubId !== booking.dropHubId,
        pickupDate: booking.pickupDatetime?.slice(0, 10) || '',
        pickupTime: booking.pickupDatetime?.slice(11, 16) || '10:00',
        returnDate: booking.returnDatetime?.slice(0, 10) || '',
        returnTime: booking.returnDatetime?.slice(11, 16) || '10:00',
      },
      carType: booking.carType,
      addonQuantities: quantitiesByAddonId,
    }));
    setEditingBooking({ confirmationNo: booking.confirmationNo });
  }, []);

  const clearEditingBooking = useCallback(() => setEditingBooking(null), []);

  const pickupDatetime = useMemo(() => {
    if (!draft.search.pickupDate) return null;
    return `${draft.search.pickupDate}T${draft.search.pickupTime || '10:00'}:00`;
  }, [draft.search.pickupDate, draft.search.pickupTime]);

  const returnDatetime = useMemo(() => {
    if (!draft.search.returnDate) return null;
    return `${draft.search.returnDate}T${draft.search.returnTime || '10:00'}:00`;
  }, [draft.search.returnDate, draft.search.returnTime]);

  const days = useMemo(
    () => daysBetween(pickupDatetime, returnDatetime),
    [pickupDatetime, returnDatetime]
  );

  const selectedAddonLines = useMemo(() => {
    return addons
      .filter((a) => (draft.addonQuantities[a.addonId] || 0) > 0)
      .map((a) => ({
        addon: a,
        quantity: draft.addonQuantities[a.addonId],
        subtotal: a.dailyRate * draft.addonQuantities[a.addonId] * days,
      }));
  }, [addons, draft.addonQuantities, days]);

  // const totals = useMemo(() => {
  //   const rentalAmount = draft.carType ? draft.carType.dailyRate * days : 0;
  //   const addonAmount = selectedAddonLines.reduce((sum, l) => sum + l.subtotal, 0);
  //   return { rentalAmount, addonAmount, total: rentalAmount + addonAmount, days };
  // }, [draft.carType, days, selectedAddonLines]);
const totals = useMemo(() => {
  const rentalAmount = calculateRentalAmount(draft.carType, days);

  const addonAmount = selectedAddonLines.reduce(
    (sum, line) => sum + line.subtotal,
    0
  );

  return {
    rentalAmount,
    addonAmount,
    total: rentalAmount + addonAmount,
    days,
  };
}, [draft.carType, days, selectedAddonLines]);
  const value = useMemo(
    () => ({
      draft,
      addons,
      staffMode,
      setStaffMode,
      prefillVersion,
      confirmedBooking,
      setConfirmedBooking,
      editingBooking,
      beginEditBooking,
      clearEditingBooking,
      setSearch,
      applyPrefill,
      selectCarType,
      setAddonsCatalog,
      setAddonQuantity,
      setCustomer,
      resetDraft,
      pickupDatetime,
      returnDatetime,
      days,
      selectedAddonLines,
      totals,
    }),
    [
      draft,
      addons,
      staffMode,
      prefillVersion,
      confirmedBooking,
      editingBooking,
      beginEditBooking,
      clearEditingBooking,
      setSearch,
      applyPrefill,
      selectCarType,
      setAddonsCatalog,
      setAddonQuantity,
      setCustomer,
      resetDraft,
      pickupDatetime,
      returnDatetime,
      days,
      selectedAddonLines,
      totals,
    ]
  );

  return <BookingContext.Provider value={value}>{children}</BookingContext.Provider>;
}

export function useBooking() {
  const ctx = useContext(BookingContext);
  if (!ctx) throw new Error('useBooking must be used within BookingProvider');
  return ctx;
}
