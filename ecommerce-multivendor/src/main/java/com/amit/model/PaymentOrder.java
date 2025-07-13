package com.amit.model;

import com.amit.domain.PaymentMethod;
import com.amit.domain.PaymentOrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class PaymentOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long amount;

    private PaymentOrderStatus status = PaymentOrderStatus.PENDING;

    private PaymentMethod paymentMethod;

    private String paymentLinkId;

    @ManyToOne  // Many PaymentOrders can belong to one User
    private User user;

    @OneToMany  // One PaymentOrder can have multiple Orders
    private Set<Order> orders = new HashSet<>();

}
