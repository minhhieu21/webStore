package edu.poly.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.poly.domain.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
		Optional<Customer>findByEmail(String email);
}
