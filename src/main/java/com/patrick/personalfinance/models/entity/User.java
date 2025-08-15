package com.patrick.personalfinance.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@With
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user", schema = "finance_tables")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_user")
    private UUID id;

    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(columnDefinition = "TEXT")
    private String photo;

    @CreationTimestamp
    private LocalDateTime registrationDate;

    private LocalDateTime inactivationDate;

    @OneToMany(mappedBy = "user")
    private List<Title> titles;

    @OneToMany(mappedBy = "user")
    private List<CenterCost> centerCosts;

    //#region Framework Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return inactivationDate == null;
    }
    //#endregion
}
