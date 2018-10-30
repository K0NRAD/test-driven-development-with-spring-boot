package de.xakte.springboottdd.repository;

import de.xakte.springboottdd.model.Customer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;


    @Before
    public void setUp() throws Exception {
        customerRepository.deleteAll();
    }

    @Test
    public void createAndPersistCustomer() {
        Customer customer = new Customer(null, "John", "Doe");

        Customer persistedCustomer = customerRepository.save(customer);

        assertThat(persistedCustomer.getId()).isGreaterThan(0L);
    }

    @Test
    public void findAllCustomers() {
        customerRepository.saveAll(
                Arrays.asList(
                        new Customer(null, "John", "Doe"),
                        new Customer(null, "Jane", "Doe"),
                        new Customer(null, "Peter", "Pan"),
                        new Customer(null, "Paul", "Nobody")
                )
        );

        List<Customer> customers = customerRepository.findAll();

        assertThat(customers.size()).isEqualTo(4);
        assertThat(customers.get(0).getId()).isGreaterThan(0L);
        assertThat(customers.get(1).getId()).isGreaterThan(0L);
        assertThat(customers.get(2).getId()).isGreaterThan(0L);
        assertThat(customers.get(3).getId()).isGreaterThan(0L);
    }

    @Test
    public void findCustomerByFirstNameAndLastName() {
        customerRepository.saveAll(
                Arrays.asList(
                        new Customer(null, "John", "Doe"),
                        new Customer(null, "Jane", "Doe"),
                        new Customer(null, "Peter", "Pan"),
                        new Customer(null, "Paul", "Nobody")
                )
        );

        String firstName = "Jane";
        String lastName = "Doe";

        List<Customer> customers = customerRepository.findByFirstNameAndLastName( firstName, lastName);

        assertThat(customers.size()).isEqualTo(1);
        assertThat(customers.get(0).getId()).isGreaterThan(0L);
        assertThat(customers.get(0).getFirstName()).isEqualTo(firstName);
        assertThat(customers.get(0).getLastName()).isEqualTo(lastName);

    }
}

