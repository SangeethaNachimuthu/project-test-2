package se.lexicon.subscriptionapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import se.lexicon.subscriptionapi.domain.entity.Plan;

@Repository
@Validated
public interface PlanRepository extends JpaRepository<Plan, Long> {
    boolean existsByNameIgnoreCase(String name);
}
