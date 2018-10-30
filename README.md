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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

***Branch: tdd-0002-object-mapping***

Create a new test class named CustomerMappingTest in the package ***de.xakte.springboottdd.model.CustomerMappingTest***
```java
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
```

Run the test an you will see the test ist broken. Why is it broken?

```bash
java.lang.IllegalArgumentException: Unknown entity: de.xakte.springboottdd.model.Customer
```
This error means the Customer object is no entity that can persisted. Will will fix this in the Customer class.
```java
package de.xakte.springboottdd.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
 