export default function CarSvg(props) {
  return (
    <svg viewBox="0 0 700 320" xmlns="http://www.w3.org/2000/svg" {...props}>
      <defs>
        <linearGradient id="paint" x1="0%" y1="0%" x2="0%" y2="100%">
          <stop offset="0%" stopColor="#3a4a6b" />
          <stop offset="55%" stopColor="#1c2436" />
          <stop offset="100%" stopColor="#0d1119" />
        </linearGradient>
        <linearGradient id="glass" x1="0%" y1="0%" x2="0%" y2="100%">
          <stop offset="0%" stopColor="#8fa3c9" stopOpacity="0.85" />
          <stop offset="100%" stopColor="#22283a" stopOpacity="0.95" />
        </linearGradient>
        <linearGradient id="chrome" x1="0%" y1="0%" x2="100%" y2="0%">
          <stop offset="0%" stopColor="#e7ebf2" />
          <stop offset="50%" stopColor="#aab3c4" />
          <stop offset="100%" stopColor="#e7ebf2" />
        </linearGradient>
        <radialGradient id="ground" cx="50%" cy="50%" r="50%">
          <stop offset="0%" stopColor="#12151C" stopOpacity="0.18" />
          <stop offset="100%" stopColor="#12151C" stopOpacity="0" />
        </radialGradient>
        <radialGradient id="headlamp" cx="50%" cy="50%" r="50%">
          <stop offset="0%" stopColor="#fff8ee" stopOpacity="0.95" />
          <stop offset="100%" stopColor="#fff8ee" stopOpacity="0" />
        </radialGradient>
      </defs>

      <ellipse cx="350" cy="272" rx="300" ry="24" fill="url(#ground)" />

      <path
        d="M40 210
           C 45 180, 90 175, 140 168
           C 170 130, 240 100, 330 96
           C 420 92, 500 104, 560 132
           C 610 140, 655 158, 672 182
           C 682 196, 678 214, 662 218
           L 610 218
           C 606 194, 584 178, 560 178
           C 536 178, 516 194, 512 218
           L 232 218
           C 228 194, 206 178, 182 178
           C 158 178, 138 194, 134 218
           L 55 218
           C 44 218, 38 216, 40 210 Z"
        fill="url(#paint)"
      />

      <path
        d="M140 172 C 240 150, 420 148, 600 168"
        fill="none"
        stroke="url(#chrome)"
        strokeWidth="3"
        opacity="0.7"
      />

      <path
        d="M188 166 C 232 122, 288 100, 335 98 C 385 96, 440 106, 480 128 L 560 156 C 470 148, 320 148, 240 168 Z"
        fill="url(#glass)"
      />
      <path d="M330 100 L 335 158" stroke="#0d1119" strokeWidth="3" opacity="0.5" />

      <path
        d="M230 128 C 270 106, 320 98, 340 98"
        fill="none"
        stroke="#8fa3c9"
        strokeWidth="2"
        opacity="0.5"
      />

      <circle cx="656" cy="182" r="22" fill="url(#headlamp)" />
      <path d="M648 176 L 668 180 L 666 190 L 646 188 Z" fill="#fffaf2" opacity="0.9" />

      <rect x="46" y="176" width="10" height="14" rx="3" fill="#FF6B35" opacity="0.85" />

      <path d="M400 168 L 396 218" stroke="#0d1119" strokeWidth="2" opacity="0.4" />
      <rect x="360" y="182" width="22" height="5" rx="2.5" fill="#aab3c4" opacity="0.8" />

      <circle cx="182" cy="218" r="34" fill="#0d1119" stroke="url(#chrome)" strokeWidth="5" />
      <circle cx="182" cy="218" r="13" fill="#3a4a6b" />
      <circle cx="182" cy="218" r="4" fill="#aab3c4" />

      <circle cx="560" cy="218" r="34" fill="#0d1119" stroke="url(#chrome)" strokeWidth="5" />
      <circle cx="560" cy="218" r="13" fill="#3a4a6b" />
      <circle cx="560" cy="218" r="4" fill="#aab3c4" />
    </svg>
  );
}
