package de.xakte.springboottdd.model;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class CustomerTest {

    @Test
    public void createCustomer() {
        Long id = 1000L;
        String firstName = "John";
        String lastName = "Doe";

        Customer customer = new Customer(id, firstName, lastName);

        Assertions.assertThat(customer).isNotNull();
    }
}
