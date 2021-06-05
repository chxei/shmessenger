package org.chxei.shmessenger.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
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
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty
    @Column(unique = true)
    private String username;

    @NotEmpty
    private String name;

    //@ColumnDefault("now()")
    @CreationTimestamp
    private Timestamp creationTimeStamp;

    @UpdateTimestamp
    private Timestamp updateTimestamp;

    @ManyToOne
    private Country country;

    @Email
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String phone;
    //@JsonDeserialize(using= OptimizedTimestampDeserializer.class)
    private Timestamp birthDate;
    private String pathToProfilePicture;
    private String pathToBackgroundPicture;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    private boolean active = true;
    private boolean isVerified = false;

    @ManyToOne
    private Gender gender;

    //todo make separate table for authorities
    @Transient
    private List<GrantedAuthority> authorities;

    public User(String username, String name, String email, Timestamp birthDate, String password, Gender gender) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
        this.password = password;
        this.gender = gender;
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

//    @PrePersist
//    public void setCreationDateTime() {
//        this.creationTimeStamp = LocalDateTime.now();
//    }
//
//    @PreUpdate
//    public void setChangeDateTime() {
//        this.modifiedTimestamp = LocalDateTime.now();
//    }

    //do not change order, add new values last
    public enum Role {
        USER,
        ADMIN
    }

    @Override
    public int hashCode() {
        return 562048007;
    }
}

