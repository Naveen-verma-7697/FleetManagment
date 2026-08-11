import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import StepBar from '../components/StepBar';
import SideSummary from '../components/booking/SideSummary';
import { useBooking } from '../context/BookingContext';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { getCityById } from '../api/locationService';

const fieldClass =
  'w-full rounded-[11px] border border-ink/10 bg-white/70 px-[11px] py-2.5 text-[13.5px] text-ink outline-none focus:border-steel';

const MAX_DOCUMENT_BYTES = 5 * 1024 * 1024; // 5 MB

function Field({ label, ...props }) {
  return (
    <label className="field block">
      <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">{label}</span>
      <input className={fieldClass} {...props} />
    </label>
  );
}

function readFileAsBase64(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(String(reader.result).split(',')[1] || '');
    reader.onerror = () => reject(reader.error);
    reader.readAsDataURL(file);
  });
}

export default function CustomerInfoPage() {
  const { draft, setCustomer } = useBooking();
  const { customer: loggedInCustomer } = useAuth();
  const navigate = useNavigate();
  const toast = useToast();
  const [uploading, setUploading] = useState(false);

  useEffect(() => {
    if (!draft.carType) {
      navigate('/vehicles');
    }
  }, [draft.carType, navigate]);

  // A logged-in customer shouldn't have to retype details they already
  // gave us - every field the backend actually stores gets prefilled here,
  // once, the first time this page is reached with an empty draft. Fields
  // with no backend equivalent (cell number, licence-issuing state, card
  // details) are left for the customer to fill in as before.
  useEffect(() => {
    if (loggedInCustomer && !draft.customer.email) {
      const [first, ...rest] = (loggedInCustomer.fullName || '').split(' ');
      const city = loggedInCustomer.cityId ? getCityById(loggedInCustomer.cityId) : null;
      setCustomer({
        firstName: first || '',
        lastName: rest.join(' '),
        email: loggedInCustomer.email || '',
        phone: loggedInCustomer.phone || '',
        address1: loggedInCustomer.address1 || '',
        city: city?.cityName || '',
        zip: loggedInCustomer.pincode || '',
        drivingLicenseNo: loggedInCustomer.drivingLicenseNo || '',
        govtIdDocumentName: loggedInCustomer.govtIdDocumentName || '',
        govtIdDocumentType: loggedInCustomer.govtIdDocumentType || 'DRIVING_LICENSE',
      });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [loggedInCustomer]);

  async function handleFileChange(e) {
    const file = e.target.files?.[0];
    e.target.value = ''; // allow re-choosing the same file later
    if (!file) return;
    if (file.type !== 'application/pdf') {
      toast('Please upload a PDF file');
      return;
    }
    if (file.size > MAX_DOCUMENT_BYTES) {
      toast('That file is too large — please keep it under 5 MB');
      return;
    }
    setUploading(true);
    try {
      const base64 = await readFileAsBase64(file);
      set({ govtIdDocument: base64, govtIdDocumentName: file.name });
    } catch {
      toast('Could not read that file — please try again');
    } finally {
      setUploading(false);
    }
  }

  function handleContinue() {
    const c = draft.customer;
    if (!c.firstName.trim() || !c.lastName.trim() || !c.email.trim() || !c.phone.trim() || !c.drivingLicenseNo.trim()) {
      toast('Fill in name, email,phone number, and driving licence to continue');
      return;
    }
    if (!/^[6-9]\d{9}$/.test(c.phone.trim())) {
      toast("Enter a valid 10-digit phone number.");
      return;
    }
    navigate('/booking/confirm');
  }

  const c = draft.customer;
  const set = (patch) => setCustomer(patch);

  return (
    <div className="anim-screen-fade mx-auto max-w-[1120px] px-6 pt-14 pb-28 md:px-8">
      <button type="button" onClick={() => navigate('/booking/add-ons')} className="mb-5.5 flex items-center gap-1.5 text-[13.5px] font-medium text-ink/55 hover:text-ink">
        &larr; Back to add-ons
      </button>
      <StepBar current={4} />
      <div className="mb-9">
        <h1 className="mb-2 font-display text-[26px] font-semibold tracking-[-0.01em] md:text-[30px]">Your details</h1>
        <p className="max-w-135 text-[15px] text-ink/55">
          We'll use this to confirm your reservation and have the paperwork ready at pickup.
        </p>
      </div>

      <div className="grid items-start gap-7 md:grid-cols-[1fr_300px]">
        <div className="rounded-[22px] border border-line bg-[rgba(212,215,220,0.7)] p-6.5 shadow-[0_6px_20px_rgba(18,21,28,0.05)]">
          <div className="mb-3.5 grid grid-cols-1 gap-3.5 sm:grid-cols-2">
            <Field label="First name *" placeholder="Gryffindors" required value={c.firstName} onChange={(e) => set({ firstName: e.target.value })} />
            <Field label="Last name *" placeholder="Gryffindors" required value={c.lastName} onChange={(e) => set({ lastName: e.target.value })} />
          </div>
          <div className="mb-3.5">
            <Field label="Address line 1" placeholder="10,SMVITA,Mumbai, Gulmahar Road" value={c.address1} onChange={(e) => set({ address1: e.target.value })} />
          </div>
          <div className="mb-3.5 grid grid-cols-1 gap-3.5 sm:grid-cols-3">
            <Field label="City" placeholder="Mumbai" value={c.city} onChange={(e) => set({ city: e.target.value })} />
            <Field label="PIN Code" placeholder="400001" value={c.zip} onChange={(e) => set({ zip: e.target.value })} />
            <Field label="Email" type="email" placeholder="Team2@email.com" value={c.email} onChange={(e) => set({ email: e.target.value })} />
          </div>
          <div className="mb-3.5 grid grid-cols-1 gap-3.5 sm:grid-cols-2">
            <Field label="Phone *" type="tel" placeholder="+91 XXXXX XXXXX" required value={c.phone} onChange={(e) => set({ phone: e.target.value })} />
            <Field label="Cell" type="tel" placeholder="+91 XXXXX XXXXX" value={c.cell} onChange={(e) => set({ cell: e.target.value })} />
          </div>
          <div className="mb-3.5 grid grid-cols-1 gap-3.5 sm:grid-cols-3">
            <Field label="Driving licence no *" placeholder="MH14XXXXXXXXXX" value={c.drivingLicenseNo} onChange={(e) => set({ drivingLicenseNo: e.target.value })} />
            <Field label="Issued by" placeholder="Maharashtra RTO" value={c.drivingLicenseState} onChange={(e) => set({ drivingLicenseState: e.target.value })} />
            <Field label="Valid thru" type="date" value={c.drivingLicenseExp} onChange={(e) => set({ drivingLicenseExp: e.target.value })} />
          </div>
          <div className="mb-3.5 grid grid-cols-1 gap-3.5 sm:grid-cols-2">
            <label className="field block">
              <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">Card type</span>
              <select className={`${fieldClass} appearance-none`} value={c.cardType} onChange={(e) => set({ cardType: e.target.value })}>
                <option>Visa</option>
                <option>Mastercard</option>
                <option>Amex</option>
              </select>
            </label>
            <Field label="Card number" placeholder="xxxx xxxx xxxx 4021" value={c.cardNumber} onChange={(e) => set({ cardNumber: e.target.value })} />
          </div>

          <div className="mt-1 border-t border-ink/8 pt-4">
            <p className="mb-1 text-[13.5px] font-semibold text-ink">Government ID</p>
            <p className="mb-3 text-[12px] text-ink/50">Driving licence, Aadhaar card, PAN card, or passport — PDF only, up to 5 MB.</p>
            <div className="grid grid-cols-1 gap-3.5 sm:grid-cols-2">
              <label className="field block">
                <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">Document type</span>
                <select
                  className={`${fieldClass} appearance-none`}
                  value={c.govtIdDocumentType}
                  onChange={(e) => set({ govtIdDocumentType: e.target.value })}
                >
                  <option value="DRIVING_LICENSE">Driving licence</option>
                  <option value="AADHAAR">Aadhaar card</option>
                  <option value="PAN">PAN card</option>
                  <option value="PASSPORT">Passport</option>
                  <option value="OTHER">Other</option>
                </select>
              </label>
              <label className="field block">
                <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">
                  {c.govtIdDocumentName ? 'Replace document' : 'Upload document'}
                </span>
                <input
                  type="file"
                  accept="application/pdf"
                  onChange={handleFileChange}
                  className="w-full rounded-[11px] border border-dashed border-ink/15 bg-white/70 px-[11px] py-2 text-[12.5px] text-ink/70 outline-none file:mr-2.5 file:rounded-full file:border-0 file:bg-ink/6 file:px-3 file:py-1 file:text-[12px] file:font-medium file:text-ink"
                />
              </label>
            </div>
            {uploading && <p className="mt-2 text-[12px] text-ink/45">Reading file…</p>}
            {!uploading && c.govtIdDocumentName && (
              <p className="mt-2 text-[12.5px] text-primary">📄 {c.govtIdDocumentName} on file</p>
            )}
          </div>
        </div>
        <SideSummary />
      </div>

      <div className="mt-7 flex flex-wrap gap-3">
        <button
          type="button"
          onClick={handleContinue}
          className="flex items-center gap-1.5 rounded-full bg-primary px-6.5 py-3 text-[14.5px] font-semibold text-white shadow-[0_4px_14px_rgba(47,111,237,0.3)] transition-[filter] hover:brightness-95"
        >
          Continue booking
        </button>
        <button
          type="button"
          onClick={() => navigate('/')}
          className="rounded-full border border-line bg-white px-5.5 py-2.5 text-sm font-medium text-ink transition-colors hover:border-ink/30 hover:bg-paper"
        >
          Cancel
        </button>
      </div>
    </div>
  );
}
