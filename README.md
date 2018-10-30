# test-driven-development-with-spring-boot

**Development Environment**
 - IDE:         IntelliJ IDE
 - Java:        8
 - Spring Boot: 2.0.6.RELEASE 

____
**TDD - 0001 - Create Model**

***Branch: tdd-0001-create-model***

Create a new test class named CustomerTest in the package ***de.xakte.springboottdd.model.CustomerTest***

```java
package de.xakte.springboottdd.model;

import org.junit.Test;

public class CustomerTest {

    @Test
    public void createCustomer() {
        Long id = 1000L;
        String firstName = "John";
        String lastName = "Doe";
        
        Customer customer = new Customer(id, firstName, lastName);
    }
}
```

There is an error in the test code because no Customer object exist. Move the mouse pointer over 'new Customer(...)' and 
use the quick fix to generate the Customer class.

```java
package de.xakte.springboottdd.model;

public class Customer {
    
    public Customer(Long id, String firstName, String lastName) {
    }
}
```

Lombok will be used to reduce the boilerplate code. Lombok generate the Getter and Setter as well as the constructors.<br>
 
Change the Customer class as below.

```java
package de.xakte.springboottdd.model;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    private Long id;
    private String firstName;
    private String lastName;
}

```
Open and extend the CustomerTest class as below.
```
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
```
Run the test and you see the test will sucessfull passed. 
  
____
**TDD - 0001 - Object Mapping**

***Branch: tdd-0003-object-mapping***

Create a new test class named CustomerMappingTest in the package ***de.xakte.springboottdd.model.CustomerMappingTest***
```java
package de.xakte.springboottdd.model;

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
```

Run the test an you will see the test ist broken. Why is it broken?

```bash
java.lang.IllegalArgumentException: Unknown entity: de.xakte.springboottdd.model.Customer
```
This error means the Customer object is no entity that can persisted. Will will fix this in the Customer class.
```java
package de.xakte.springboottdd.model;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity // <-- mark class as entity object
public class Customer {
    @Id                                             // <- every jpa object need an id field
    @GeneratedValue(strategy = GenerationType.AUTO) // <- the id will generated automatical
    private Long id;
    private String firstName;
    private String lastName;
}
``` 
Run the test CustomerMappingTest again and the test will successful passed.

____
**TDD - 0003 - Customer repository**

***Branch: tdd-0003-customer-repository***

Create a new test class ***de.xakte.springboottdd.repository.CustomerRepositoryTest***

```java
package de.xakte.springboottdd.repository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;
    

}
``` 
There is an error because no interface CustomerRepository exist. Create an interface named CustomerRepository in the
package ***de.xakte.springboottdd.repository.CustomerRepository***

```java
package de.xakte.springboottdd.repository;

import de.xakte.springboottdd.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
```
Add the test method ***createAndPersistCustomer()***.

```java
package de.xakte.springboottdd.repository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;


    @Test
    public void createAndPersistCustomer() {
        Customer customer = new Customer(null, "John", "Doe");

        Customer persistedCustomer = customerRepository.save(customer);

        assertThat(persistedCustomer.getId()).isGreaterThan(0L);
    }
}
```

Run the test. The test will successful passed.

Now we will test to show all persisted Customer. Add a new test method called ***findAllCustomer()***.<br>
There will four customers created an persisted, than the generic method findAll() of the customerRepository will be
called, the result is a list of four Coustomer with an id greater than 0.


```java
package de.xakte.springboottdd.repository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;


    @Before
    public void setUp() throws Exception {
        customerRepository.deleteAll();
    }

    // @Test public void createAndPersistCustomer() {...}

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
}
```
We add a new test method ***findCustomerByFirstNameAndLastName()***.

```java
package de.xakte.springboottdd.repository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Before
    public void setUp() throws Exception {
        customerRepository.deleteAll();
    }

    // @Test public void createAndPersistCustomer() {}

    // @Test public void findAllCustomers() {...}

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
```
There is an error, the method ***findByFirstNameAndLastName(...)*** no exist. Add the Methode in the interface 
***de.xakte.springboottdd.repository.CustomerRepository***.

```java
package de.xakte.springboottdd.repository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByFirstNameAndLastName(String firstName, String lastName);

}
```
Run the test again, the the test will passed successfuly.
