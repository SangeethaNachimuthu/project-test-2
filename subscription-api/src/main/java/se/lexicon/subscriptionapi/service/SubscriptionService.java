package se.lexicon.subscriptionapi.service;

import se.lexicon.subscriptionapi.dto.request.SubscriptionRequest;
import se.lexicon.subscriptionapi.dto.response.SubscriptionResponse;

import java.util.List;

public interface SubscriptionService {

    SubscriptionResponse create(SubscriptionRequest request);
    List<SubscriptionResponse> viewSubscriptionByCustomer(String email);
    SubscriptionResponse updateSubscription(Long id, SubscriptionRequest request);
    void cancelSubscription(Long id);
}
