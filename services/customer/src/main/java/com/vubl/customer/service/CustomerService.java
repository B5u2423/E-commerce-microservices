package com.vubl.customer.service;

import com.vubl.customer.exception.CustomerNotFoundException;
import com.vubl.customer.mapper.CustomerMapper;
import com.vubl.customer.entity.Customer;
import com.vubl.customer.entity.CustomerRequest;
import com.vubl.customer.entity.CustomerResponse;
import com.vubl.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    public String createCustomer (CustomerRequest request) {
        Customer customerFromRequest = mapper.toCustomer(request);
        var customer = repository.save(customerFromRequest);
        return customer.getId();
    }

    public void updateCustomer (CustomerRequest request) {
        var customer = repository.findById(request.id())
                .orElseThrow(() -> new CustomerNotFoundException(
                        String.format("Customer with ID::%s not found", request.id())
                ));
        mergerCustomer(customer, request);
        repository.save(customer);
    }

    // Write data ONLY blank fields to avoid overwrite of existing fields.
    private void mergerCustomer(Customer customer, CustomerRequest request) {
        if(StringUtils.isNotBlank(request.firstName())) {
            customer.setFirstName(request.firstName());
        }
        if(StringUtils.isNotBlank(request.lastName())) {
            customer.setFirstName(request.lastName());
        }
        if(StringUtils.isNotBlank(request.email())) {
            customer.setFirstName(request.email());
        }
        if(request.address() != null) {
            customer.setAddress(request.address());
        }
    }

    public List<CustomerResponse> findAllCustomers() {
        return repository.findAll()
                .stream()
                .map(mapper::fromCustomer)
                .collect(Collectors.toList());
    }

    public Boolean existsById(String customerId) {
        return repository.findById(customerId).isPresent();
    }

    public CustomerResponse findById(String customerId) {
        return repository.findById(customerId)
                .map(mapper::fromCustomer)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer with ID::%s not found", customerId)));
    }

    public void deleteCustomer(String customerId) {
        repository.deleteById(customerId);
    }
}
