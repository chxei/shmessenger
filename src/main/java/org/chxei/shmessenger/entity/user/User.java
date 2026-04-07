package org.chxei.shmessenger.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@ToString
@Entity(name = "users")
@DynamicInsert
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "CONSTRAINT_UNIQUE_USERS_USERNAME", columnNames = "username"),
                @UniqueConstraint(name = "CONSTRAINT_UNIQUE_USERS_EMAIL", columnNames = "email"),
                @UniqueConstraint(name = "CONSTRAINT_UNIQUE_USERS_PHONE", columnNames = "phone"),
        }

)
public final class User implements UserDetails, CredentialsContainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty
    //@Column(unique = true)
    private String username;

    @NotEmpty
    private String name;

    @JsonIgnore
    @CreationTimestamp
    private Timestamp creationTimeStamp;

    @JsonIgnore
    @UpdateTimestamp
    private Timestamp updateTimestamp;

    @ManyToOne
    private Country country;

    @Email
    //@Column//(unique = true)
    private String email;

    //@Column(unique = true)
    private String phone;

    private Timestamp birthDate;
    private String pathToProfilePicture;
    private String pathToBackgroundPicture;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private boolean active = true;

    private boolean isVerified = false;

    @ManyToOne(cascade = {CascadeType.MERGE})
    private Gender gender;

    //todo make separate table for authorities
    @Transient
    private List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("app");

    public User(UserDetails userDetails) {
        this.username = userDetails.getUsername();
        this.password = userDetails.getPassword();
        this.authorities = AuthorityUtils.createAuthorityList("app");
    }

    public User(String username, String name, String email, Timestamp birthDate, String password, Gender gender) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
        this.password = password;
        this.gender = gender;
        this.authorities = AuthorityUtils.createAuthorityList("app");
    }

    public User(String username, boolean active, String password) {
        this.username = username;
        this.password = password;
        this.active = active;
        this.authorities = Stream.of(this.getRole().name())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public User() {
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
        return active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return username != null && username.equals(user.getUsername());
    }

    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : getClass().hashCode();
    }

    //do not change order, add new values last
    public enum Role {
        USER,
        ADMIN
    }
}
