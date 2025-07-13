package com.amit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnore
    @ManyToOne
    private Order order;  // many items can be part of one order

    @ManyToOne
    private Product product; // Both you and other customers can order the same shirt (the same Product).

    private String size; // XL, L , M etc

    private int quantity;

    private Integer mrpPrice;

    private Integer sellingPrice;

    private Long userId;


}
