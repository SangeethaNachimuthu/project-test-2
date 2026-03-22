package se.lexicon.subscriptionapi.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.lexicon.subscriptionapi.domain.entity.Operator;
import se.lexicon.subscriptionapi.dto.request.OperatorRequest;
import se.lexicon.subscriptionapi.dto.response.OperatorResponse;
import se.lexicon.subscriptionapi.exception.BusinessRuleException;
import se.lexicon.subscriptionapi.exception.ResourceNotFoundException;
import se.lexicon.subscriptionapi.mapper.OperatorMapper;
import se.lexicon.subscriptionapi.repository.OperatorRepository;
import se.lexicon.subscriptionapi.service.impl.OperatorServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OperatorServiceImplTest {

    @Mock
    private OperatorRepository operatorRepository;

    @Mock
    private OperatorMapper mapper;

    @InjectMocks
    private OperatorServiceImpl operatorService;

    private Operator operator;
    private OperatorRequest request;
    private OperatorResponse response;

    @BeforeEach
    void setUp() {
        request = new OperatorRequest("Fiber Net");

        operator = new Operator();
        operator.setId(1L);
        operator.setName("Fiber Net");

        response = new OperatorResponse(1L, "Fiber Net");
    }

    @Test
    @DisplayName("create() should save an operator and return response")
    void create_success() {

        when(operatorRepository.existsByName(request.name())).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(operator);
        when(operatorRepository.save(operator)).thenReturn(operator);
        when(mapper.toResponse(operator)).thenReturn(response);

        OperatorResponse result = operatorService.create(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Fiber Net");

        // Verify interactions
        verify(operatorRepository).existsByName(request.name());
        verify(mapper).toEntity(request);
        verify(operatorRepository).save(operator);
        verify(mapper).toResponse(operator);
    }

    @Test
    @DisplayName("create() should throw IllegalArgumentException when request is null")
    void create_nullRequest() {

        assertThatThrownBy(() -> operatorService.create(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("OperatorRequest cannot be null");
    }

    @Test
    @DisplayName("create() should throw BusinessRuleException when name already exists")
    void create_duplicateName() {
        //Arrange
        when(operatorRepository.existsByName(request.name())).thenReturn(true);

        //Assert & Action
        assertThatThrownBy(() -> operatorService.create(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Operator name already exists: " + request.name());

        //Verify that operatorRepository.save() was never called with any Operator object
        verify(operatorRepository, never()).save(any(Operator.class));
    }

    @Test
    @DisplayName("findById() should return OperatorResponse when Operator exists")
    void findById_found() {

        //Arrange
        when(operatorRepository.findById(1L)).thenReturn(Optional.of(operator));
        when(mapper.toResponse(operator)).thenReturn(response);

        //Act
        OperatorResponse result = operatorService.findById(1L);

        //Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("findById() should return empty response when Operator not found")
    void findById_notFound() {

        //Arrange
        when(operatorRepository.findById(1L)).thenReturn(Optional.empty());

        //Act
        assertThatThrownBy(() -> operatorService.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Operator not found with id: 1" );

        // Verify repository interaction
        verify(operatorRepository).findById(1L);
    }

    @Test
    @DisplayName("findByName() should return OperatorResponse when Operator exists")
    void findByName_found() {

        //Arrange
        when(operatorRepository.findByName("Fiber Net")).thenReturn(Optional.of(operator));
        when(mapper.toResponse(operator)).thenReturn(response);

        //Act
        OperatorResponse result = operatorService.findByName("Fiber Net");

        //Assert
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Fiber Net");
    }

    @Test
    @DisplayName("findByName() should return empty response when Operator not found")
    void findByName_notFound() {

        //Arrange
        when(operatorRepository.findByName("Fiber Net")).thenReturn(Optional.empty());

        //Act
        assertThatThrownBy(() -> operatorService.findByName("Fiber Net"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Operator not found with name: Fiber Net" );

        // Verify repository interaction
        verify(operatorRepository).findByName("Fiber Net");
    }

    @Test
    @DisplayName("findAll() should return list of OperatorRequest")
    void findAll_success() {
        // Arrange
        List<Operator> operators = Arrays.asList(operator);
        when(operatorRepository.findAll()).thenReturn(operators);
        when(mapper.toResponse(operator)).thenReturn(response);

        // Act
        List<OperatorResponse> result = operatorService.findAll();

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
    }
}
