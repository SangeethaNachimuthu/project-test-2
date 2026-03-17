package se.lexicon.subscriptionapi.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.lexicon.subscriptionapi.domain.constant.PlanStatus;
import se.lexicon.subscriptionapi.domain.entity.Operator;
import se.lexicon.subscriptionapi.domain.entity.Plan;
import se.lexicon.subscriptionapi.dto.request.PlanRequest;
import se.lexicon.subscriptionapi.dto.response.PlanResponse;
import se.lexicon.subscriptionapi.exception.BusinessRuleException;
import se.lexicon.subscriptionapi.exception.ResourceNotFoundException;
import se.lexicon.subscriptionapi.mapper.PlanMapper;
import se.lexicon.subscriptionapi.repository.OperatorRepository;
import se.lexicon.subscriptionapi.repository.PlanRepository;
import se.lexicon.subscriptionapi.service.PlanService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;
    private final OperatorRepository operatorRepository;
    private final PlanMapper mapper;

    public PlanServiceImpl(PlanRepository planRepository, OperatorRepository operatorRepository,PlanMapper mapper) {
        this.planRepository = planRepository;
        this.operatorRepository = operatorRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public PlanResponse create(PlanRequest request) {

        if (planRepository.existsByNameIgnoreCase(request.name())) {
            throw new BusinessRuleException("Plan name already exists: " + request.name());
        }
        Operator operator = operatorRepository.findById(request.operatorId())
                .orElseThrow(() -> new ResourceNotFoundException("Operator not found with Id: " + request.operatorId()));

        Plan plan = mapper.toEntity(request);
        plan.setOperator(operator);
        plan.setStatus(PlanStatus.ACTIVE);

        Plan savedPlan = planRepository.save(plan);

        return mapper.toResponse(savedPlan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanResponse> viewAll() {

        return planRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PlanResponse updatePlan(Long id, PlanRequest request) {
        return null;
    }

    @Override
    public PlanResponse deletePlan(Long id) {
        return null;
    }
}
