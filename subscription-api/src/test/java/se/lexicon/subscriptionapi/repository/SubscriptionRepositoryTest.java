package se.lexicon.subscriptionapi.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import se.lexicon.subscriptionapi.domain.constant.PlanStatus;
import se.lexicon.subscriptionapi.domain.constant.ServiceType;
import se.lexicon.subscriptionapi.domain.constant.SubscriptionStatus;
import se.lexicon.subscriptionapi.domain.entity.Customer;
import se.lexicon.subscriptionapi.domain.entity.Operator;
import se.lexicon.subscriptionapi.domain.entity.Plan;
import se.lexicon.subscriptionapi.domain.entity.Subscription;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class SubscriptionRepositoryTest {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OperatorRepository operatorRepository;

    @Autowired
    private PlanRepository planRepository;

    @Test
    @DisplayName("existsByCustomerIdAndPlanServiceTypeAndStatus() should be true for the specified condition")
    public void existsByCustomerIdAndPlanServiceTypeAndStatus_shouldBeTrue() {

        Customer customer = subscriptionData();

        boolean result = subscriptionRepository.existsByCustomerIdAndPlanServiceTypeAndStatus(
                                customer.getId(),
                                ServiceType.MOBILE,
                                SubscriptionStatus.ACTIVE);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("findByCustomerId() should be true for existing customer")
    public void findByCustomerId_shouldBeTrue() {

        Customer customer = subscriptionData();
        List<Subscription> result = subscriptionRepository.findByCustomerId(customer.getId());
        assertThat(result).hasSize(2);
    }

    private Customer subscriptionData() {

        Customer rose = new Customer();
        rose.setFirstName("Rose");
        rose.setLastName("Mari");
        rose.setEmail("rose@example.com");
        rose.setPassword("password");
        Customer savedRose = customerRepository.save(rose);

        Operator fiberNet = new Operator();
        fiberNet.setName("Fiber Net");
        operatorRepository.save(fiberNet);

        Plan fiber10 = new Plan();
        fiber10.setName("Fiber 10");
        fiber10.setPrice(BigDecimal.valueOf(50.00));
        fiber10.setOperator(fiberNet);
        fiber10.setServiceType(ServiceType.INTERNET);
        fiber10.setStatus(PlanStatus.ACTIVE);
        planRepository.save(fiber10);

        Plan fiber50 = new Plan();
        fiber50.setName("Fiber 50");
        fiber50.setPrice(BigDecimal.valueOf(150.00));
        fiber50.setOperator(fiberNet);
        fiber50.setServiceType(ServiceType.INTERNET);
        fiber50.setStatus(PlanStatus.ACTIVE);
        planRepository.save(fiber50);

        Plan fiberMobPlus = new Plan();
        fiberMobPlus.setName("Fiber Mobile Plus");
        fiberMobPlus.setPrice(BigDecimal.valueOf(200.00));
        fiberMobPlus.setOperator(fiberNet);
        fiberMobPlus.setServiceType(ServiceType.MOBILE);
        fiberMobPlus.setDataLimit("50");
        fiberMobPlus.setStatus(PlanStatus.ACTIVE);
        planRepository.save(fiberMobPlus);

        Subscription subscription1 = new Subscription();
        subscription1.setCustomer(savedRose);
        subscription1.setPlan(fiber50);
        subscription1.setStatus(SubscriptionStatus.ACTIVE);
        subscriptionRepository.save(subscription1);

        Subscription subscription2 = new Subscription();
        subscription2.setCustomer(savedRose);
        subscription2.setPlan(fiberMobPlus);
        subscription2.setStatus(SubscriptionStatus.ACTIVE);
        subscriptionRepository.save(subscription2);

        return savedRose;
    }
}
