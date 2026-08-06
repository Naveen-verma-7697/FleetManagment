package com.fleman.service;

import com.fleman.dto.*;

public interface CustomerService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
    StaffAuthResponse staffLogin(StaffLoginRequest request);
    CustomerDTO updateCustomer(UpdateCustomerRequest request);
    CustomerDTO getCustomerById(Long customerId);
    CustomerDTO upsertGuestCustomer(GuestCustomerRequest request);
    GovtIdDocumentDTO getGovtIdDocument(Long customerId);
}
