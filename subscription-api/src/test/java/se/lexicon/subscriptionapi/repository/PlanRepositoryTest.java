package se.lexicon.subscriptionapi.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import se.lexicon.subscriptionapi.domain.constant.PlanStatus;
import se.lexicon.subscriptionapi.domain.constant.ServiceType;
import se.lexicon.subscriptionapi.domain.entity.Operator;
import se.lexicon.subscriptionapi.domain.entity.Plan;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@ActiveProfiles("test")
public class PlanRepositoryTest {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private OperatorRepository operatorRepository;

    @Test
    @DisplayName("existsByNameIgnoreCase() should be true for existing name")
    void existsByNameIgnoreCase_shouldBeTrue() {

        setOperatorAndPlanData();

        //ACT + Assert
        assertThat(planRepository.existsByNameIgnoreCase("Fiber 10")).isTrue();
        assertThat(planRepository.existsByNameIgnoreCase("Fiber 40")).isFalse();
    }

    @Test
    @DisplayName("findByStatus() should be true for either 'Active' or 'Inactive' status")
    void findByStatus_shouldBeTrue() {

        setOperatorAndPlanData();

        List<Plan> result = planRepository.findByStatus(PlanStatus.ACTIVE);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("findByStatusAndServiceType() should be true for specified status and service type")
    void findByStatusAndServiceType_shouldBeTrue() {

        setOperatorAndPlanData();
        List<Plan> result = planRepository.findByStatusAndServiceType(PlanStatus.ACTIVE, ServiceType.MOBILE);

        assertThat(result).hasSize(0);
    }

    @Test
    @DisplayName("findByStatusAndServiceType() should be true for specified status and operator id")
    void findByStatusAndOperatorId_shouldBeTrue() {

        setOperatorAndPlanData();
        List<Plan> result = planRepository.findByStatusAndOperatorId(PlanStatus.ACTIVE, 1L);

        assertThat(result).hasSize(2);
    }

    private void setOperatorAndPlanData() {

        Operator operator = new Operator();
        operator.setName("Fiber Net");
        operatorRepository.save(operator);

        Plan plan = new Plan();
        plan.setName("Fiber 10");
        plan.setPrice(BigDecimal.valueOf(40));
        plan.setServiceType(ServiceType.INTERNET);
        plan.setOperator(operator);
        plan.setStatus(PlanStatus.ACTIVE);
        plan.setCreatedAt(LocalDateTime.now());

        Plan plan1 = new Plan();
        plan1.setName("Fiber 50");
        plan1.setPrice(BigDecimal.valueOf(100));
        plan1.setServiceType(ServiceType.INTERNET);
        plan1.setOperator(operator);
        plan1.setStatus(PlanStatus.ACTIVE);
        plan1.setCreatedAt(LocalDateTime.now());

        planRepository.save(plan);
        planRepository.save(plan1);
    }

}
