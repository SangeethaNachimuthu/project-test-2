package se.lexicon.subscriptionapi.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import se.lexicon.subscriptionapi.domain.constant.SubscriptionStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Column(nullable = false, updatable = false)
    private LocalDateTime subscribedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;

    private LocalDateTime cancelledDate;

    @PrePersist
    public void prePersist() {
        subscribedDate = LocalDateTime.now();
    }

    public void cancel() {

        this.status = SubscriptionStatus.CANCELLED;
        this.cancelledDate = LocalDateTime.now();
    }
}
