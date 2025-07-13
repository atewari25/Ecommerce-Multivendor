package com.amit.model;

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
public class Wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne // one user can have only one wishlist
    private User user;

    @ManyToMany  // multiple user can add same product in their wishlist
    private Set<Product> products = new HashSet<>();
}
