package com.fleman.service.impl;

import com.fleman.dto.*;
import com.fleman.entity.Customer;
import com.fleman.entity.Customer.CustomerStatus;
import com.fleman.exception.ApiException;
import com.fleman.exception.ResourceNotFoundException;
import com.fleman.repository.CityRepository;
import com.fleman.repository.CustomerRepository;
import com.fleman.repository.StateRepository;
import com.fleman.security.JwtUtil;
import com.fleman.service.CustomerService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerServiceImpl implements CustomerService {

    // Every hub in the seed data is Hub #1 (Mumbai) for a mock staff
    // session — there is no real staff/hub-assignment table yet, matching
    // the frontend mock's staffLogin(), which returns a fixed hub for any
    // credentials.
    private static final String DEFAULT_STAFF_HUB_NAME = "WanderCar — Mumbai Hub";
    private static final Long DEFAULT_STAFF_HUB_ID = 1L;

    // The one fixed staff account. No Staff table exists (see the class
    // header) and there's no registration flow for staff, so this is a
    // deliberate hardcoded pair rather than a DB row — direct comparison
    // is enough here; the BCrypt machinery elsewhere in this class exists
    // for real customer accounts, not this single demo credential.
    private static final Long STAFF_ID=1L;
    private static final String STAFF_EMAIL = "Team2@gmail.com";
    private static final String STAFF_PASSWORD = "123456789";
    

    private final CustomerRepository customerRepository;
    private final CityRepository cityRepository;
    private final StateRepository stateRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public CustomerServiceImpl(CustomerRepository customerRepository, CityRepository cityRepository,
                                StateRepository stateRepository, PasswordEncoder passwordEncoder,
                                JwtUtil jwtUtil, EmailService emailService) {
        this.customerRepository = customerRepository;
        this.cityRepository = cityRepository;
        this.stateRepository = stateRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Customer customer = customerRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new ApiException("Invalid email or password", 401));
        if (customer.getPasswordHash() == null
                || !passwordEncoder.matches(request.getPassword(), customer.getPasswordHash())) {
            throw new ApiException("Invalid email or password", 401);
        }
        String token = jwtUtil.generateToken(
                customer.getCustomerId(),
                customer.getEmail(),
                "CUSTOMER"
        );
        return new AuthResponse(token, toDto(customer));
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
//
//        if (customerRepository.existsByEmailIgnoreCase(request.getEmail())) {
//            throw new ApiException("An account with this email already exists", 409);
//        }
    	
    	
    	Customer existing =
    		    customerRepository.findByEmailIgnoreCase(request.getEmail())
    		                      .orElse(null);

    		if(existing != null) {

    		    if(existing.getPasswordHash() != null) {
    		        throw new ApiException(
    		            "An account with this email already exists",
    		            409
    		        );
    		    }

    		    // Guest account
    		    existing.setFullName(request.getFullName());
    		    existing.setPasswordHash(
    		        passwordEncoder.encode(request.getPassword())
    		    );
    		    existing.setProvider(Customer.AuthProvider.LOCAL);
    		    existing.setEmailVerified(false);
    		    existing.setStatus(CustomerStatus.ACTIVE);

    		    existing = customerRepository.save(existing);

    		    String token = jwtUtil.generateToken(
    		            existing.getCustomerId(),
    		            existing.getEmail(),
    		            "CUSTOMER"
    		    );

    		    return new AuthResponse(token, toDto(existing));
    		}

        Customer customer = new Customer();

        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        customer.setPhone(null);
        customer.setDrivingLicenseNo(null);
        customer.setAddress1(null);
        customer.setAddress2(null);
        customer.setPincode(null);
        customer.setStatus(CustomerStatus.ACTIVE);

        customer.setProvider(Customer.AuthProvider.LOCAL);
        customer.setEmailVerified(false);
        customer = customerRepository.save(customer);
        

        // Send Welcome Email
        try {

            String body = """
                    Dear %s,

                    Welcome to WonderCar!

                    Your account has been created successfully.

                    You can now log in and book rental vehicles from our platform.

                    Thank you for choosing WonderCar.

                    Regards,
                    Gryffindors Team
                    """.formatted(customer.getFullName());

            emailService.sendEmail(
                    customer.getEmail(),
                    "Welcome to WonderCar",
                    body
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

        String token = jwtUtil.generateToken(
                customer.getCustomerId(),
                customer.getEmail(),
                "CUSTOMER"
        );

        return new AuthResponse(token, toDto(customer));
    }

    @Override
    public StaffAuthResponse staffLogin(StaffLoginRequest request) {
        String username = request.getUsername() == null ? "" : request.getUsername().trim();
        String password = request.getPassword() == null ? "" : request.getPassword();
        if (!STAFF_EMAIL.equalsIgnoreCase(username) || !STAFF_PASSWORD.equals(password)) {
            throw new ApiException("Invalid staff email or password", 401);
        }
        String token = jwtUtil.generateToken(
                STAFF_ID,
                STAFF_EMAIL,
                "STAFF"
        );
        StaffDTO staff = new StaffDTO(username, DEFAULT_STAFF_HUB_NAME, DEFAULT_STAFF_HUB_ID);
        return new StaffAuthResponse(token, staff);
    }

    @Override
    @Transactional
    public CustomerDTO updateCustomer(UpdateCustomerRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + request.getCustomerId()));

        if (request.getFullName() != null) customer.setFullName(request.getFullName());
        if (request.getPhone() != null) customer.setPhone(request.getPhone());
        if (request.getDateOfBirth() != null) customer.setDateOfBirth(request.getDateOfBirth());
        if (request.getDrivingLicenseNo() != null) customer.setDrivingLicenseNo(request.getDrivingLicenseNo());
        if (request.getPassportNo() != null) customer.setPassportNo(request.getPassportNo());
        if (request.getAddress1() != null) customer.setAddress1(request.getAddress1());
        if (request.getAddress2() != null) customer.setAddress2(request.getAddress2());
        if (request.getPincode() != null) customer.setPincode(request.getPincode());
        if (request.getCityId() != null) {
            if (!cityRepository.existsById(request.getCityId())) {
                throw new ResourceNotFoundException("City not found: " + request.getCityId());
            }
            customer.setCityId(request.getCityId());
        }
        if (request.getStateId() != null) {
            if (!stateRepository.existsById(request.getStateId())) {
                throw new ResourceNotFoundException("State not found: " + request.getStateId());
            }
            customer.setStateId(request.getStateId());
        }
        if (request.getGovtIdDocument() != null) {
            customer.setGovtIdDocument(request.getGovtIdDocument());
            customer.setGovtIdDocumentName(request.getGovtIdDocumentName());
            customer.setGovtIdDocumentType(request.getGovtIdDocumentType());
        }

        return toDto(customerRepository.save(customer));
    }

    @Override
    public CustomerDTO getCustomerById(Long customerId) {
        return customerRepository.findById(customerId).map(this::toDto).orElse(null);
    }

    @Override
    @Transactional
    public CustomerDTO upsertGuestCustomer(GuestCustomerRequest request) {
        Customer existing = customerRepository.findByEmailIgnoreCase(request.getEmail()).orElse(null);

        if (existing != null) {
            String fullName = (request.getFirstName() + " " + safe(request.getLastName())).trim();
            existing.setFullName(fullName);
            if (request.getPhone() != null && !request.getPhone().isBlank()) existing.setPhone(request.getPhone());
            if (request.getAddress1() != null && !request.getAddress1().isBlank()) existing.setAddress1(request.getAddress1());
            if (request.getDrivingLicenseNo() != null && !request.getDrivingLicenseNo().isBlank())
                existing.setDrivingLicenseNo(request.getDrivingLicenseNo());
            if (request.getGovtIdDocument() != null && !request.getGovtIdDocument().isBlank()) {
                existing.setGovtIdDocument(request.getGovtIdDocument());
                existing.setGovtIdDocumentName(request.getGovtIdDocumentName());
                existing.setGovtIdDocumentType(request.getGovtIdDocumentType());
            }
            return toDto(customerRepository.save(existing));
        }

        Customer customer = new Customer();
        customer.setFullName((request.getFirstName() + " " + safe(request.getLastName())).trim());
        customer.setEmail(request.getEmail());
        customer.setPhone(safe(request.getPhone()));
        customer.setDrivingLicenseNo(safe(request.getDrivingLicenseNo()));
        customer.setAddress1(safe(request.getAddress1()));
        customer.setAddress2("");
        customer.setPincode(safe(request.getZip()));
        customer.setStatus(CustomerStatus.ACTIVE);
        
        customer.setProvider(Customer.AuthProvider.LOCAL);
        customer.setEmailVerified(false);
        customer.setGovtIdDocument(request.getGovtIdDocument());
        customer.setGovtIdDocumentName(request.getGovtIdDocumentName());
        customer.setGovtIdDocumentType(request.getGovtIdDocumentType());
        // No passwordHash — a guest checkout never sets one; login() correctly
        // rejects this account until/unless the customer later registers.
        return toDto(customerRepository.save(customer));
    }

    @Override
    public GovtIdDocumentDTO getGovtIdDocument(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));
        return new GovtIdDocumentDTO(
                customer.getGovtIdDocument(), customer.getGovtIdDocumentName(), customer.getGovtIdDocumentType());
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private CustomerDTO toDto(Customer c) {
        CustomerDTO dto = new CustomerDTO();
        dto.setCustomerId(c.getCustomerId());
        dto.setFullName(c.getFullName());
        dto.setEmail(c.getEmail());
        dto.setPhone(c.getPhone());
        dto.setDateOfBirth(c.getDateOfBirth());
        dto.setDrivingLicenseNo(c.getDrivingLicenseNo());
        dto.setPassportNo(c.getPassportNo());
        dto.setAddress1(c.getAddress1());
        dto.setAddress2(c.getAddress2());
        dto.setCityId(c.getCityId());
        dto.setStateId(c.getStateId());
        dto.setPincode(c.getPincode());
        dto.setStatus(c.getStatus() != null ? c.getStatus().name() : null);
        dto.setGovtIdDocumentName(c.getGovtIdDocumentName());
        dto.setGovtIdDocumentType(c.getGovtIdDocumentType());
        return dto;
    }
}
