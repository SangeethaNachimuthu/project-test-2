package se.lexicon.subscriptionapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import se.lexicon.subscriptionapi.domain.constant.PlanStatus;
import se.lexicon.subscriptionapi.domain.constant.ServiceType;
import se.lexicon.subscriptionapi.domain.entity.Plan;
import se.lexicon.subscriptionapi.dto.response.PlanResponse;

import java.util.List;

@Repository
@Validated
public interface PlanRepository extends JpaRepository<Plan, Long> {

    boolean existsByNameIgnoreCase(String name);
    List<Plan> findByStatus(PlanStatus status);
    List<Plan> findByStatusAndServiceType(PlanStatus status, ServiceType serviceType);
    List<Plan> findByStatusAndOperatorId(PlanStatus status, Long operatorId);

}
