package se.lexicon.subscriptionapi.dto.response;

import se.lexicon.subscriptionapi.domain.constant.SubscriptionStatus;

import java.time.LocalDateTime;

public record SubscriptionResponse(

        Long id,
        Long customerId,
        PlanResponse plan,
        LocalDateTime subscribedDate,
        SubscriptionStatus status,
        LocalDateTime cancelledDate
) {
}
