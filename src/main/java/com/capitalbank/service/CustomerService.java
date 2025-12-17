package com.capitalbank.service;

import com.capitalbank.model.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerService {

    // CRUD
    boolean register(Customer customer);
    Customer loadCustomerForAuthentication(String email);

    Customer getCustomerById(long customerId, Customer.Role requesterRole, long requesterId);
    List<Customer> getAllCustomers(Customer.Role requesterRole);
    Customer getCustomerByEmail(String email, Customer.Role requesterRole);
    boolean updateCustomer(Customer customer, Customer.Role requesterRole, long requesterId);
    boolean deleteCustomer(long customerId, Customer.Role requesterRole);

    // Authentication
    Customer login(String email, String password);
}
