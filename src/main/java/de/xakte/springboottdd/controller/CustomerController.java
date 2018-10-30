package de.xakte.springboottdd.controller;


import de.xakte.springboottdd.model.Customer;
import de.xakte.springboottdd.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    List<Customer> getFindAll() {
        return customerService.findAll();
    }

    @GetMapping(path = {"/{firstName}/{lastName}"})
    List<Customer> getFindByFirstNameAndLastName(
            @PathVariable("firstName") String firstName,
            @PathVariable("lastName") String lastName
    ) {
        return customerService.findByFirstNameAndLastName(firstName, lastName);
    }

    @PostMapping
    public Customer postCustomer(@RequestBody Customer customer){
        return customerService.createCustomer(customer);
    }

    @DeleteMapping
    public void deleteCustomer(@RequestBody Customer customer){
        customerService.deleteCustomer(customer);
    }
}
