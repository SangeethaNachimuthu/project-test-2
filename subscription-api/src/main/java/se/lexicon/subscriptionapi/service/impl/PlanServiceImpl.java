package se.lexicon.subscriptionapi.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.lexicon.subscriptionapi.domain.constant.PlanStatus;
import se.lexicon.subscriptionapi.domain.constant.ServiceType;
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

        if(request == null) {
            throw new IllegalArgumentException("PlanRequest cannot be null");
        }

        if (planRepository.existsByNameIgnoreCase(request.name())) {
            throw new BusinessRuleException("Plan name already exists: " + request.name());
        }
        Operator operator = operatorRepository.findById(request.operatorId())
                .orElseThrow(() -> new ResourceNotFoundException("Operator not found with Id: " + request.operatorId()));

        Plan plan = mapper.toEntity(request);
        plan.setOperator(operator);
        plan.setStatus(request.status());

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
    @Transactional
    public PlanResponse updatePlan(Long id, PlanRequest request) {

        if (id == null || request == null) {
            throw new IllegalArgumentException("Id or PlanRequest cannot be null");
        }

        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + id));

        Operator operator = operatorRepository.findById(request.operatorId())
                .orElseThrow(() -> new ResourceNotFoundException("Operator not found with Id: " + request.operatorId()));

        /*plan.setName(request.name());
        plan.setPrice(request.price());
        plan.setServiceType(request.serviceType());
        plan.setStatus(request.status());
        plan.setDataLimit(request.dataLimit());
        plan.setOperator(operator);*/
        mapper.updateEntity(request, plan);
        plan.setOperator(operator);

        Plan updatedPlan = planRepository.save(plan);
        return mapper.toResponse(updatedPlan);
    }

    @Override
    @Transactional
    public void deletePlan(Long id) {

        if(id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + id));

        planRepository.delete(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanResponse> findByStatus() {

        return planRepository.findByStatus(PlanStatus.ACTIVE)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanResponse> viewActivePlansByServiceType(ServiceType serviceType) {

        if (serviceType == null) {
            throw new IllegalArgumentException("ServiceType cannot be null");
        }
        return planRepository.findByStatusAndServiceType(PlanStatus.ACTIVE, serviceType)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanResponse> viewActivePlansByOperatorId(Long operatorId) {

        if(operatorId == null) {
            throw new IllegalArgumentException("Operator id cannot be null");
        }

        operatorRepository.findById(operatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Operator not found with Id: " + operatorId));

        return planRepository.findByStatusAndOperatorId(PlanStatus.ACTIVE, operatorId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }


}
