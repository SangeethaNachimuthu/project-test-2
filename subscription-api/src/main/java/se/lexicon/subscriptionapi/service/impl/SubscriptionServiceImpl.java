package se.lexicon.subscriptionapi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.lexicon.subscriptionapi.domain.constant.PlanStatus;
import se.lexicon.subscriptionapi.domain.constant.SubscriptionStatus;
import se.lexicon.subscriptionapi.domain.entity.Customer;
import se.lexicon.subscriptionapi.domain.entity.Plan;
import se.lexicon.subscriptionapi.domain.entity.Subscription;
import se.lexicon.subscriptionapi.dto.request.SubscriptionRequest;
import se.lexicon.subscriptionapi.dto.response.SubscriptionResponse;
import se.lexicon.subscriptionapi.exception.InvalidPlanException;
import se.lexicon.subscriptionapi.exception.ResourceNotFoundException;
import se.lexicon.subscriptionapi.exception.SubscriptionAlreadyExistsException;
import se.lexicon.subscriptionapi.mapper.SubscriptionMapper;
import se.lexicon.subscriptionapi.repository.CustomerRepository;
import se.lexicon.subscriptionapi.repository.PlanRepository;
import se.lexicon.subscriptionapi.repository.SubscriptionRepository;
import se.lexicon.subscriptionapi.service.SubscriptionService;

import java.util.List;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final CustomerRepository customerRepository;
    private final PlanRepository planRepository;
    private final SubscriptionMapper mapper;

    @Autowired
    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository,
                                   CustomerRepository customerRepository,
                                   PlanRepository planRepository,
                                   SubscriptionMapper mapper) {
        this.subscriptionRepository = subscriptionRepository;
        this.customerRepository = customerRepository;
        this.planRepository = planRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public SubscriptionResponse create(SubscriptionRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Subscription Request cannot be null");
        }

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(()-> new ResourceNotFoundException("Customer not found with ID: " + request.customerId()));

        Plan plan = planRepository.findById(request.planId())
                .orElseThrow(()-> new ResourceNotFoundException("Plan not found with ID: " + request.planId()));

        // Plan must be ACTIVE
        if (plan.getStatus() == PlanStatus.INACTIVE) {
            throw new InvalidPlanException("Cannot subscribe to an inactive plan");
        }

        //Check for active subscription
        boolean exists = subscriptionRepository.existsByCustomerIdAndPlanServiceTypeAndStatus(
                customer.getId(), plan.getServiceType(), SubscriptionStatus.ACTIVE);
        if (exists) {
            throw new SubscriptionAlreadyExistsException(
                    "Customer already has an active subscription for " + plan.getServiceType());
        }

        Subscription subscription = mapper.toEntity(request);
        subscription.setCustomer(customer);
        subscription.setPlan(plan);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        Subscription savesSubscription = subscriptionRepository.save(subscription);

        return mapper.toResponse(savesSubscription);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionResponse> viewSubscriptionByCustomer(String email) {

        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        return subscriptionRepository.findByCustomerId(customer.getId())
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public SubscriptionResponse updateSubscription(Long id, SubscriptionRequest request) {

        if(id == null || request == null) {
            throw new IllegalArgumentException("ID or SubscriptionRequest cannot be null");
        }

        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + id));

        Plan plan = planRepository.findById(request.planId())
                .orElseThrow(()-> new ResourceNotFoundException("Plan not found with ID: " + request.planId()));

        // Validate plan change rules
        if (subscription.getPlan().getServiceType() != plan.getServiceType() ||
                !subscription.getPlan().getOperator().getId().equals(plan.getOperator().getId())) {
            throw new IllegalArgumentException("Plan change must be within the same service type and operator");
        }

        if (plan.getStatus() == PlanStatus.INACTIVE) {
            throw new IllegalArgumentException("Cannot change to an inactive plan");
        }

        mapper.updateEntity(request, subscription);
        subscription.setPlan(plan);
        Subscription updatedSubscription = subscriptionRepository.save(subscription);

        return mapper.toResponse(updatedSubscription);
    }

    @Override
    @Transactional
    public void cancelSubscription(Long id) {

        if(id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with Id: " + id));

        subscription.cancel();

        subscriptionRepository.save(subscription);
    }
}
