package se.lexicon.subscriptionapi.dto.request;

import jakarta.validation.constraints.NotNull;
import se.lexicon.subscriptionapi.domain.constant.SubscriptionStatus;

import java.time.LocalDateTime;

public record SubscriptionRequest(

        @NotNull(message = "Customer Id is required")
        Long customerId,

        @NotNull(message = "Plan Id is required")
        Long planId,

        @NotNull(message = "Subscribed Date is required")
        LocalDateTime subscribedDate,

        @NotNull(message = "Subscription Status is required")
        SubscriptionStatus status,

        LocalDateTime cancelledDate
) {
}
