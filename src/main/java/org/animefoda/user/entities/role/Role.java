package org.animefoda.user.entities.role;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "role", schema = "users")
@Getter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;
}
