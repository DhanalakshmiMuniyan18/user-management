package com.usermanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard API response wrapper")
public record StandardResponse<T>(
        @Schema(description = "Response payload")
        T data,
        @Schema(description = "Correlation ID for tracing")
        String correlationId,
        @Schema(description = "Error message, if any")
        String error
) {
    public static <T> StandardResponse<T> success(T data, String correlationId) {
        return new StandardResponse<>(data, correlationId, null);
    }
    public static <T> StandardResponse<T> error(String error, String correlationId) {
        return new StandardResponse<>(null, correlationId, error);
    }
} 