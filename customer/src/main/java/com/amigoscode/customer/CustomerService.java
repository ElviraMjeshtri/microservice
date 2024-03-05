package com.amigoscode.customer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
@Slf4j
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final RestTemplate restTemplate;
    public void registerCustomer(CustomerRegistrationRequest request){
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();
        //TODO: check if email valid
        //TODO: check if email not taken
        customerRepository.saveAndFlush(customer);
        //TODO: check if fraudster ( this access the Fraud api and gets the is fraudster response)
        FraudCheckResponse fraudCheckResponse = restTemplate.getForObject(
                "http://localhost:8082/api/v1/fraud-check/{customerId}",
                FraudCheckResponse.class,
                customer.getId()
        );
        log.info("Fraud check request for customer {} ", customer.getId());
        assert fraudCheckResponse != null;
        if (fraudCheckResponse.isFraudster()){
            throw new IllegalStateException("fraudster");
        }
        //TODO: send notification
    }
}
