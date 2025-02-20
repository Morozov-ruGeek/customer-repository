package ru.dexsys.customers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.dexsys.customers.entities.Customer;
import ru.dexsys.customers.entities.CustomerContact;
import ru.dexsys.customers.repositoriesAPI.CustomerContactRepository;
import ru.dexsys.customers.repositoriesAPI.CustomerRepository;
import ru.dexsys.customers.repositoriesIMPL.SpringCustomerContactRepository;
import ru.dexsys.customers.repositoriesIMPL.SpringCustomerRepository;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CustomerRepositoryTest {

    private CustomerRepository customerRepository;
    private CustomerContactRepository customerContactRepository;

    @BeforeEach
    public void init() {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
                .setName("users")
                .generateUniqueName(true)
                .setType(EmbeddedDatabaseType.H2)
                .setScriptEncoding("UTF-8")
                .addScript("schema.sql")
                .build();

        customerRepository = new SpringCustomerRepository(dataSource);
        customerContactRepository = new SpringCustomerContactRepository(dataSource);
    }

    @Test
    public void testCustomerSaved() {
        var customer = new Customer();
        customer.setId("customer_id");
        customer.setName("John Mathew");

        var home = new CustomerContact();
        home.setId("customer_contact_email");
        home.setContact("John.Mathew@xyz.com");
        home.setType(CustomerContact.Type.EMAIL);
        customer.addContact(home);

        var mobile = new CustomerContact();
        mobile.setId("customer_contact_mobile");
        mobile.setContact("+79123456789");
        mobile.setType(CustomerContact.Type.MOBILE);
        customer.addContact(mobile);

        customerRepository.save(customer);

        var savedCustomer = customerRepository.findById("customer_id");
        assertTrue(savedCustomer.isPresent());
        assertEquals("John Mathew", savedCustomer.get().getName());
        assertEquals(2, savedCustomer.get().getContacts().size());
        assertEquals("John.Mathew@xyz.com", savedCustomer.get().getContacts().get(0).getContact());
        assertEquals(CustomerContact.Type.EMAIL, savedCustomer.get().getContacts().get(0).getType());
        assertEquals("+79123456789", savedCustomer.get().getContacts().get(1).getContact());
        assertEquals(CustomerContact.Type.MOBILE, savedCustomer.get().getContacts().get(1).getType());
    }

    @Test
    public void testDeleteCustomerContact() {
        var customer = new Customer();
        customer.setId("customer_id");
        customer.setName("John Mathew");

        var mobile = new CustomerContact();
        mobile.setId("customer_contact_mobile");
        mobile.setContact("+79123456789");
        mobile.setType(CustomerContact.Type.MOBILE);
        customer.addContact(mobile);

        customerRepository.save(customer);

        customerContactRepository.deleteById("customer_contact_mobile");

        var savedCustomer = customerRepository.findById("customer_id");
        assertTrue(savedCustomer.isPresent());
        assertTrue(savedCustomer.get().getContacts().isEmpty());
    }
}
