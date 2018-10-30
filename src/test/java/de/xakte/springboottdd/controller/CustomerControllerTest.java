package de.xakte.springboottdd.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.xakte.springboottdd.model.Customer;
import de.xakte.springboottdd.service.CustomerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                        new Customer(1L, "John", "Doe"),
                        new Customer(2L, "Jane", "Doe"),
                        new Customer(3L, "Peter", "Pan"),
                        new Customer(4L, "Paul", "Nobody")
                )
        );

        mockMvc.perform(get("/customers"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(4))
                .andExpect(jsonPath("$.[0].firstName").value("John"))
                .andExpect(jsonPath("$.[0].lastName").value("Doe"))
                .andExpect(jsonPath("$.[3].firstName").value("Paul"))
                .andExpect(jsonPath("$.[3].lastName").value("Nobody"));
    }

    @Test
    public void getUserByFirsnameAndLastname() throws Exception {
        String firstName = "John";
        String lastName = "Doe";

        when(customerService.findByFirstNameAndLastName(firstName, lastName)).thenReturn(
                Arrays.asList(
                        new Customer(1L, firstName, lastName)
                )
        );

        mockMvc.perform(get("/customers/John/Doe"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].firstName").value(firstName))
                .andExpect(jsonPath("$.[0].lastName").value(lastName));
    }

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
