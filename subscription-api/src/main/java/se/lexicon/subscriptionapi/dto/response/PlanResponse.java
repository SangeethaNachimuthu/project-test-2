package se.lexicon.subscriptionapi.dto.response;

import se.lexicon.subscriptionapi.domain.constant.PlanStatus;
import se.lexicon.subscriptionapi.domain.constant.ServiceType;
import se.lexicon.subscriptionapi.domain.entity.Operator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PlanResponse(

        Long id,
        String name,
        BigDecimal price,
        ServiceType serviceType,
        PlanStatus status,
        String dataLimit,
        LocalDateTime createdAt,
        Operator operator
) {
}
