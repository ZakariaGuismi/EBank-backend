package com.emsi.ebankbackend.repositories;

import com.emsi.ebankbackend.entities.BankAccount;
import com.emsi.ebankbackend.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
}
