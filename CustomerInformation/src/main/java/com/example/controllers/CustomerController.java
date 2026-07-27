package com.example.controllers;
import jakarta.validation.Valid;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.entities.Customer;
import com.example.services.CustomerService;

@RestController
@RequestMapping("/customer")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@PostMapping("/save")
	public Customer saveCustomer(@Valid @RequestBody Customer customer) {
	    return customerService.saveCustomer(customer);
	}
	
	@GetMapping("/all")
	public List<Customer> getAllCustomers() 
	{
		return customerService.getAllCustomers();
	}

	@GetMapping("/{id}")
	public Customer getCustomerById(@PathVariable Integer id) 
	{
		return customerService.getCustomerById(id);
	}

	@PutMapping("/update")
	public Customer updateCustomer(@Valid @RequestBody Customer customer) {
	    return customerService.updateCustomer(customer);
	}

	@DeleteMapping("/delete/{id}")
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