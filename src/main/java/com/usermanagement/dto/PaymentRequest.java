/**
 * DTO for payment requests.
 * @author Saravanamuthukumar S
 */
package com.usermanagement.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    @NotBlank
    private String orderId;
    @NotBlank
    private String customerId;
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;
    @NotBlank
    private String currency;
    @NotBlank
    private String paymentMethod;
    @NotBlank
    private String region;
    @NotBlank
    private String merchantId;
    private Map<String, Object> paymentDetails;
} 