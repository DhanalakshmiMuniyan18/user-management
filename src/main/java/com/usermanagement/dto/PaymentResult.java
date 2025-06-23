/**
 * DTO for payment processing results.
 * @author Saravanamuthukumar S
 */
package com.usermanagement.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResult {
    private String orderId;
    private double amount;
    private String transactionId;
    private String status;
    private String message;
} 