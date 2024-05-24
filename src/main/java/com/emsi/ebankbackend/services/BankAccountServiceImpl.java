package com.emsi.ebankbackend.services;

import com.emsi.ebankbackend.dtos.*;
import com.emsi.ebankbackend.entities.*;
import com.emsi.ebankbackend.enums.OperationType;
import com.emsi.ebankbackend.exceptions.BalanceNotSufficientException;
import com.emsi.ebankbackend.exceptions.BankAccountNotFoundException;
import com.emsi.ebankbackend.exceptions.CustomerNotFoundException;
import com.emsi.ebankbackend.mappers.BankAccountMapperImpl;
import com.emsi.ebankbackend.repositories.AccountOperationRepository;
import com.emsi.ebankbackend.repositories.BankAccountRepository;
import com.emsi.ebankbackend.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService{

    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationRepository accountOperationRepository;
    private BankAccountMapperImpl dtoMapper;



    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving new Customer");
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        CurrentAccount bankAccount = new CurrentAccount();
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {throw new CustomerNotFoundException("Customer not found");}
        bankAccount.setId(UUID.randomUUID().toString());
        bankAccount.setCreatedAt(new Date());
        bankAccount.setBalance(initialBalance);
        bankAccount.setCustomer(customer);
        bankAccount.setOverDraft(overDraft);
        return dtoMapper.fromCurrentBankAccount(bankAccountRepository.save(bankAccount));
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
        SavingAccount bankAccount = new SavingAccount();
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {throw new CustomerNotFoundException("Customer not found");}
        bankAccount.setId(UUID.randomUUID().toString());
        bankAccount.setCreatedAt(new Date());
        bankAccount.setBalance(initialBalance);
        bankAccount.setCustomer(customer);
        bankAccount.setInterestRate(interestRate);
        return dtoMapper.fromSavingBankAccount(bankAccountRepository.save(bankAccount));
    }


    @Override
    public List<CustomerDTO> listCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream().map(cust->dtoMapper.fromCustomer(cust)).collect(Collectors.toList());
    }

    @Override
    public BankAccountDTO getBankAccount(String acocuntId) throws BankAccountNotFoundException {
        BankAccount bankAccount =  bankAccountRepository.findById(acocuntId).orElseThrow(()-> new BankAccountNotFoundException("Bank Account not found"));
        if (bankAccount instanceof SavingAccount) {
            SavingAccount savingAccount = (SavingAccount) bankAccount;
            return dtoMapper.fromSavingBankAccount(savingAccount);
        } else {
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return dtoMapper.fromCurrentBankAccount(currentAccount);
        }
    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount =  bankAccountRepository.findById(accountId).orElseThrow(()-> new BankAccountNotFoundException("Bank Account not found"));
        if (bankAccount.getBalance()<amount) throw new BalanceNotSufficientException("Balance not sufficient");
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()-amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount =  bankAccountRepository.findById(accountId).orElseThrow(()-> new BankAccountNotFoundException("Bank Account not found"));
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()+amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException {
        debit(accountIdSource,amount,"Transfer to " + accountIdDestination);
        credit(accountIdDestination, amount,"Transfer to " + accountIdSource);
    }
    
    @Override
    public List<BankAccountDTO> bankAccountList(){
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
         return bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount) {
                return dtoMapper.fromSavingBankAccount((SavingAccount) bankAccount);
            } else {
                return dtoMapper.fromCurrentBankAccount((CurrentAccount) bankAccount);
            }
        }).collect(Collectors.toList());
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("Customer not found."));
        return dtoMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("Saving updated Customer");
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }
    @Override
    public void deleteCustomer(Long customerId){
        log.info("Deleting Customer");
        customerRepository.deleteById(customerId);
    }

    @Override
    public List<AccountOperationDTO> accountHistory(String accountId){
        List<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountId);
        return accountOperations.stream().map(op->dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null);
        if (bankAccount == null) throw new BankAccountNotFoundException("Account not found");
        Page<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountId, PageRequest.of(page, size));
        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        List<AccountOperationDTO> accountOperationDTOS = accountOperations.getContent().stream().map(op -> dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
        accountHistoryDTO.setAccountOperationDTO(accountOperationDTOS);
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());
        return accountHistoryDTO;
    }
}
