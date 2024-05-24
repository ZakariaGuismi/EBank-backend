package com.emsi.ebankbackend.repositories;

import com.emsi.ebankbackend.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
