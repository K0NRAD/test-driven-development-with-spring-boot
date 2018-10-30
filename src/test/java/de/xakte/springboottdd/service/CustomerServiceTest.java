package de.xakte.springboottdd.service;

import de.xakte.springboottdd.model.Customer;
import de.xakte.springboottdd.repository.CustomerRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @MockBean
    private CustomerRepository customerRepository;

    @Test
    public void findAllCustomers() {

        given(customerRepository.findAll()).willReturn(
                Arrays.asList(
                        new Customer(1L, "John", "Doe"),
                        new Customer(2L, "Jane", "Doe"),
                        new Customer(3L, "Peter", "Pan"),
                        new Customer(4L, "Paul", "Nobody")
                )
        );

        List<Customer> customers = customerService.findAll();

        assertThat(customers.size()).isEqualTo(4);
    }

    @Test
    public void findCustomerByFirstNameAndLastName() {
        String firstName = "Jane";
        String lastName = "Doe";

        given(customerRepository.findByFirstNameAndLastName(firstName, lastName)).willReturn(
                Arrays.asList(
                        new Customer(2L, "Jane", "Doe")
                )
        );

        List<Customer> customers = customerService.findByFirstNameAndLastName(firstName, lastName);

        assertThat(customers.size()).isEqualTo(1);
        assertThat(customers.get(0).getFirstName()).isEqualTo(firstName);
        assertThat(customers.get(0).getLastName()).isEqualTo(lastName);

    }

    @Test
    public void createNewCustomer() {
        Long id = 1L;
        String firstName = "Jane";
        String lastName = "Doe";
        Customer customer = new Customer(null, firstName, lastName);

        given(customerRepository.save(customer)).willReturn(
                new Customer(id, firstName, lastName)
        );


        Customer newCustomer = customerService.createCustomer(customer);

        assertThat(newCustomer.getId()).isEqualTo(id);
        assertThat(newCustomer.getFirstName()).isEqualTo(firstName);
        assertThat(newCustomer.getLastName()).isEqualTo(lastName);
    }

    @Test
    public void deleteCustomer() {
        Customer customer = new Customer(2L, "Jane", "Doe");

        doNothing().when(customerRepository).delete(customer);

        customerService.deleteCustomer(customer);

        verify(customerRepository, times(1)).delete(customer);
    }
}
