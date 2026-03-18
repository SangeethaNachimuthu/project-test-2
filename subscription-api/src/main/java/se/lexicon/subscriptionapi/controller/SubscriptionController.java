package se.lexicon.subscriptionapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.lexicon.subscriptionapi.dto.request.SubscriptionRequest;
import se.lexicon.subscriptionapi.dto.response.SubscriptionResponse;
import se.lexicon.subscriptionapi.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
@Tag(name = "Subscriptions", description = "APIs for managing Subscriptions")
public class SubscriptionController {

    private final SubscriptionService service;

    @Autowired
    public SubscriptionController(SubscriptionService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Subscribe a Plan")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SubscriptionResponse> create(@RequestBody SubscriptionRequest request) {

        SubscriptionResponse response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/my")
    @Operation(summary = "View their own subscriptions")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<SubscriptionResponse>> viewSubscriptionByCustomer(Authentication authentication) {

        String email = authentication.getName();
        List<SubscriptionResponse> subscriptions = service.viewSubscriptionByCustomer(email);
        return ResponseEntity.ok(subscriptions);
    }
}
