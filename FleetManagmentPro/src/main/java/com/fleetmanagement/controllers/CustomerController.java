package com.fleetmanagement.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.fleetmanagement.entity.Customer;
import com.fleetmanagement.service.CustomerService;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@PostMapping("/save")
	public Customer saveCustomer(@RequestBody Customer customer) 
	{
		return customerService.saveCustomer(customer);
	}

	@GetMapping("/getAll")
	public List<Customer> getAllCustomers() 
	{
		return customerService.getAllCustomers();
	}

	@GetMapping("/getById{id}")
	public Customer getCustomerById(@PathVariable Integer id) 
	{
		return customerService.getCustomerById(id);
	}

	@PutMapping("/update")
	public Customer updateCustomer(@RequestBody Customer customer) 
	{
		return customerService.updateCustomer(customer);
	}

	@DeleteMapping("/deleteById/{id}")
	public String deleteCustomer(@PathVariable Integer id) 
	{
		customerService.deleteCustomer(id);
		return "Customer Deleted Successfully";
	}

	@GetMapping("/city/{city}")
	public List<Customer> getCustomerByCity(@PathVariable String city) 
	{
		return customerService.getCustomerByCity(city);
	}

	@GetMapping("/state/{state}")
	public List<Customer> getCustomerByState(@PathVariable String state) 
	{
		return customerService.getCustomerByState(state);
	}

	@GetMapping("/email/{email}")
	public Customer getCustomerByEmail(@PathVariable String email) 
	{
		return customerService.getCustomerByEmail(email);
	}

	@GetMapping("/count")
	public Long totalCustomers() 
	{
		return customerService.totalCustomers();
	}

}