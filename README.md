# test-driven-development-with-spring-boot

**Development Environment**
 - IDE:         IntelliJ IDE
 - Java:        8
 - Spring Boot: 2.0.6.RELEASE 

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
  


 
