package org.animefoda.user.entities.user;

import jakarta.persistence.*;
import lombok.Getter;
import org.animefoda.user.entities.role.Role;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "users", schema = "users")
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String name;

    @Column
    private String surname;

    @Column(unique = true)
    private String username;

    @Column(name = "birthdate")
    private Date birthDate;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String salt;

    @Column(name = "superuser")
    private Boolean superUser;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            schema = "users",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    private Set<Role> roles;

}
