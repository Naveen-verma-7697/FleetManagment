import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import StepBar from "../components/StepBar";
import { MapPinIcon, ClockIcon } from "../components/icons/Icons";
import * as locationService from "../api/locationService";
import { useBooking } from "../context/BookingContext";

export default function HubSelectionPage() {
  const { draft, setSearch } = useBooking();
  const navigate = useNavigate();
  const { search } = draft;
  const needsPickupHub =
    search.pickupMode === "city";

  const needsReturnHub =
    search.differentDropoff &&
    search.dropoffMode === "city";

  const [selectedPickupHub, setSelectedPickupHub] = useState(null);
  const [selectedReturnHub, setSelectedReturnHub] = useState(null);

  useEffect(() => {

    if (
      !needsPickupHub &&
      !needsReturnHub
    ) {
      navigate("/vehicles", {
        replace: true,
      });
      return;
    }

    if (
      needsPickupHub &&
      !search.pickupCityId
    ) {
      navigate("/", {
        replace: true,
      });
    }

  }, [
    needsPickupHub,
    needsReturnHub,
    search.pickupCityId,
    navigate,
  ]);

  const pickupHubs =
    needsPickupHub
      ? locationService.findHubForCity(
        search.pickupCityId
      )
      : [];

  const returnHubs =
    needsReturnHub
      ? locationService.findHubForCity(
        search.dropoffCityId
      )
      : [];

  function continueBooking() {
    if (
      needsPickupHub &&
      !selectedPickupHub
    ) {
      alert("Please select a pickup hub.");
      return;
    }

    if (
      needsReturnHub &&
      !selectedReturnHub
    ) {
      alert("Please select a return hub.");
      return;
    }

    setSearch({
      pickupHubId:
    needsPickupHub
        ? selectedPickupHub.hubId
        : search.pickupHubId,
      dropoffHubId:
    needsReturnHub
        ? selectedReturnHub.hubId
        : search.dropoffHubId,
    });

    navigate("/vehicles");
  }

  return (
    <div className="min-h-screen bg-slate-50">
      <div className="mx-auto max-w-7xl px-8 py-12">

        <button
          onClick={() => navigate("/")}
          className="mb-6 text-sm font-medium text-slate-500 hover:text-primary"
        >
          ← Back
        </button>

        <StepBar current={1} />

        <h1 className="mt-8 text-4xl font-bold tracking-tight text-slate-900">
          Choose Pickup & Return Locations
        </h1>

        <p className="mt-2 mb-10 text-lg text-slate-500">
          Select the most convenient hubs for your journey.
        </p>

        <div
          className={`grid gap-8 ${search.differentDropoff
              ? "grid-cols-1 lg:grid-cols-2"
              : "grid-cols-1"
            }`}
        >

          {/* Pickup Card */}
{needsPickupHub && (
          <div className="rounded-3xl border border-slate-200 bg-white p-7 shadow-sm">

            <div className="mb-7 border-b border-slate-100 pb-5">
              <p className="text-sm font-semibold uppercase tracking-wider text-primary">
                Pickup
              </p>

              <h2 className="mt-2 text-2xl font-bold">
                {search.pickupCity}
              </h2>

              <p className="mt-1 text-sm text-slate-500">
                {pickupHubs.length} hub
                {pickupHubs.length !== 1 ? "s" : ""} available
              </p>
            </div>

            <div className="max-h-[600px] space-y-5 overflow-y-auto pr-2">

              {pickupHubs.map((hub) => {

                const city = locationService.getCityById(hub.cityId);
                const state = locationService.getStateById(hub.stateId);

                return (
                  <div
                    key={hub.hubId}
                    onClick={() => setSelectedPickupHub(hub)}
                    className={`relative cursor-pointer rounded-2xl border p-6 transition-all duration-300 hover:-translate-y-1 hover:border-primary hover:shadow-xl ${selectedPickupHub?.hubId === hub.hubId
                        ? "border-primary bg-blue-50 shadow-lg ring-2 ring-primary/20"
                        : "border-slate-200"
                      }`}
                  >

                    {selectedPickupHub?.hubId === hub.hubId && (
                      <span className="absolute right-5 top-5 rounded-full bg-primary px-3 py-1 text-xs font-medium text-white">
                        Selected
                      </span>
                    )}

                    <h3 className="text-xl font-semibold">
                      {hub.hubName}
                    </h3>

                    <p className="mt-2 text-slate-500">
                      {city?.cityName}, {state?.stateName}
                    </p>

                    <div className="mt-5 flex items-start gap-4">

                      <div className="flex h-10 w-10 items-center justify-center rounded-full bg-slate-100">
                        <MapPinIcon width="18" height="18" />
                      </div>

                      <span>{hub.address}</span>

                    </div>

                    <div className="mt-4 flex items-center gap-4">

                      <div className="flex h-10 w-10 items-center justify-center rounded-full bg-slate-100">
                        <ClockIcon width="18" height="18" />
                      </div>

                      <span>{hub.contactNo}</span>

                    </div>

                  </div>
                );
              })}

            </div>

          </div>
)}
          {/* Return Card */}

          {needsReturnHub && (
            <div className="rounded-3xl border border-slate-200 bg-white p-7 shadow-sm">

              <div className="mb-7 border-b border-slate-100 pb-5">
                <p className="text-sm font-semibold uppercase tracking-wider text-primary">
                  Return
                </p>

                <h2 className="mt-2 text-2xl font-bold">
                  {search.dropoffCity}
                </h2>

                <p className="mt-1 text-sm text-slate-500">
                  {returnHubs.length} hub
                  {returnHubs.length !== 1 ? "s" : ""} available
                </p>
              </div>

              <div className="max-h-[600px] space-y-5 overflow-y-auto pr-2">

                {returnHubs.map((hub) => {

                  const city = locationService.getCityById(hub.cityId);
                  const state = locationService.getStateById(hub.stateId);

                  return (
                    <div
                      key={hub.hubId}
                      onClick={() => setSelectedReturnHub(hub)}
                      className={`relative cursor-pointer rounded-2xl border p-6 transition-all duration-300 hover:-translate-y-1 hover:border-primary hover:shadow-xl ${selectedReturnHub?.hubId === hub.hubId
                          ? "border-primary bg-blue-50 shadow-lg ring-2 ring-primary/20"
                          : "border-slate-200"
                        }`}
                    >

                      {selectedReturnHub?.hubId === hub.hubId && (
                        <span className="absolute right-5 top-5 rounded-full bg-primary px-3 py-1 text-xs font-medium text-white">
                          Selected
                        </span>
                      )}

                      <h3 className="text-xl font-semibold">
                        {hub.hubName}
                      </h3>

                      <p className="mt-2 text-slate-500">
                        {city?.cityName}, {state?.stateName}
                      </p>

                      <div className="mt-5 flex items-start gap-4">

                        <div className="flex h-10 w-10 items-center justify-center rounded-full bg-slate-100">
                          <MapPinIcon width="18" height="18" />
                        </div>

                        <span>{hub.address}</span>

                      </div>

                      <div className="mt-4 flex items-center gap-4">

                        <div className="flex h-10 w-10 items-center justify-center rounded-full bg-slate-100">
                          <ClockIcon width="18" height="18" />
                        </div>

                        <span>{hub.contactNo}</span>

                      </div>

                    </div>
                  );
                })}

              </div>

            </div>
          )}

        </div>

        <div className="mt-12 flex justify-center">
          <button
            onClick={continueBooking}
            className="rounded-full bg-primary px-12 py-4 text-lg font-semibold text-white shadow-lg transition-all duration-300 hover:scale-105 hover:shadow-xl"
          >
            Continue Booking →
          </button>
        </div>

      </div>
    </div>
  );
}