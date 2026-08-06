package com.fleman.controller;

import com.fleman.dto.CustomerDTO;
import com.fleman.dto.GovtIdDocumentDTO;
import com.fleman.dto.GuestCustomerRequest;
import com.fleman.dto.UpdateCustomerRequest;
import com.fleman.security.AuthenticatedUser;
import com.fleman.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.getCustomerById(customerId));
    }

    // Separate from the main customer read - see GovtIdDocumentDTO for why.
    @GetMapping("/{customerId}/govt-id")
    public ResponseEntity<GovtIdDocumentDTO> getGovtIdDocument(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.getGovtIdDocument(customerId));
    }

    @PutMapping
    public ResponseEntity<CustomerDTO> updateCustomer(@RequestBody UpdateCustomerRequest request) {
        return ResponseEntity.ok(customerService.updateCustomer(request));
    }
    
    
    @GetMapping("/me")
    public ResponseEntity<CustomerDTO> me() {

        return ResponseEntity.ok(
                customerService.getCustomerById(
                        AuthenticatedUser.getCurrentUserId()
                )
        );
    }

    // Used by the booking flow for a guest who fills in their details
    // without logging in first — finds or creates a Customer row so the
    // booking always carries a real customerId.
    @PostMapping("/guest")
    public ResponseEntity<CustomerDTO> upsertGuestCustomer(@Valid @RequestBody GuestCustomerRequest request) {
        return ResponseEntity.ok(customerService.upsertGuestCustomer(request));
    }
}







