# test-driven-development-with-spring-boot

In this tutorial we develop an simple CRUD application in five steps.
- TDD - 0001 - Create Model
- TDD - 0002 - Object Mapping
- TDD - 0003 - Customer Repository
- TDD - 0004 - Customer Service
- TDD - 0004 - Customer Controller

**Development Environment**
 - IDE:         IntelliJ IDE
 - Java:        8
 - Spring Boot: 2.0.6.RELEASE 


____
**TDD - 0001 - Create Model**

***Branch: tdd-0001-create-model***

Create a new test class named CustomerTest in the package *de.xakte.springboottdd.model.CustomerTest*

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
```java
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
**TDD - 0002 - Object Mapping**

***Branch: tdd-0002-object-mapping***

Create a new test class named CustomerMappingTest in the package *de.xakte.springboottdd.model.CustomerMappingTest*
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

Create a new test class *de.xakte.springboottdd.repository.CustomerRepositoryTest*

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
package *de.xakte.springboottdd.repository.CustomerRepository*

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

Now we will test to show all persisted Customer. Add a new test method called *findAllCustomer()*.<br>
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
We add a new test method *findCustomerByFirstNameAndLastName()*.
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
*de.xakte.springboottdd.repository.CustomerRepository*.

```java
package de.xakte.springboottdd.repository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByFirstNameAndLastName(String firstName, String lastName);

}
```
Run the test again, the the test will passed successfuly.
____
**TDD - 0004 - Customer service**

***Branch: tdd-0003 -customer-service***

Create a new test class *de.xakte.springboottdd.service.CustomerServiceTest*.

```java
package de.xakte.springboottdd.service;

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
}
```
There is an error because the class ***de.xakte.springboottdd.service.CustomerService*** doesn't exist. Create the customer
service class.
```java
package de.xakte.springboottdd.service;

@Service
public class CustomerService {

    private CustomerRepository customerRepository; // <- will injected in the constructor below, no @Autowired are required. 

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }
}
```  

Add the test method *findAllCustomers()* 
as shown below.

```java
package de.xakte.springboottdd.service;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @MockBean // <- this will inject the mock repository into the service class
    private CustomerRepository customerRepository;

    @Test
    public void findAllCustomers() {

        given(customerRepository.findAll()).willReturn( // <- the method findAll() of the CustomerRepository will be mocked
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
}
```
Run the test and you will see its successful.

Now wie create a test method *findCustomerByFirstNameAndLastName()* 

```java
package de.xakte.springboottdd.service;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @MockBean
    private CustomerRepository customerRepository;

    // @Test public void findAllCustomers() {}

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
}
```
There is again an error, because the method *findByFirstNameAndLastName(...)* is missing. Create the methode in the
*de.xakte.springboottdd.service.CustomerService* class.
```java
package de.xakte.springboottdd.service;

@Service
public class CustomerService {

    private CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public List<Customer> findByFirstNameAndLastName(String firstName, String lastName) {
        return customerRepository.findByFirstNameAndLastName(firstName,lastName);
    }
}
```  
Run the test and you will see all is green.

Now we will test to create a new customer. Add the test methode *createNewCustomer()* to the test class.

```java
package de.xakte.springboottdd.service;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @MockBean
    private CustomerRepository customerRepository;

    // @Test public void findAllCustomers() {}

    // @Test public void findCustomerByFirstNameAndLastName() {}

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
}
```
We fix the error and add the missing method in the CustomerService class.
```java
package de.xakte.springboottdd.service;

@Service
public class CustomerService {

    private CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // public List<Customer> findAll() {}

    // public List<Customer> findByFirstNameAndLastName(String firstName, String lastName) {}

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
}
``` 
After run the test yo will see the the test is green. We can create a new customer, search by first name 
and last name, list all customers. We are missing a method to delete a customer. 

We will create a new test method *deleteCustomer()*.

```java
package de.xakte.springboottdd.service;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @MockBean
    private CustomerRepository customerRepository;

    //@Test public void findAllCustomers() {}

    //@Test public void findCustomerByFirstNameAndLastName() {}

    //@Test public void createNewCustomer() {}

    @Test
    public void deleteCustomer() {
        Customer customer = new Customer(2L, "Jane", "Doe");

        doNothing().when(customerRepository).delete(customer);

        customerService.deleteCustomer(customer);

        verify(customerRepository, times(1)).delete(customer);
    }
}
```
The error because the missing method will be fixed in the CustomerService class.

```java
package de.xakte.springboottdd.service;

@Service
public class CustomerService {

    private CustomerRepository customerRepository;

    //public CustomerService(CustomerRepository customerRepository) {}

    //public List<Customer> findAll() {}

    //public List<Customer> findByFirstNameAndLastName(String firstName, String lastName) {}

    //public Customer createCustomer(Customer customer) {}

    public void deleteCustomer(Customer customer) {
        customerRepository.delete(customer);
    }
}
```
We run the test and all is green.
____
**TDD - 0005 - Customer Controller**

***Branch: tdd-0004-customer-controller***

Finally we add the rest controller in tdd manner. We create a new test class.

```java
package de.xakte.springboottdd.controller;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {CustomerController.class})
public class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CustomerService customerService;

    @Test
    public void getFindAllCustomers() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/customers"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
```
The first error is the missing class CustomerController, we will add it immediately. As we see below, we inject the
customer service into the constructor. We add a service get method named getFindAll().
```java
package de.xakte.springboottdd.controller;


import de.xakte.springboottdd.model.Customer;
import de.xakte.springboottdd.service.CustomerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
```
We run the test successfully. Now we will check that the service get the valid result, to do that we will mock the findAll() 
methode of the customer service.

```java
package de.xakte.springboottdd.controller;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {CustomerController.class})
public class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CustomerService customerService;

    @Test
    public void getFindAllCustomers() throws Exception {

        when(customerService.findAll()).thenReturn(
                Arrays.asList(
                        new Customer(1L, "John","Doe"),
                        new Customer(2L, "Jane","Doe"),
                        new Customer(3L, "Peter","Pan"),
                        new Customer(4L, "Paul","Nobody")
                )
        );

        mockMvc.perform(MockMvcRequestBuilders.get("/customers"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(4))
                .andExpect(jsonPath("$.[0].firstName").value("John"))
                .andExpect(jsonPath("$.[0].lastName").value("Doe"))
                .andExpect(jsonPath("$.[3].firstName").value("Paul"))
                .andExpect(jsonPath("$.[3].lastName").value("Nobody"));
    }
}
```
As you can see we check the result with the result matcher *MockMvcResultMatchers.jsonPath*, that's a continuous way to
validate json objects. Now we create a test method to get customer by first and last name.

```java
package de.xakte.springboottdd.controller;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {CustomerController.class})
public class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CustomerService customerService;

    //@Test public void getFindAllCustomers() throws Exception {}

    @Test
    public void getUserByFirsnameAndLastname() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/customers/John/Doe"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].firstName").value("John"))
                .andExpect(jsonPath("$.[0].lastName").value("Doe"));
    }
}
```
We run the test and the test will be failed with http error 404. That means that no service endpoint exist in the rest
controller. We will add the missing method in the customer controlller.
```java
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

    //@GetMapping List<Customer> getFindAll() {}

    @GetMapping(path = {"/{firstName}/{lastName}"})
    List<Customer> getFindByFirstNameAndLastName(
            @PathVariable("firstName") String firstName,
            @PathVariable("lastName") String lastName
    ) {
        return customerService.findByFirstNameAndLastName(firstName, lastName);
    }
}
```   
We run the test again, and it will fails, because the customer service mock isn't implemented. We will add mock methode.
```java
package de.xakte.springboottdd.controller;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {CustomerController.class})
public class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CustomerService customerService;

    //@Test public void getFindAllCustomers() throws Exception {}

    @Test
    public void getUserByFirsnameAndLastname() throws Exception {
        String firstName = "John";
        String lastName = "Doe";

        when(customerService.findByFirstNameAndLastName(firstName,lastName)).thenReturn(
            Arrays.asList(
                    new Customer(1L, firstName, lastName)
            )
        );

        mockMvc.perform(MockMvcRequestBuilders.get("/customers/John/Doe"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].firstName").value(firstName))
                .andExpect(jsonPath("$.[0].lastName").value(lastName));
    }
}
```
We run the tests successfully. Now we add a test for for the rest endpoint createCustomer.
```java
package de.xakte.springboottdd.controller;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {CustomerController.class})
public class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CustomerService customerService;

    //@Test public void getFindAllCustomers() throws Exception {}

    //@Test public void getUserByFirsnameAndLastname() throws Exception {}

    @Test
    public void createUser() throws Exception {
        Long id = null;
        String firstName = "John";
        String lastName = "Doe";
        Customer customer = new Customer(id, firstName, lastName);

        when(customerService.createCustomer(customer)).thenReturn(
                new Customer(1L, firstName, lastName)
        );

        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(objectToJson(customer)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value(firstName))
                .andExpect(jsonPath("$.lastName").value(lastName));
    }

    private byte[] objectToJson(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to map json object");
        }
    }
}
```
Because the test failed, we must be implement the rest controller entpoint.
```Java
package de.xakte.springboottdd.controller;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    //@GetMapping List<Customer> getFindAll() {}

    //@GetMapping(path = {"/{firstName}/{lastName}"}) List<Customer> getFindByFirstNameAndLastName(...)}

    @PostMapping
    public Customer postCustomer(@RequestBody Customer customer){
        return customerService.createCustomer(customer);
    }
}
```
Runnig the test and all is green. The last rest service entpoint will be used to delete a customer. We write the test for
this scenario. 
```java
package de.xakte.springboottdd.controller;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {CustomerController.class})
public class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CustomerService customerService;

    //@Test public void getFindAllCustomers() throws Exception {}

    //@Test public void getUserByFirsnameAndLastname() throws Exception {}

    //@Test public void createUser() throws Exception {}

    @Test
    public void deleteCustomer() throws Exception {

        Long id = 1L;
        String firstName = "John";
        String lastName = "Doe";
        Customer customer = new Customer(id, firstName, lastName);

        doNothing().when(customerService).deleteCustomer(customer);

        mockMvc.perform(delete("/customers")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(objectToJson(customer)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(customerService,times(1)).deleteCustomer(customer);
    }

    private byte[] objectToJson(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to map json object");
        }
    }
}
```
This test also fails, because there is no rest entpoint to delete a customer. We will add this missing part.
```java
package de.xakte.springboottdd.controller;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    //@GetMappingList<Customer> getFindAll() {}

    //@GetMapping(path = {"/{firstName}/{lastName}"}) List<Customer> getFindByFirstNameAndLastName(...) {}

    //@PostMapping public Customer postCustomer(@RequestBody Customer customer){}

    @DeleteMapping
    public void deleteCustomer(@RequestBody Customer customer){
        customerService.deleteCustomer(customer);
    }
}
```
We run the test successfully.

Now we have a simple CRUD application test driven developed. 
