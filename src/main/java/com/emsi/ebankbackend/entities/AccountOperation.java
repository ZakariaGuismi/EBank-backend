package com.emsi.ebankbackend.entities;

import com.emsi.ebankbackend.enums.OperationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Data @NoArgsConstructor
@AllArgsConstructor
public class AccountOperation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date operationDate;
    @Enumerated(EnumType.STRING)
    private OperationType type;
    private double amount;
    @ManyToOne
    private BankAccount bankAccount;
    private String description;
}
