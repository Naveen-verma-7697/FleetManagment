package com.fleetmanagement.service;

import java.util.List;

import com.fleetmanagement.entity.Customer;



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
