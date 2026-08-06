package com.fleman.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fleman.dto.CustomerDTO;
import com.fleman.entity.Customer;
import com.fleman.repository.CityRepository;
import com.fleman.repository.CustomerRepository;
import com.fleman.repository.StateRepository;
import com.fleman.security.JwtUtil;

class CustomerServiceImplTest {

    @Mock
    CustomerRepository customerRepository;

    @Mock
    CityRepository cityRepository;

    @Mock
    StateRepository stateRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtUtil jwtUtil;

    @Mock
    EmailService emailService;

    CustomerServiceImpl customerService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        customerService = new CustomerServiceImpl(
                customerRepository,
                cityRepository,
                stateRepository,
                passwordEncoder,
                jwtUtil,
                emailService);
    }

    @Test
    void testGetCustomerById() {

        // Arrange
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        customer.setFullName("Pooja");
        customer.setEmail("pooja@gmail.com");

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        // Act
        CustomerDTO dto = customerService.getCustomerById(1L);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.getCustomerId());
        assertEquals("Pooja", dto.getFullName());
        assertEquals("pooja@gmail.com", dto.getEmail());

        verify(customerRepository).findById(1L);
    }
}