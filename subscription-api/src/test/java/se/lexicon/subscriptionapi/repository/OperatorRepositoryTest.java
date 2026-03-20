package se.lexicon.subscriptionapi.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import se.lexicon.subscriptionapi.domain.entity.Operator;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class OperatorRepositoryTest {

    @Autowired
    private OperatorRepository operatorRepository;

    @Test
    @DisplayName("existsByName() should be true for existing name")
    public void existsByName_shouldBeTrue() {

        Operator operator = new Operator();
        operator.setName("Fiber Net");
        operatorRepository.save(operator);

        assertThat(operatorRepository.existsByName("Fiber Net")).isTrue();
        assertThat(operatorRepository.existsByName("Telia Net")).isFalse();
    }

    @Test
    @DisplayName("findByName() should be true for existing name")
    public void findByName_shouldBeTrue() {

        Operator operator = new Operator();
        operator.setName("Fiber Net");
        operatorRepository.save(operator);

        Optional<Operator> result = operatorRepository.findByName("Fiber Net");

        assertThat(result.get().getName()).isEqualTo("Fiber Net");
        assertThat(result.get().getName()).isNotEqualTo("Telia Net");
    }
}
