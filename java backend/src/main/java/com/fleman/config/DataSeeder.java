package com.fleman.config;

import com.fleman.entity.*;
import com.fleman.entity.BookingHeader.BookingStatus;
import com.fleman.entity.Car.CarStatus;
import com.fleman.entity.Car.FuelType;
import com.fleman.entity.Customer.CustomerStatus;
import com.fleman.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Populates the database with India-based fixture data, weighted toward
 * Maharashtra — 12 states, 38 cities (8 of them Maharashtra), 17 city hubs
 * plus 13 airport kiosks, 6 car types priced in INR, an original hand-picked
 * fleet of 18 cars (14 of them at Maharashtra hubs) topped up with enough
 * generated cars that every one of the 30 hubs — including every airport
 * kiosk — has at least {@link #MIN_CARS_PER_HUB_AND_TYPE} AVAILABLE cars of
 * every car type (a realistic, browsable inventory per category instead of
 * a token one or two), 5 add-ons, one sample customer, and the one sample
 * WDR-2894 booking that StaffReturnPage/ModifyCancelPage default their
 * confirmation-number field to.
 *
 * No entity relationships are used anywhere in this project — every
 * cross-table reference here is a plain id assigned by hand, matching the
 * DB design's "Logical Ref" pattern throughout (not just the few fields
 * the sheet originally marked "fk").
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final HubRepository hubRepository;
    private final AirportRepository airportRepository;
    private final CarTypeRepository carTypeRepository;
    private final CarRepository carRepository;
    private final AddonRepository addonRepository;
    private final CustomerRepository customerRepository;
    private final BookingHeaderRepository bookingHeaderRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final PasswordEncoder passwordEncoder;

    private final Map<Integer, Long> stateIds = new HashMap<>();
    private final Map<Integer, Long> cityIds = new HashMap<>();
    private final Map<Integer, Long> hubIds = new HashMap<>();
    private final Map<Integer, Long> carTypeIds = new HashMap<>();
    private final Map<Integer, Long> addonIds = new HashMap<>();

    // "<hubIndex>-<carTypeIndex>" -> how many hand-picked cars from
    // seedCars() already sit in that combo, so topUpFleetToMinimum() knows
    // exactly how many more it needs to generate rather than assuming zero.
    private final Map<String, Integer> handPickedCounts = new HashMap<>();

    public DataSeeder(StateRepository stateRepository, CityRepository cityRepository,
                       HubRepository hubRepository, AirportRepository airportRepository,
                       CarTypeRepository carTypeRepository, CarRepository carRepository,
                       AddonRepository addonRepository, CustomerRepository customerRepository,
                       BookingHeaderRepository bookingHeaderRepository,
                       BookingDetailRepository bookingDetailRepository,
                       PasswordEncoder passwordEncoder) {
        this.stateRepository = stateRepository;
        this.cityRepository = cityRepository;
        this.hubRepository = hubRepository;
        this.airportRepository = airportRepository;
        this.carTypeRepository = carTypeRepository;
        this.carRepository = carRepository;
        this.addonRepository = addonRepository;
        this.customerRepository = customerRepository;
        this.bookingHeaderRepository = bookingHeaderRepository;
        this.bookingDetailRepository = bookingDetailRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (stateRepository.count() > 0) return;

        seedStates();
        seedCities();
        seedHubs();
        seedAirportsAndKioskHubs();
        seedCarTypes();
        seedCars();
        topUpFleetToMinimum();
        seedAddons();
        seedCustomerAndSampleBooking();
    }

    private void seedStates() {
        // Index 1 = Maharashtra, deliberately first — most cities, hubs,
        // and cars below belong to it. Index 6 = Madhya Pradesh, added
        // for the Bhopal/Indore hubs. Indices 7-12 round out the map with
        // Rajasthan, West Bengal, Telangana, Uttar Pradesh, Punjab, and
        // Kerala, each getting at least one hub below.
        String[] names = {
                "Maharashtra", "Karnataka", "Delhi", "Gujarat", "Tamil Nadu", "Madhya Pradesh",
                "Rajasthan", "West Bengal", "Telangana", "Uttar Pradesh", "Punjab", "Kerala"
        };
        for (int i = 0; i < names.length; i++) {
            State s = new State(names[i]);
            stateIds.put(i + 1, stateRepository.save(s).getStateId());
        }
    }

    private void seedCities() {
        // { cityId (local index), cityName, stateId (local index) }
        Object[][] rows = {
                // Maharashtra — 8 cities, the bulk of the map
                {1, "Mumbai", 1}, {2, "Pune", 1}, {3, "Nagpur", 1}, {4, "Nashik", 1},
                {5, "Thane", 1}, {6, "Navi Mumbai", 1}, {7, "Chhatrapati Sambhajinagar", 1}, {8, "Kolhapur", 1},
                // Karnataka
                {9, "Bengaluru", 2}, {10, "Mysuru", 2}, {11, "Hubballi", 2},
                // Delhi
                {12, "New Delhi", 3}, {13, "Dwarka", 3}, {14, "Rohini", 3},
                // Gujarat
                {15, "Ahmedabad", 4}, {16, "Surat", 4}, {17, "Vadodara", 4},
                // Tamil Nadu
                {18, "Chennai", 5}, {19, "Coimbatore", 5}, {20, "Madurai", 5},
                // Madhya Pradesh
                {21, "Bhopal", 6}, {22, "Indore", 6}, {23, "Jabalpur", 6},
                // Rajasthan
                {24, "Jaipur", 7}, {25, "Udaipur", 7}, {26, "Jodhpur", 7},
                // West Bengal
                {27, "Kolkata", 8}, {28, "Howrah", 8}, {29, "Siliguri", 8},
                // Telangana
                {30, "Hyderabad", 9}, {31, "Warangal", 9},
                // Uttar Pradesh
                {32, "Lucknow", 10}, {33, "Noida", 10}, {34, "Kanpur", 10},
                // Punjab
                {35, "Ludhiana", 11}, {36, "Amritsar", 11},
                // Kerala
                {37, "Kochi", 12}, {38, "Thiruvananthapuram", 12},
        };
        for (Object[] r : rows) {
            City c = new City((String) r[1], stateIds.get((Integer) r[2]));
            cityIds.put((Integer) r[0], cityRepository.save(c).getCityId());
        }
    }

    private void seedHubs() {
        // { hubId (local index), hubName, address, cityId (local), stateId (local),
        //   pincode, contactNo, email }
        Object[][] rows = {
                {1, "WanderCar — Mumbai Hub", "Plot 14, Andheri-Kurla Road", 1, 1, "400059", "+91 22 4001 5566", "mumbai.hub@wandercar.example"},
                {2, "WanderCar — Pune Hub", "221, FC Road, Shivajinagar", 2, 1, "411005", "+91 20 4002 5566", "pune.hub@wandercar.example"},
                {3, "WanderCar — Nagpur Hub", "45, Wardha Road", 3, 1, "440012", "+91 712 400 5566", "nagpur.hub@wandercar.example"},
                {4, "WanderCar — Nashik Hub", "12, College Road", 4, 1, "422005", "+91 253 400 5566", "nashik.hub@wandercar.example"},
                {5, "WanderCar — Thane Hub", "8, Ghodbunder Road", 5, 1, "400607", "+91 22 4003 5566", "thane.hub@wandercar.example"},
                {6, "WanderCar — Bengaluru Hub", "77, Outer Ring Road", 9, 2, "560103", "+91 80 4004 5566", "bengaluru.hub@wandercar.example"},
                {7, "WanderCar — New Delhi Hub", "19, Connaught Place", 12, 3, "110001", "+91 11 4005 5566", "delhi.hub@wandercar.example"},
                {8, "WanderCar — Ahmedabad Hub", "5, SG Highway", 15, 4, "380015", "+91 79 4006 5566", "ahmedabad.hub@wandercar.example"},
                {9, "WanderCar — Chennai Hub", "31, Anna Salai", 18, 5, "600002", "+91 44 4007 5566", "chennai.hub@wandercar.example"},
                {10, "WanderCar — Bhopal Hub", "26, MP Nagar Zone II", 21, 6, "462011", "+91 755 400 5566", "bhopal.hub@wandercar.example"},
                {11, "WanderCar — Indore Hub", "9, Vijay Nagar Square", 22, 6, "452010", "+91 731 400 5566", "indore.hub@wandercar.example"},
                {12, "WanderCar — Jaipur Hub", "3, Malviya Nagar", 24, 7, "302017", "+91 141 400 5566", "jaipur.hub@wandercar.example"},
                {13, "WanderCar — Kolkata Hub", "58, Park Street", 27, 8, "700016", "+91 33 4008 5566", "kolkata.hub@wandercar.example"},
                {14, "WanderCar — Hyderabad Hub", "22, Banjara Hills Road", 30, 9, "500034", "+91 40 4009 5566", "hyderabad.hub@wandercar.example"},
                {15, "WanderCar — Lucknow Hub", "17, Hazratganj", 32, 10, "226001", "+91 522 400 5566", "lucknow.hub@wandercar.example"},
                {16, "WanderCar — Ludhiana Hub", "40, Ferozepur Road", 35, 11, "141002", "+91 161 400 5566", "ludhiana.hub@wandercar.example"},
                {17, "WanderCar — Kochi Hub", "6, Marine Drive", 37, 12, "682031", "+91 484 400 5566", "kochi.hub@wandercar.example"},
        };
        for (Object[] r : rows) {
            hubIds.put((Integer) r[0], saveHub((String) r[1], (String) r[2], (Integer) r[3], (Integer) r[4],
                    (String) r[5], (String) r[6], (String) r[7]));
        }
    }

    private void seedAirportsAndKioskHubs() {
        // { airportId, code, name, cityId (local), stateId (local), kioskHubId (local) }
        // Weighted Maharashtra: 4 of the 13 airports. Kiosk hub indices start
        // at 20 to stay clear of the 17 city-hub indices (1-17) above.
        Object[][] rows = {
                {1, "BOM", "Chhatrapati Shivaji Maharaj International Airport", 1, 1, 20},
                {2, "PNQ", "Pune Airport", 2, 1, 21},
                {3, "NAG", "Dr. Babasaheb Ambedkar International Airport", 3, 1, 22},
                {4, "ISK", "Nashik Airport", 4, 1, 23},
                {5, "BLR", "Kempegowda International Airport", 9, 2, 24},
                {6, "DEL", "Indira Gandhi International Airport", 12, 3, 25},
                {7, "AMD", "Sardar Vallabhbhai Patel International Airport", 15, 4, 26},
                {8, "MAA", "Chennai International Airport", 18, 5, 27},
                {9, "IDR", "Devi Ahilya Bai Holkar Airport", 22, 6, 28},
                {10, "JAI", "Jaipur International Airport", 24, 7, 29},
                {11, "CCU", "Netaji Subhas Chandra Bose International Airport", 27, 8, 30},
                {12, "HYD", "Rajiv Gandhi International Airport", 30, 9, 31},
                {13, "LKO", "Chaudhary Charan Singh International Airport", 32, 10, 32},
        };
        for (Object[] r : rows) {
            int cityId = (Integer) r[3];
            int stateId = (Integer) r[4];
            int kioskHubId = (Integer) r[5];
            String code = (String) r[1];
            String name = (String) r[2];

            Long kioskHub = saveHub(
                    "WanderCar — " + code + " Airport Kiosk",
                    name + " . Rental Car Counter",
                    cityId, stateId, "000000", "1800-123-5566", "airport.kiosk@wandercar.example"
            );
            hubIds.put(kioskHubId, kioskHub);

            Airport airport = new Airport();
            airport.setAirportCode(code);
            airport.setAirportName(name);
            airport.setCityId(cityIds.get(cityId));
            airport.setStateId(stateIds.get(stateId));
            airport.setHubId(kioskHub);
            airportRepository.save(airport);
        }
    }

    private Long saveHub(String name, String address, int cityId, int stateId,
                         String pincode, String contactNo, String email) {
        Hub h = new Hub();
        h.setHubName(name);
        h.setAddress(address);
        h.setCityId(cityIds.get(cityId));
        h.setStateId(stateIds.get(stateId));
        h.setPincode(pincode);
        h.setContactNo(contactNo);
        h.setEmail(email);
        return hubRepository.save(h).getHubId();
    }

    private void seedCarTypes() {
        // { id, name, daily (INR), weekly, monthly, image }
        Object[][] rows = {
                {1, "Economy", 15.0, 100.0, 2000.0, "/cars/economy.svg"},
                {2, "Compact", 20.0, 120.0, 3000.0, "/cars/compact.svg"},
                {3, "Sedan", 30.0, 200.0, 5000.0, "/cars/sedan.svg"},
                {4, "SUV", 40.0, 250.0, 5000.0, "/cars/suv.svg"},
                {5, "Luxury", 100.0, 500.0, 25000, "/cars/luxury.svg"},
                {6, "Minivan", 50.0, 300.0, 8000.0, "/cars/minivan.svg"},
        };
        for (Object[] r : rows) {
            CarType ct = new CarType();
            ct.setCarTypeName((String) r[1]);
            ct.setDailyRate(((Number) r[2]).doubleValue());
            ct.setWeeklyRate(((Number) r[3]).doubleValue());
            ct.setMonthlyRate(((Number) r[4]).doubleValue());
            ct.setRateValidFrom(LocalDate.of(2026, 1, 1));
            ct.setRateValidTo(LocalDate.of(2026, 12, 31));
            ct.setImagePath((String) r[5]);
            carTypeIds.put((Integer) r[0], carTypeRepository.save(ct).getCarTypeId());
        }
    }

    private void seedCars() {
        // { carId, carTypeId, hubId, vehicleNumber, brand, model, year, color,
        //   fuelType, seats, mileage(kmpl), odometer, fuelLevel, isAvailable, status }
        // 14 of 18 cars sit at Maharashtra hubs (1-5); one each at Bengaluru
        // and Ahmedabad, and two at the new Madhya Pradesh hubs (10, 11).
        Object[][] rows = {
                {1, 1, 1, "MH01AB1234", "Maruti Suzuki", "Swift", 2024, "White", "PETROL", 4, 22.5, 18240, 82, true, "AVAILABLE"},
                {2, 2, 1, "MH01AC2345", "Hyundai", "i20", 2025, "Silver", "PETROL", 5, 18.0, 9120, 50, true, "AVAILABLE"},
                {3, 3, 1, "MH01AD3456", "Honda", "City", 2025, "Grey", "PETROL", 5, 17.8, 4210, 95, true, "AVAILABLE"},
                {4, 4, 1, "MH01AE4567", "Mahindra", "XUV700", 2024, "Black", "DIESEL", 7, 14.5, 22890, 40, false, "BOOKED"},
                {5, 5, 1, "MH01AF5678", "Mercedes-Benz", "E-Class", 2025, "Black", "PETROL", 5, 12.0, 3100, 100, true, "AVAILABLE"},
                {6, 4, 2, "MH12BG6789", "Tata", "Nexon EV", 2024, "Blue", "ELECTRIC", 5, 0.0, 6100, 88, true, "AVAILABLE"},
                {7, 1, 2, "MH12BH7890", "Maruti Suzuki", "Baleno", 2024, "Red", "PETROL", 5, 21.0, 27650, 55, true, "AVAILABLE"},
                {8, 6, 2, "MH12BI8901", "Toyota", "Innova Crysta", 2023, "White", "DIESEL", 7, 11.5, 41800, 60, true, "UNDER_MAINTENANCE"},
                {9, 4, 3, "MH31CJ9012", "Kia", "Seltos", 2024, "Grey", "PETROL", 5, 16.5, 12200, 70, true, "AVAILABLE"},
                {10, 3, 3, "MH31CK0123", "Hyundai", "Verna", 2025, "White", "PETROL", 5, 18.4, 2200, 90, true, "AVAILABLE"},
                {11, 5, 4, "MH15DL1234", "BMW", "5 Series", 2025, "Black", "PETROL", 5, 13.0, 8700, 75, true, "AVAILABLE"},
                {12, 1, 4, "MH15DM2345", "Maruti Suzuki", "Alto", 2023, "Silver", "PETROL", 4, 24.0, 38900, 45, true, "AVAILABLE"},
                {13, 4, 5, "MH04EN3456", "Toyota", "Fortuner", 2024, "White", "DIESEL", 7, 10.5, 17600, 60, true, "AVAILABLE"},
                {14, 2, 5, "MH04EO4567", "Hyundai", "Grand i10 Nios", 2024, "Blue", "PETROL", 5, 20.3, 5400, 85, true, "AVAILABLE"},
                {15, 3, 6, "KA01FP5678", "Honda", "Amaze", 2024, "Grey", "PETROL", 5, 19.2, 9800, 65, true, "AVAILABLE"},
                {16, 1, 8, "GJ01GQ6789", "Tata", "Tiago", 2023, "Red", "PETROL", 5, 23.8, 14300, 70, true, "AVAILABLE"},
                {17, 2, 10, "MP04HR7890", "Hyundai", "i20", 2024, "White", "PETROL", 5, 18.0, 15200, 60, true, "AVAILABLE"},
                {18, 4, 11, "MP09HS8901", "Mahindra", "XUV700", 2024, "Grey", "DIESEL", 7, 14.5, 9800, 55, true, "AVAILABLE"},
        };
        for (Object[] r : rows) {
            int typeIndex = (Integer) r[1];
            int hubIndex = (Integer) r[2];
            Car car = new Car();
            car.setCarTypeId(carTypeIds.get(typeIndex));
            car.setHubId(hubIds.get(hubIndex));
            car.setVehicleNumber((String) r[3]);
            car.setBrand((String) r[4]);
            car.setModel((String) r[5]);
            car.setManufactureYear((Integer) r[6]);
            car.setColor((String) r[7]);
            car.setFuelType(FuelType.valueOf((String) r[8]));
            car.setSeatingCapacity((Integer) r[9]);
            car.setMileage(((Number) r[10]).doubleValue());
            car.setOdometer((Integer) r[11]);
            car.setFuelLevel((Integer) r[12]);
            car.setAvailable((Boolean) r[13]);
            car.setStatus(CarStatus.valueOf((String) r[14]));
            carRepository.save(car);
            handPickedCounts.merge(hubIndex + "-" + typeIndex, 1, Integer::sum);
        }
    }

    // Every hub must show at least this many cars per category on the
    // vehicle-selection page — a real, browsable inventory per car type
    // rather than a token one or two. Applies uniformly to all 30 hubs
    // (all 17 city hubs and all 13 airport kiosks) and all 6 categories.
    private static final int MIN_CARS_PER_HUB_AND_TYPE = 10;

    // { carTypeId -> a handful of real brand/model/fuelType/seats/mileage
    // variants for that category }. topUpFleetToMinimum() cycles through
    // these so a hub's list of 10 Sedans, say, isn't 10 identical clones.
    private static final Map<Integer, Object[][]> TYPE_VARIANTS = new HashMap<>();
    static {
        TYPE_VARIANTS.put(1, new Object[][] { // Economy
                {"Maruti Suzuki", "Alto", "PETROL", 4, 24.0},
                {"Tata", "Tiago", "PETROL", 5, 23.0},
                {"Renault", "Kwid", "PETROL", 5, 22.0},
        });
        TYPE_VARIANTS.put(2, new Object[][] { // Compact
                {"Hyundai", "i20", "PETROL", 5, 20.0},
                {"Maruti Suzuki", "Baleno", "PETROL", 5, 21.0},
                {"Tata", "Altroz", "PETROL", 5, 19.0},
        });
        TYPE_VARIANTS.put(3, new Object[][] { // Sedan
                {"Honda", "City", "PETROL", 5, 18.0},
                {"Hyundai", "Verna", "PETROL", 5, 18.4},
                {"Skoda", "Slavia", "PETROL", 5, 19.0},
        });
        TYPE_VARIANTS.put(4, new Object[][] { // SUV
                {"Mahindra", "XUV700", "DIESEL", 7, 15.0},
                {"Kia", "Seltos", "PETROL", 5, 16.5},
                {"Tata", "Harrier", "DIESEL", 5, 14.6},
        });
        TYPE_VARIANTS.put(5, new Object[][] { // Luxury
                {"Mercedes-Benz", "E-Class", "PETROL", 5, 12.0},
                {"BMW", "5 Series", "PETROL", 5, 13.0},
                {"Audi", "A6", "PETROL", 5, 12.5},
        });
        TYPE_VARIANTS.put(6, new Object[][] { // Minivan
                {"Toyota", "Innova Crysta", "DIESEL", 7, 12.0},
                {"Maruti Suzuki", "Ertiga", "PETROL", 7, 17.0},
                {"Kia", "Carens", "DIESEL", 7, 16.0},
        });
    }

    private static final String[] FILLER_COLORS =
            {"White", "Silver", "Black", "Grey", "Red", "Blue", "Maroon", "Beige"};

    // Tops up every (hub, carType) combination to at least
    // MIN_CARS_PER_HUB_AND_TYPE cars total, hand-picked fleet included -
    // most importantly the 13 airport kiosk hubs, which otherwise have zero
    // cars at all and would show an empty vehicle-selection page the
    // moment a customer picks an airport for pickup instead of a city.
    // A combo with genuine hand-picked cars keeps them and only gets
    // however many more it's short of the minimum; a combo with none
    // starts from zero. All generated cars are seeded AVAILABLE, so the
    // live available-count per category only drops when a real
    // handover/return moves a car's status - see StaffServiceImpl.
    private void topUpFleetToMinimum() {
        int vehicleNumberSeq = 19; // continues on from the hand-picked fleet's 18 cars
        for (Integer hubIndex : hubIds.keySet()) {
            for (int typeIndex = 1; typeIndex <= 6; typeIndex++) {
                int existing = handPickedCounts.getOrDefault(hubIndex + "-" + typeIndex, 0);
                int toAdd = MIN_CARS_PER_HUB_AND_TYPE - existing;
                if (toAdd <= 0) continue;

                Object[][] variants = TYPE_VARIANTS.get(typeIndex);
                for (int i = 0; i < toAdd; i++) {
                    Object[] variant = variants[i % variants.length];
                    Car car = new Car();
                    car.setCarTypeId(carTypeIds.get(typeIndex));
                    car.setHubId(hubIds.get(hubIndex));
                    car.setVehicleNumber("WCR" + String.format("%04d", vehicleNumberSeq++));
                    car.setBrand((String) variant[0]);
                    car.setModel((String) variant[1]);
                    car.setManufactureYear(2023 + (i % 3));
                    car.setColor(FILLER_COLORS[i % FILLER_COLORS.length]);
                    car.setFuelType(FuelType.valueOf((String) variant[2]));
                    car.setSeatingCapacity((Integer) variant[3]);
                    car.setMileage((Double) variant[4]);
                    car.setOdometer(500 * i);
                    car.setFuelLevel(100);
                    car.setAvailable(true);
                    car.setStatus(CarStatus.AVAILABLE);
                    carRepository.save(car);
                }
            }
        }
    }

    private void seedAddons() {
        // { id, name, dailyRate (INR), description }
        // Additional Driver and Roadside Assistance Plus were removed from
        // the catalog per product decision - GPS Navigation and WiFi
        // Hotspot are one-per-booking toggles, Child Seat is the only
        // add-on with a quantity stepper (0-3, enforced on the add-ons page).
        Object[][] rows = {
                {1, "GPS Navigation", 5.0, "Turn-by-turn navigation unit, mounted and ready."},
                {2, "Child Seat", 5.0, "Rear-facing or booster, installed at pickup."},
                {5, "WiFi Hotspot", 10.0, "In-car 4G hotspot, unlimited data."},
        };
        for (Object[] r : rows) {
            Addon a = new Addon();
            a.setAddonName((String) r[1]);
            a.setDailyRate(((Number) r[2]).doubleValue());
            a.setRateValidFrom(LocalDate.of(2026, 1, 1));
            a.setRateValidTo(LocalDate.of(2026, 12, 31));
            a.setDescription((String) r[3]);
            addonIds.put((Integer) r[0], addonRepository.save(a).getAddonId());
        }
    }

    private void seedCustomerAndSampleBooking() {
        Customer customer = new Customer();
        customer.setFullName("Gryffindors Team");
        customer.setEmail("Gryffindors.team@email.com");
        customer.setPhone("9876540000");
        customer.setDateOfBirth(LocalDate.of(2026, 4, 01));
        customer.setDrivingLicenseNo("MH1220230012345");
        customer.setAddress1("12, gulmohar road juhu, mumbai");
        customer.setAddress2("");
        customer.setCityId(cityIds.get(1));   // mumbai
        customer.setStateId(stateIds.get(1)); // Maharashtra
        customer.setPincode("411005");
        customer.setStatus(CustomerStatus.ACTIVE);
        // Same example password as before ('password123'), now properly hashed.
        customer.setPasswordHash(passwordEncoder.encode("123456789"));
        customer = customerRepository.save(customer);

        BookingHeader header = new BookingHeader();
        header.setConfirmationNo("Team-0002");
        header.setBookingDate(LocalDateTime.of(2026, 7, 18, 9, 30));
        header.setCustomerId(customer.getCustomerId());
        header.setCarTypeId(carTypeIds.get(2)); // Compact — reserved, not yet assigned a specific car
        header.setCarId(null); // staff assigns one of the ≥10 Compact cars at Mumbai on hand-over
        header.setPickupHubId(hubIds.get(1)); // Mumbai
        header.setDropHubId(hubIds.get(1));
        header.setPickupDatetime(LocalDateTime.of(2026, 7, 18, 10, 0));
        header.setReturnDatetime(LocalDateTime.of(2026, 7, 21, 10, 0));
        header.setBookingStatus(BookingStatus.CONFIRMED);
        // 3 days x Compact daily rate (2200) = 6600, + 1 GPS Navigation x 3 days (250 x 1 x 3 = 750)
        header.setEstimatedAmount(7350.0);
        header.setRemarks("");
        header = bookingHeaderRepository.save(header);

        BookingDetail detail = new BookingDetail();
        detail.setBookingId(header.getBookingId());
        detail.setAddonId(addonIds.get(1)); // GPS Navigation
        detail.setAddonRate(250.0);
        detail.setQuantity(1);
        detail.setSubtotal(750.0);
        bookingDetailRepository.save(detail);
    }
}