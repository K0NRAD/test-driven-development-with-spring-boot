package de.xakte.springboottdd.repository;

import de.xakte.springboottdd.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByFirstNameAndLastName(String firstName, String lastName);
}
