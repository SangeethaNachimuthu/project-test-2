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
import se.lexicon.subscriptionapi.domain.entity.Operator;
import se.lexicon.subscriptionapi.domain.entity.Plan;
import se.lexicon.subscriptionapi.dto.request.OperatorRequest;
import se.lexicon.subscriptionapi.dto.request.PlanRequest;
import se.lexicon.subscriptionapi.dto.response.OperatorResponse;
import se.lexicon.subscriptionapi.dto.response.PlanResponse;
import se.lexicon.subscriptionapi.exception.BusinessRuleException;
import se.lexicon.subscriptionapi.exception.ResourceNotFoundException;
import se.lexicon.subscriptionapi.mapper.PlanMapper;
import se.lexicon.subscriptionapi.repository.OperatorRepository;
import se.lexicon.subscriptionapi.repository.PlanRepository;
import se.lexicon.subscriptionapi.service.impl.PlanServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlanServiceImplTest {

    @Mock
    private PlanRepository planRepository;

    @Mock
    private OperatorRepository operatorRepository;

    @Mock
    private PlanMapper mapper;

    @InjectMocks
    private PlanServiceImpl planService;

    private Plan plan;
    private PlanRequest planRequest;
    private PlanResponse planResponse;
    private Operator operator;
    private OperatorRequest operatorRequest;
    private OperatorResponse operatorResponse;

    @BeforeEach
    void setUp() {

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
    }

    @Test
    @DisplayName("create() should save the plan and return the response")
    void create_success() {

        when(planRepository.existsByNameIgnoreCase(planRequest.name())).thenReturn(false);
        when(operatorRepository.findById(planRequest.operatorId())).thenReturn(Optional.ofNullable(operator));
        when(mapper.toEntity(planRequest)).thenReturn(plan);
        when(planRepository.save(plan)).thenReturn(plan);
        when(mapper.toResponse(plan)).thenReturn(planResponse);

        PlanResponse result = planService.create(planRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Fiber 10");

        // Verify interactions
        verify(planRepository).existsByNameIgnoreCase(planRequest.name());
        verify(mapper).toEntity(planRequest);
        verify(planRepository).save(plan);
        verify(mapper).toResponse(plan);
    }

    @Test
    @DisplayName("create() should throw IllegalArgumentException when request is null")
    void create_nullRequest() {

        assertThatThrownBy(() -> planService.create(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("PlanRequest cannot be null");
    }

    @Test
    @DisplayName("create() should throw BusinessRuleException when name already exists")
    void create_duplicateName() {
        //Arrange
        when(planRepository.existsByNameIgnoreCase(planRequest.name())).thenReturn(true);

        //Assert & Action
        assertThatThrownBy(() -> planService.create(planRequest))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Plan name already exists: Fiber 10");

        //Verify that operatorRepository.save() was never called with any Operator object
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    @DisplayName("findAll() should return list of PlanRequest")
    void findAll_success() {
        // Arrange
        List<Plan> plans = Arrays.asList(plan);
        when(planRepository.findAll()).thenReturn(plans);
        when(mapper.toResponse(plan)).thenReturn(planResponse);

        // Act
        List<PlanResponse> result = planService.viewAll();

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("deleteById should delete a plan if the plan exists")
    void deleteById_success() {

        //Arrange
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));

        //Act
        planService.deletePlan(1L);

        //Assert
        verify(planRepository).delete(plan);
    }

    @Test
    @DisplayName("deleteById() should throw IllegalArgumentException when ID is null")
    void deleteById_nullId() {
        // Act & Assert
        assertThatThrownBy(() -> planService.deletePlan(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Id cannot be null");
    }

    @Test
    @DisplayName("deleteById() should throw ResourceNotFoundException when user not found")
    void deleteById_notFound() {
        // Arrange
        when(planRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> planService.deletePlan(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Plan not found with id: 1");

        verify(planRepository, never()).delete(any());
    }

    @Test
    @DisplayName("findByStatus() should return PlanResponse when Plan exists")
    void findByStatus_found() {

        //Arrange
        when(planRepository.findByStatus(PlanStatus.ACTIVE)).thenReturn(Collections.singletonList(plan));
        when(mapper.toResponse(plan)).thenReturn(planResponse);

        //Act
        List<PlanResponse> result = planService.findByStatus();

        //Assert
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("viewActivePlansByServiceType should return list of PlanResponse when condition satisfies")
    void viewActivePlansByServiceType_found() {

        when(planRepository.findByStatusAndServiceType(PlanStatus.ACTIVE, ServiceType.INTERNET))
                .thenReturn(Collections.singletonList(plan));
        when(mapper.toResponse(plan)).thenReturn(planResponse);

        List<PlanResponse> result = planService.viewActivePlansByServiceType(ServiceType.INTERNET);

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("viewActivePlansByOperatorId should return list of PlanResponse when condition satisfies")
    void viewActivePlansByOperatorId_found() {

        when(operatorRepository.findById(1L)).thenReturn(Optional.of(operator));
        when(planRepository.findByStatusAndOperatorId(PlanStatus.ACTIVE, 1L))
                .thenReturn(Collections.singletonList(plan));
        when(mapper.toResponse(plan)).thenReturn(planResponse);

        List<PlanResponse> result = planService.viewActivePlansByOperatorId(1L);

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("updatePlan() should return updated planResponse when the id exists")
    void updatePlan_success() {

        //Arrange
        when(operatorRepository.findById(1L)).thenReturn(Optional.of(operator));
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        doNothing().when(mapper).updateEntity(planRequest, plan);

        when(planRepository.save(plan)).thenReturn(plan);
        when(mapper.toResponse(plan)).thenReturn(planResponse);

        //Act
        PlanResponse result = planService.updatePlan(1L, planRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);

        // Verify interactions
        verify(planRepository).findById(1L);
        verify(operatorRepository).findById(1L);
        verify(mapper).updateEntity(planRequest, plan);
        verify(planRepository).save(plan);
        verify(mapper).toResponse(plan);
    }

    @Test
    @DisplayName("updatePlan() should throw exception for null request")
    void updatePlan_nullRequest() {

        assertThatThrownBy(() -> planService.updatePlan(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Id or PlanRequest cannot be null");
    }
}
