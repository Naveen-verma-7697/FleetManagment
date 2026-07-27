package com.example.services;
import com.example.exception.CustomerNotFoundException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.entities.Customer;
import com.example.repository.CustomerRepository;

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
		return customerRepository.findById(id)
				.orElseThrow(() ->
				new CustomerNotFoundException("Customer with Id " + id + " not found"));
	}

	@Override
	public Customer updateCustomer(Customer customer)
	{
		customerRepository.findById(customer.getCustomerId())
				.orElseThrow(() ->
				new CustomerNotFoundException("Customer with Id "
						+ customer.getCustomerId() + " not found"));

		return customerRepository.save(customer);
	}

	@Override
	public void deleteCustomer(Integer id)
	{
		Customer customer = customerRepository.findById(id)
				.orElseThrow(() ->
				new CustomerNotFoundException("Customer with Id " + id + " not found"));

		customerRepository.delete(customer);
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
		return customerRepository.findByEmail(email)
				.orElseThrow(() ->
				new CustomerNotFoundException("Customer with Email "
						+ email + " not found"));
	}

	@Override
	public Long totalCustomers() 
	{
		return customerRepository.count();
	}

	
}
