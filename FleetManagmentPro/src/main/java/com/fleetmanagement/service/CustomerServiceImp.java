package com.fleetmanagement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fleetmanagement.entity.Customer;
import com.fleetmanagement.repository.CustomerRepository;



@Service
public class CustomerServiceImp implements CustomerService
{
	private final CustomerRepository customerRepository;

	CustomerServiceImp(CustomerRepository customerRepository) 
	{
		this.customerRepository = customerRepository;
	}
	
	@Override
	public Customer saveCustomer(Customer customer) 
	{
		return customerRepository.save(customer);
	}

	@Override
	public List<Customer> getAllCustomers() 
	{
		return customerRepository.findAll();
	}

	@Override
	public Customer getCustomerById(Integer id) 
	{
		return customerRepository.findById(id).orElse(null);
	}

	@Override
	public Customer updateCustomer(Customer customer) 
	{
		return customerRepository.save(customer);
	}

	@Override
	public void deleteCustomer(Integer id) 
	{
		customerRepository.deleteById(id);
		
	}

	@Override
	public List<Customer> getCustomerByCity(String city) 
	{
		return customerRepository.findByCity(city);
	}

	@Override
	public List<Customer> getCustomerByState(String state) 
	{
		return customerRepository.findByState(state);
	}

	@Override
	public Customer getCustomerByEmail(String email) 
	{
		
		return customerRepository.findByEmail(email).orElse(null);
	}

	@Override
	public Long totalCustomers() 
	{
		return customerRepository.count();
	}

	
}
