package de.xakte.springboottdd.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CustomerMappingTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void createAndPersistCustomer() {
        Long id = null;
        String firstName = "John";
        String lastName = "Doe";

        Customer customer = new Customer(id, firstName, lastName);

        Customer persistedCustomer = entityManager.persistAndFlush(customer);

        assertThat(persistedCustomer.getId()).isGreaterThan(0L);
    }
}
