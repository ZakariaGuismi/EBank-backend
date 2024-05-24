package com.emsi.ebankbackend.dtos;

import com.emsi.ebankbackend.enums.OperationType;
import lombok.Data;
import java.util.Date;

@Data
public class AccountOperationDTO {
    private Long id;
    private Date operationDate;
    private double amount;
    private OperationType type;
    private String description;
}
