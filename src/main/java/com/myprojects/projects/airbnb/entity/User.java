package com.myprojects.projects.airbnb.entity;

import com.myprojects.projects.airbnb.entity.enums.Gender;
import com.myprojects.projects.airbnb.entity.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Table(name="app_user")   // because of the reserved word "user" in Postgresql
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true, nullable=false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;

    private LocalDate dateOfBirth;
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_"+role.name()))
                .collect(Collectors.toSet());
    }
    @Override
    public String getUsername() {
        return email;
    }
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(getId(),user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
