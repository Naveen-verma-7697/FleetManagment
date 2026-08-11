import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import * as locationService from '../api/locationService';
import * as customerService from '../api/customerService';

const fieldClass =
  'w-full rounded-[11px] border border-ink/10 bg-white/70 px-[11px] py-2.5 text-[13.5px] text-ink outline-none focus:border-steel disabled:opacity-60';

const MAX_DOCUMENT_BYTES = 5 * 1024 * 1024;

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

// View and edit the logged-in customer's own profile - the same fields
// CustomerInfoPage collects during a booking, so a customer only ever
// has to enter this information once.
export default function ProfilePage() {
  const { customer, isAuthenticated, updateProfile } = useAuth();
  const navigate = useNavigate();
  const toast = useToast();

  const [form, setForm] = useState(null);
  const [states, setStates] = useState([]);
  const [cities, setCities] = useState([]);
  const [saving, setSaving] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [pendingDoc, setPendingDoc] = useState(null); // { govtIdDocument, govtIdDocumentName }

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/');
      return;
    }
    setForm({
      fullName: customer.fullName || '',
      phone: customer.phone || '',
      dateOfBirth: customer.dateOfBirth || '',
      drivingLicenseNo: customer.drivingLicenseNo || '',
      passportNo: customer.passportNo || '',
      address1: customer.address1 || '',
      address2: customer.address2 || '',
      stateId: customer.stateId || '',
      cityId: customer.cityId || '',
      pincode: customer.pincode || '',
    });
    locationService.getStates().then(setStates);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isAuthenticated]);

  useEffect(() => {
    if (!form?.stateId) {
      setCities([]);
      return;
    }
    locationService.getCitiesByState(form.stateId).then(setCities);
  }, [form?.stateId]);

  if (!isAuthenticated || !form) return null;

  function set(patch) {
    setForm((f) => ({ ...f, ...patch }));
  }

  async function handleFileChange(e) {
    const file = e.target.files?.[0];
    e.target.value = '';
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
      setPendingDoc({ govtIdDocument: base64, govtIdDocumentName: file.name });
    } catch {
      toast('Could not read that file — please try again');
    } finally {
      setUploading(false);
    }
  }

  async function handleViewDocument() {
    try {
      const doc = await customerService.getGovtIdDocument(customer.customerId);
      if (!doc?.govtIdDocument) {
        toast('No document on file yet');
        return;
      }
      const link = document.createElement('a');
      link.href = `data:application/pdf;base64,${doc.govtIdDocument}`;
      link.download = doc.govtIdDocumentName || 'government-id.pdf';
      link.click();
    } catch (err) {
      toast(err.message || 'Could not load that document');
    }
  }

  async function handleSave(e) {
    e.preventDefault();
    setSaving(true);
    try {
      await updateProfile({
        fullName: form.fullName,
        phone: form.phone,
        dateOfBirth: form.dateOfBirth || null,
        drivingLicenseNo: form.drivingLicenseNo,
        passportNo: form.passportNo,
        address1: form.address1,
        address2: form.address2,
        stateId: form.stateId || undefined,
        cityId: form.cityId || undefined,
        pincode: form.pincode,
        ...(pendingDoc
          ? { govtIdDocument: pendingDoc.govtIdDocument, govtIdDocumentName: pendingDoc.govtIdDocumentName, govtIdDocumentType: customer.govtIdDocumentType || 'DRIVING_LICENSE' }
          : {}),
      });
      setPendingDoc(null);
    } catch (err) {
      toast(err.message || 'Could not save your profile');
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="anim-screen-fade mx-auto max-w-[760px] px-6 pt-14 pb-28 md:px-8">
      <div className="mb-9">
        <h1 className="mb-2 font-display text-[26px] font-semibold tracking-[-0.01em] md:text-[30px]">Your profile</h1>
        <p className="text-[15px] text-ink/55">Keep your details up to date so bookings pre-fill correctly.</p>
      </div>

      <form onSubmit={handleSave} className="rounded-[22px] border border-line bg-white/70 p-6.5 shadow-[0_6px_20px_rgba(18,21,28,0.05)]">
        <div className="mb-3.5 grid grid-cols-1 gap-3.5 sm:grid-cols-2">
          <Field label="Full name" value={form.fullName} onChange={(e) => set({ fullName: e.target.value })} />
          <Field label="Email" value={customer.email} disabled />
        </div>
        <div className="mb-3.5 grid grid-cols-1 gap-3.5 sm:grid-cols-2">
          <Field label="Phone" type="tel" value={form.phone} onChange={(e) => set({ phone: e.target.value })} />
          <Field label="Date of birth" type="date" value={form.dateOfBirth || ''} onChange={(e) => set({ dateOfBirth: e.target.value })} />
        </div>
        <div className="mb-3.5 grid grid-cols-1 gap-3.5 sm:grid-cols-2">
          <Field label="Driving licence no" value={form.drivingLicenseNo} onChange={(e) => set({ drivingLicenseNo: e.target.value })} />
          <Field label="Passport no" value={form.passportNo} onChange={(e) => set({ passportNo: e.target.value })} />
        </div>
        <div className="mb-3.5">
          <Field label="Address line 1" value={form.address1} onChange={(e) => set({ address1: e.target.value })} />
        </div>
        <div className="mb-3.5">
          <Field label="Address line 2" value={form.address2} onChange={(e) => set({ address2: e.target.value })} />
        </div>
        <div className="mb-3.5 grid grid-cols-1 gap-3.5 sm:grid-cols-3">
          <label className="field block">
            <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">State</span>
            <select className={`${fieldClass} appearance-none`} value={form.stateId} onChange={(e) => set({ stateId: e.target.value, cityId: '' })}>
              <option value="">Select state</option>
              {states.map((s) => (
                <option key={s.stateId} value={s.stateId}>{s.stateName}</option>
              ))}
            </select>
          </label>
          <label className="field block">
            <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">City</span>
            <select className={`${fieldClass} appearance-none`} value={form.cityId} disabled={!form.stateId} onChange={(e) => set({ cityId: e.target.value })}>
              <option value="">Select city</option>
              {cities.map((c) => (
                <option key={c.cityId} value={c.cityId}>{c.cityName}</option>
              ))}
            </select>
          </label>
          <Field label="PIN code" value={form.pincode} onChange={(e) => set({ pincode: e.target.value })} />
        </div>

        <div className="mt-1 border-t border-ink/8 pt-4">
          <p className="mb-1 text-[13.5px] font-semibold text-ink">Government ID</p>
          <p className="mb-3 text-[12px] text-ink/50">Driving licence, Aadhaar card, PAN card, or passport — PDF only, up to 5 MB.</p>
          <div className="flex flex-wrap items-center gap-3">
            {customer.govtIdDocumentName && (
              <button type="button" onClick={handleViewDocument} className="rounded-full border border-line bg-white px-4 py-2 text-[12.5px] font-medium text-ink hover:border-ink/30">
                📄 View {customer.govtIdDocumentName}
              </button>
            )}
            <label className="cursor-pointer rounded-full bg-ink/6 px-4 py-2 text-[12.5px] font-medium text-ink hover:bg-ink/10">
              {customer.govtIdDocumentName ? 'Replace document' : 'Upload document'}
              <input type="file" accept="application/pdf" onChange={handleFileChange} className="hidden" />
            </label>
            {uploading && <span className="text-[12px] text-ink/45">Reading file…</span>}
            {pendingDoc && <span className="text-[12.5px] text-primary">{pendingDoc.govtIdDocumentName} ready to save</span>}
          </div>
        </div>

        <button
          type="submit"
          disabled={saving}
          className="mt-6 rounded-full bg-primary px-6.5 py-3 text-[14.5px] font-semibold text-white shadow-[0_4px_14px_rgba(47,111,237,0.3)] transition-[filter] hover:brightness-95 disabled:opacity-60"
        >
          {saving ? 'Saving…' : 'Save changes'}
        </button>
      </form>
    </div>
  );
}
