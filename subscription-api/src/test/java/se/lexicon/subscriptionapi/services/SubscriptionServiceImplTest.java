package se.lexicon.subscriptionapi.services;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.lexicon.subscriptionapi.domain.constant.PlanStatus;
import se.lexicon.subscriptionapi.domain.constant.ServiceType;
import se.lexicon.subscriptionapi.domain.constant.SubscriptionStatus;
import se.lexicon.subscriptionapi.domain.entity.Customer;
import se.lexicon.subscriptionapi.domain.entity.Operator;
import se.lexicon.subscriptionapi.domain.entity.Plan;
import se.lexicon.subscriptionapi.domain.entity.Subscription;
import se.lexicon.subscriptionapi.dto.request.CustomerRequest;
import se.lexicon.subscriptionapi.dto.request.OperatorRequest;
import se.lexicon.subscriptionapi.dto.request.PlanRequest;
import se.lexicon.subscriptionapi.dto.request.SubscriptionRequest;
import se.lexicon.subscriptionapi.dto.response.CustomerResponse;
import se.lexicon.subscriptionapi.dto.response.OperatorResponse;
import se.lexicon.subscriptionapi.dto.response.PlanResponse;
import se.lexicon.subscriptionapi.dto.response.SubscriptionResponse;
import se.lexicon.subscriptionapi.exception.BusinessRuleException;
import se.lexicon.subscriptionapi.exception.ResourceNotFoundException;
import se.lexicon.subscriptionapi.exception.SubscriptionAlreadyExistsException;
import se.lexicon.subscriptionapi.mapper.SubscriptionMapper;
import se.lexicon.subscriptionapi.repository.CustomerRepository;
import se.lexicon.subscriptionapi.repository.PlanRepository;
import se.lexicon.subscriptionapi.repository.SubscriptionRepository;
import se.lexicon.subscriptionapi.service.SubscriptionService;
import se.lexicon.subscriptionapi.service.impl.SubscriptionServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private PlanRepository planRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private SubscriptionMapper mapper;

    @InjectMocks
    private SubscriptionServiceImpl service;

    private Customer customer;
    private CustomerRequest customerRequest;
    private CustomerResponse customerResponse;
    private Plan plan;
    private PlanRequest planRequest;
    private PlanResponse planResponse;
    private Operator operator;
    private OperatorRequest operatorRequest;
    private OperatorResponse operatorResponse;
    private Subscription subscription;
    private SubscriptionRequest subscriptionRequest;
    private SubscriptionResponse subscriptionResponse;

    @BeforeEach
    void setUp() {

        customerRequest = new CustomerRequest("rose@example.com", "Rose",
                "Mari", "password");

        customer = new Customer();
        customer.setFirstName("Rose");
        customer.setLastName("Mari");
        customer.setEmail("rose@example.com");
        customer.setPassword("password");
        customer.setId(1L);

        customerResponse = new CustomerResponse(1L, "rose@example.com","Rose", "Mari");

        operatorRequest = new OperatorRequest("Fiber Net");

        operator = new Operator();
        operator.setId(1L);
        operator.setName("Fiber Net");

        operatorResponse = new OperatorResponse(1L, "Fiber Net");

        planRequest = new PlanRequest("Fiber 10", BigDecimal.valueOf(40.00), ServiceType.INTERNET,
                PlanStatus.ACTIVE, null, LocalDateTime.now(), operator.getId());

        plan = new Plan();
        plan.setId(1L);
        plan.setName("Fiber 10");
        plan.setPrice(BigDecimal.valueOf(40.00));
        plan.setServiceType(ServiceType.INTERNET);
        plan.setStatus(PlanStatus.ACTIVE);
        plan.setDataLimit(null);
        plan.setCreatedAt(LocalDateTime.now());
        plan.setOperator(operator);

        planResponse = new PlanResponse(1L, "Fiber 10", BigDecimal.valueOf(40.00), ServiceType.INTERNET,
                PlanStatus.ACTIVE, null, LocalDateTime.now(), operatorResponse);

        subscriptionRequest = new SubscriptionRequest(1L, 1L,
                LocalDateTime.now(), SubscriptionStatus.ACTIVE, null);

        subscription = new Subscription();
        subscription.setId(1L);
        subscription.setCustomer(customer);
        subscription.setPlan(plan);
        subscription.setSubscribedDate(LocalDateTime.now());
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setCancelledDate(null);

        subscriptionResponse = new SubscriptionResponse(1L, 1L, planResponse,
                LocalDateTime.now(), SubscriptionStatus.ACTIVE, null);
    }

    @Test
    @DisplayName("create() should save the subscription and return the response")
    void create_success() {
        //Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(subscriptionRepository.existsByCustomerIdAndPlanServiceTypeAndStatus(
                1L, ServiceType.INTERNET, SubscriptionStatus.ACTIVE)).thenReturn(false);
        when(mapper.toEntity(subscriptionRequest)).thenReturn(subscription);
        when(subscriptionRepository.save(subscription)).thenReturn(subscription);
        when(mapper.toResponse(subscription)).thenReturn(subscriptionResponse);

        //Act
        SubscriptionResponse result = service.create(subscriptionRequest);

        //Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);

        //Verify
        verify(subscriptionRepository).save(subscription);
    }

    @Test
    @DisplayName("create() should throw IllegalArgumentException when request is null")
    void create_nullRequest() {

        assertThatThrownBy(() -> service.create(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Subscription Request cannot be null");
    }

    @Test
    @DisplayName("create() should throw SubscriptionAlreadyExistsException when name already exists")
    void create_duplicatePlan() {
        //Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(subscriptionRepository.existsByCustomerIdAndPlanServiceTypeAndStatus(
                1L, ServiceType.INTERNET, SubscriptionStatus.ACTIVE)).thenReturn(true);

        //Assert & Action
        assertThatThrownBy(() -> service.create(subscriptionRequest))
                .isInstanceOf(SubscriptionAlreadyExistsException.class)
                .hasMessageContaining("Customer already has an active subscription for INTERNET");

        //Verify that operatorRepository.save() was never called with any Operator object
        verify(subscriptionRepository, never()).save(any(Subscription.class));
    }

    @Test
    @DisplayName("viewSubscriptionByCustomer() should return list of SubscriptionResponse")
    void viewSubscriptionByCustomer_success() {
        // Arrange
        List<Subscription> subscriptions = Arrays.asList(subscription);
        when(customerRepository.findByEmail("rose@example.com")).thenReturn(Optional.of(customer));
        when(subscriptionRepository.findByCustomerId(1L)).thenReturn(subscriptions);
        when(mapper.toResponse(subscription)).thenReturn(subscriptionResponse);

        // Act
        List<SubscriptionResponse> result = service.viewSubscriptionByCustomer("rose@example.com");

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("cancelSubscription() should cancel a subscription if the subscription exists")
    void cancelSubscription_success() {

        //Arrange
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));

        //Act
        service.cancelSubscription(1L);

        //Assert
        verify(subscriptionRepository).save(subscription);
    }

    @Test
    @DisplayName("cancelSubscription() should throw IllegalArgumentException when ID is null")
    void cancelSubscription_nullId() {
        // Act & Assert
        assertThatThrownBy(() -> service.cancelSubscription(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Id cannot be null");
    }

    @Test
    @DisplayName("cancelSubscription() should throw ResourceNotFoundException when user not found")
    void cancelSubscription_notFound() {
        // Arrange
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.cancelSubscription(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Subscription not found with Id: 1");

        //Assert
        verify(subscriptionRepository, never()).delete(any());
    }
}
