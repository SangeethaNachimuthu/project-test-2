package se.lexicon.subscriptionapi.service;

import se.lexicon.subscriptionapi.dto.request.PlanRequest;
import se.lexicon.subscriptionapi.dto.response.PlanResponse;

import java.util.List;

public interface PlanService {

    PlanResponse create(PlanRequest request);
    List<PlanResponse> viewAll();
    PlanResponse updatePlan(Long id, PlanRequest request);
    void deletePlan(Long id);
    List<PlanResponse> findByStatus();
}
