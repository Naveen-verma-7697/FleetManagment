package com.example.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entities.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Integer> 
{
	List<Customer> findByCity(String city);

	List<Customer> findByState(String state);

	Optional<Customer> findByEmail(String email);
}