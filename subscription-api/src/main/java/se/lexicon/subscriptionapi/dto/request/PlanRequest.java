package se.lexicon.subscriptionapi.dto.request;

import jakarta.validation.constraints.*;
import se.lexicon.subscriptionapi.domain.constant.PlanStatus;
import se.lexicon.subscriptionapi.domain.constant.ServiceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PlanRequest(

        @NotBlank(message = "Plan name is required")
        @Size(max = 200, message = "Name must not exceed 200 characters")
        String name,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than zero")
        @Digits(integer = 17, fraction = 2, message = "Price must have max 17 digits before decimal and 2 after")
        BigDecimal price,

        @NotNull(message = "Service type is required")
        ServiceType serviceType,

        @NotNull(message = "Status is required")
        PlanStatus status,

        String dataLimit,

        @NotNull(message = "Created date is required")
        LocalDateTime createdAt,

        @NotNull(message = "Operator Id is required")
        Long operatorId
) {
}
