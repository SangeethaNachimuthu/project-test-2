package se.lexicon.subscriptionapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.boot.data.metrics.DefaultRepositoryTagsProvider;
import se.lexicon.subscriptionapi.domain.entity.Operator;
import se.lexicon.subscriptionapi.domain.entity.Plan;
import se.lexicon.subscriptionapi.domain.entity.Subscription;
import se.lexicon.subscriptionapi.dto.request.SubscriptionRequest;
import se.lexicon.subscriptionapi.dto.response.OperatorResponse;
import se.lexicon.subscriptionapi.dto.response.PlanResponse;
import se.lexicon.subscriptionapi.dto.response.SubscriptionResponse;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subscribedDate", ignore = true)
    @Mapping(target = "cancelledDate", ignore = true)
    Subscription toEntity(SubscriptionRequest request);

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "plan", target = "plan")
    SubscriptionResponse toResponse(Subscription subscription);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "subscribedDate", ignore = true)
    void updateEntity(SubscriptionRequest request, @MappingTarget Subscription subscription);
}
