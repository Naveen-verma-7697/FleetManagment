package com.example.services;

import java.util.List;

import com.example.entities.Customer;

public interface CustomerService {

	Customer saveCustomer(Customer customer);

	List<Customer> getAllCustomers();

	Customer getCustomerById(Integer id);

	Customer updateCustomer(Customer customer);

	void deleteCustomer(Integer id);

	List<Customer> getCustomerByCity(String city);

	List<Customer> getCustomerByState(String state);

	Customer getCustomerByEmail(String email);

	Long totalCustomers();
}
