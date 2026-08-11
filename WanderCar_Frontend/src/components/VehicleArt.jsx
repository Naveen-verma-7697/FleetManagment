const palette = {
  Economy: "#fcfcfc",
  Compact: "#e6e8ef",
  Sedan: "#f6f6f8",
  SUV: "#fcfffe",
  Luxury: "#f4f5f8",
  Minivan: "#fefcfa",
};

const images = {
  Economy: "/cars/BalenoFinal.jpg",
  Compact: "/cars/Hundai.jpg",
  Sedan: "/cars/HondaCity.jpg",
  SUV: "/cars/Suv.png",
  Luxury: "/cars/luxury-car-png.png",
  Minivan: "/cars/Minivanimg.jpg",
};
export default function VehicleArt({ carTypeName, className = "", imageClassName = "" }) {
  const bg = palette[carTypeName] || "#2B3A67";
  const image = images[carTypeName];

  return (
    <div
     className={`grid h-24 w-36 shrink-0 place-items-center rounded-2xl ${className}`}
      style={{ background: `${bg}1a` }}
    >
      <img
  src={image}
  alt={carTypeName}
  className={imageClassName || "h-24 w-36 object-contain"}
/>
    </div>
  );
}