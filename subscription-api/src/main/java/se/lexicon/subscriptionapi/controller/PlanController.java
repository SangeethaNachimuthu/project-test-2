package se.lexicon.subscriptionapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import se.lexicon.subscriptionapi.dto.request.PlanRequest;
import se.lexicon.subscriptionapi.dto.response.PlanResponse;
import se.lexicon.subscriptionapi.service.PlanService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/plans")
@Tag(name = "Plans", description = "APIs for managing plans")
public class PlanController {

    private final PlanService planService;

    @Autowired
    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @PostMapping
    @Operation(summary = "Create a new plan")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanResponse> create(@Valid @RequestBody PlanRequest request) {

        PlanResponse response = planService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "View all plans")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PlanResponse>> viewAll() {

        List<PlanResponse> plans = planService.viewAll();
        return ResponseEntity.ok(plans);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a Plan")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanResponse> updatePlan(@Valid @PathVariable Long id, @RequestBody PlanRequest request) {

        PlanResponse response = planService.updatePlan(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Plan")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {

        planService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "View all active plans")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<PlanResponse>> viewAllActivePlans() {

        List<PlanResponse> plans = planService.findByStatus();

        return ResponseEntity.ok(plans);
    }
}
