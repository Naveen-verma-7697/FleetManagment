import { useState } from 'react';
import { MailIcon, HeadsetIcon, MapPinIcon, ClockIcon } from '../components/icons/Icons';
import { useToast } from '../context/ToastContext';

const CONTACT = {
  phone: '+91 1800-123-5566',
  email: 'TEAM2@gmail.com',
  address: '45,SmVita gulmohar road Mumbai, Maharashtra 400001',
  hours: 'Every day, 24/7',
};

function ContactCard({ icon: Icon, label, value, href }) {
  return (
    <div className="flex items-start gap-3.5 rounded-2xl border border-line bg-white/70 p-5">
      <span className="grid h-10 w-10 shrink-0 place-items-center rounded-full bg-steel/10 text-steel">
        <Icon width="18" height="18" />
      </span>
      <div>
        <p className="text-[12.5px] font-medium text-ink/50">{label}</p>
        {href ? (
          <a href={href} className="mt-0.5 block text-[15px] font-semibold text-ink no-underline hover:text-primary">
            {value}
          </a>
        ) : (
          <p className="mt-0.5 text-[15px] font-semibold text-ink">{value}</p>
        )}
      </div>
    </div>
  );
}

export default function CustomerCarePage() {
  const toast = useToast();
  const [message, setMessage] = useState('');
  const [sent, setSent] = useState(false);

  function handleSend(e) {
    e.preventDefault();
    if (!message.trim()) return;
    setSent(true);
    setMessage('');
    toast("Message sent — our team will get back to you shortly");
  }

  return (
    <div className="mx-auto max-w-[900px] px-6 py-14 md:px-8">
      <div className="mb-10 text-center">
        <h1 className="font-display text-[30px] font-semibold tracking-[-0.01em] md:text-[36px]">Customer Care</h1>
        <p className="mx-auto mt-3 max-w-120 text-[15px] text-ink/55">
          Questions about a booking, a hub, or anything else — we're here to help.
        </p>
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <ContactCard icon={HeadsetIcon} label="Call us" value={CONTACT.phone} href={`tel:${CONTACT.phone.replace(/[^+\d]/g, '')}`} />
        <ContactCard icon={MailIcon} label="Email us" value={CONTACT.email} href={`mailto:${CONTACT.email}`} />
        <ContactCard icon={MapPinIcon} label="Head office" value={CONTACT.address} />
        <ContactCard icon={ClockIcon} label="Support hours" value={CONTACT.hours} />
      </div>

      <div className="mt-8 rounded-2xl border border-line bg-white/70 p-6">
        <h2 className="font-display text-[18px] font-semibold">Chat with us</h2>
        <p className="mt-1 text-[13px] text-ink/50">
          Leave a message and someone from the team will follow up by email.
        </p>
        <form onSubmit={handleSend} className="mt-4">
          <textarea
            rows={4}
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            placeholder="How can we help?"
            className="w-full resize-none rounded-2xl border border-ink/10 bg-white/80 px-4 py-3 text-[13.5px] text-ink outline-none focus:border-steel"
          />
          <div className="mt-3 flex items-center justify-between">
            {sent ? <span className="text-[12.5px] text-primary">Thanks — we've got your message.</span> : <span />}
            <button
              type="submit"
              className="rounded-full bg-primary px-6 py-2.5 text-[13.5px] font-semibold text-white transition-[filter] hover:brightness-95"
            >
              Send message
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
