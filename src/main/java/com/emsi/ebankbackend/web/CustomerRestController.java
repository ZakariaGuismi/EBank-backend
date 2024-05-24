package com.emsi.ebankbackend.web;

import com.emsi.ebankbackend.dtos.CustomerDTO;
import com.emsi.ebankbackend.exceptions.CustomerNotFoundException;
import com.emsi.ebankbackend.repositories.BankAccountRepository;
import com.emsi.ebankbackend.services.BankAccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
public class CustomerRestController {

    private final BankAccountRepository bankAccountRepository;
    private BankAccountService bankAccountService;
    @GetMapping("/customers")
    public List<CustomerDTO> customers(){
        return bankAccountService.listCustomers();
    }

    @GetMapping("/customers/{id}")
    public CustomerDTO getCustomer(@PathVariable(name = "id") Long customerId) throws CustomerNotFoundException {
        return  bankAccountService.getCustomer(customerId);
    }

    @PostMapping("/customers")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO request) {
        return bankAccountService.saveCustomer(request);
    }

    @PutMapping("/customers/{customerId}")
    public CustomerDTO updateCustomer(@PathVariable Long customerId, @RequestBody CustomerDTO customerDTO) throws CustomerNotFoundException {
        customerDTO.setId(customerId);
        return bankAccountService.updateCustomer(customerDTO);
    }

    @DeleteMapping("/customers/{id}")
    public void deleteCustomer(@PathVariable Long id) throws CustomerNotFoundException {
        bankAccountService.deleteCustomer(id);
    }
}
